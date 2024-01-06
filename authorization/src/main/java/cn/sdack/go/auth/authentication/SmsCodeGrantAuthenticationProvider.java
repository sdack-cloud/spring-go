package cn.sdack.go.auth.authentication;

/**
 * @author sdack
 * @date 2023/12/28
 */

import cn.sdack.go.auth.config.SmsNames;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SmsCodeGrantAuthenticationProvider implements AuthenticationProvider {
    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final UserDetailsService userDetailsService;
    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    public SmsCodeGrantAuthenticationProvider(OAuth2AuthorizationService authorizationService,UserDetailsService userDetailsService
            , StringRedisTemplate redisTemplate, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.userDetailsService = userDetailsService;
        this.authorizationService = authorizationService;
        this.stringRedisTemplate = redisTemplate;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeGrantAuthenticationToken smsCodeGrantAuthentication =
                (SmsCodeGrantAuthenticationToken) authentication;

        // 确保客户端已经过身份验证
        OAuth2ClientAuthenticationToken clientPrincipal =
                getAuthenticatedClientElseThrowInvalidClient(smsCodeGrantAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 确保将客户端配置为使用此授权授予类型
        if (!registeredClient.getAuthorizationGrantTypes().contains(smsCodeGrantAuthentication.getGrantType())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        // 表单参数
        Map<String, Object> additionalParameters = smsCodeGrantAuthentication.getAdditionalParameters();
        String username = (String) additionalParameters.get(OAuth2ParameterNames.USERNAME);
        String code = (String) additionalParameters.get(OAuth2ParameterNames.CODE);
        String scopeStr = (String) additionalParameters.get(OAuth2ParameterNames.SCOPE);
        Set<String> scopeSet = Arrays.stream(scopeStr.split(" ")).collect(Collectors.toSet());
        for (String scope : scopeSet) {
            Optional<String> first = registeredClient.getScopes().stream().filter(it -> it.equals(scope.trim())).findFirst();
            if (first.isEmpty()) {
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
            }
        }
        Integer expires = null;
        try {
            expires = Integer.parseInt(String.valueOf(additionalParameters.get(OAuth2ParameterNames.EXPIRES_IN)));
        }catch (Exception ignored) {}

        String key = SmsNames.PREFIX + username;
        String smsCode = stringRedisTemplate.opsForValue().get(key);
        if (smsCode == null) {
            throw new OAuth2AuthenticationException("短信验证码不存在");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new OAuth2AuthenticationException("短信验证码不正确");
        }
        if (!smsCode.equals(code)) {
            throw new OAuth2AuthenticationException("短信验证码不正确");
        }
        stringRedisTemplate.delete(key);
        // 生成 the access token

        if (expires != null) {
            TokenSettings.Builder tokenSettingBuilder = TokenSettings.builder();
            tokenSettingBuilder.reuseRefreshTokens(true)
                    .accessTokenTimeToLive(Duration.ofMinutes(expires));
            registeredClient = RegisteredClient.from(registeredClient)
                    .tokenSettings(tokenSettingBuilder.build()).build();
        }

        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(clientPrincipal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(smsCodeGrantAuthentication.getGrantType())
                .authorizationGrant(smsCodeGrantAuthentication)
                .authorizedScopes(scopeSet)
                .build();

        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", null);
            throw new OAuth2AuthenticationException(error);
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(), scopeSet);
        // Initialize the OAuth2Authorization
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(clientPrincipal.getName())
                .authorizationGrantType(smsCodeGrantAuthentication.getGrantType());
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) ->
                    metadata.put(
                            OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                            ((ClaimAccessor) generatedAccessToken).getClaims())
            );
        } else {
            authorizationBuilder.accessToken(accessToken);
        }
        OAuth2Authorization authorization = authorizationBuilder.build();

        // Save the OAuth2Authorization
        this.authorizationService.save(authorization);

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

}

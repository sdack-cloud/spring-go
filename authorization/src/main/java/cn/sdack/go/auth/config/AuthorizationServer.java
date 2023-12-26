package cn.sdack.go.auth.config;

import cn.sdack.go.auth.dao.AccountDao;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * @author sdack
 * @date 2023/12/23
 */

@Configuration(proxyBeanMethods = false)
public class AuthorizationServer {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    AccountDao accountDao;


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http
            ,AuthorizationServerSettings authorizationServerSettings) throws Exception {
        http.cors(Customizer.withDefaults());

        // OAuth2 使用授权服务器的安全配置默认功能
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);


        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(endpoint ->
                        endpoint.consentPage("/oauth2/consent")
                )
                .oidc(Customizer.withDefaults());

        http
                .exceptionHandling(
                        exceptions ->
                                exceptions.defaultAuthenticationEntryPointFor(
                                        new LoginUrlAuthenticationEntryPoint("/login"),
                                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                                )
                )
                ;
        return http.build();
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator(JWKSource<SecurityContext> jwkSource,
                                                  OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }


    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwsHeader.Builder headers = context.getJwsHeader();
            JwtClaimsSet.Builder claims = context.getClaims();
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                // TODO 自定义 access_token 的 headers/claims
                claims.claim("abc","abc");
                claims.claim("authorities","authorities");
            } else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
                // TODO 自定义 id_token 的 headers/claims
                claims.claim("qwe","qwe");

            }
        };
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9999")
                .authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/token")
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                .tokenRevocationEndpoint("/oauth2/revoke")
                .jwkSetEndpoint("/oauth2/jwks")
                .oidcUserInfoEndpoint("/userinfo")
                .oidcClientRegistrationEndpoint("/connect/register")
                .deviceVerificationEndpoint("/oauth2/device_verification")
                .deviceAuthorizationEndpoint("/oauth2/device_authorization")
                .build();
    }

}

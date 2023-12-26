package cn.sdack.go.auth.config;

import cn.sdack.go.auth.dao.AccountDao;
import cn.sdack.go.auth.dao.JpaRegisteredClientRepository;
import cn.sdack.go.auth.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.Optional;
import java.util.UUID;

/**
 * @date 2023/12/23
 * @author sdack
 */
@Configuration
public class CustomConfiguration {

    @Autowired
    JpaRegisteredClientRepository registeredClientRepository;

    @Autowired
    AccountDao accountDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);

        RegisteredClient RClient = registeredClientRepository.findByClientId("sdack");
        if (RClient == null) {
            RegisteredClient registeredClient = RegisteredClient.withId("123456789")
                    .clientId("sdack")
                    .clientName("SDACK")
                    .clientSecret(passwordEncoder.encode("4ca2c1406483031a7c4df8407c"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)

                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
                    .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                    .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                    .redirectUri("http://127.0.0.1:8080/authorized")
                    .postLogoutRedirectUri("http://127.0.0.1:8080/logged-out")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("message.read")
                    .scope("message.write")
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                    .build();

            registeredClientRepository.save(registeredClient);
        }

        Optional<AccountEntity> optional = accountDao.findByAccount("sdack");
        if (optional.isEmpty()) {
            AccountEntity entity = new AccountEntity();
            entity.account = "sdack";
            entity.name = "sdack";
            entity.nickname = "sdack";
            entity.pwd = passwordEncoder.encode("123456");
            entity.issActive = true;
            accountDao.save(entity);
        }

        return () -> 42;
    }



}

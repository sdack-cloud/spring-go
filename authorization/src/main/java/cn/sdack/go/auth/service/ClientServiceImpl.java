package cn.sdack.go.auth.service;

import cn.sdack.go.auth.dao.ClientDao;
import cn.sdack.go.auth.dao.JpaRegisteredClientRepository;
import cn.sdack.go.auth.entity.ClientEntity;
import cn.sdack.go.auth.query.ClientQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * @author sdack
 * @date 2023/12/24
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientDao clientDao;

    @Autowired
    JpaRegisteredClientRepository jpaRegisteredClientRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ClientEntity getByClientId(String clientId) throws IllegalAccessException {
        Optional<ClientEntity> oEntity = clientDao.findByClientId(clientId);
        if (oEntity.isEmpty()) {
            throw new IllegalAccessException("数据不存在");
        }
        return oEntity.get();
    }

    @Transactional
    @Override
    public void save(ClientQuery param) throws IllegalAccessException {
        Optional<ClientEntity> optional = clientDao.findByClientName(param.getClientName());
        if (optional.isPresent()) {
            throw new IllegalAccessException("客户端名称已经存在");
        }
        String secret = UUID.randomUUID().toString().replace("-", "");
        String clientId = createClientId();

        RegisteredClient.Builder builder = RegisteredClient.withId(secret);
        ClientSettings.Builder builder1 = ClientSettings.builder();
        TokenSettings.Builder builder2 = TokenSettings.builder();

        builder1.requireAuthorizationConsent(param.getIssConsent() == 1);
        builder2.reuseRefreshTokens(param.getIssRefresh() == 1)
                .refreshTokenTimeToLive(Duration.ofMinutes(param.getExpRefresh()))
                .accessTokenTimeToLive(Duration.ofMinutes(param.getExpAccess()));

        builder
                .clientId(clientId)
                .clientName(param.getClientName().trim())
                .clientSecret(passwordEncoder.encode(secret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(builder1.build())
                .tokenSettings(builder2.build());

        if (!param.getGrantTypes().isEmpty()) {
            param.getGrantTypes().stream().filter("refresh_token"::equals).findAny().ifPresent(it -> {
                builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
            });
            param.getGrantTypes().stream().filter("device_code"::equals).findAny().ifPresent(it -> {
                builder.authorizationGrantType(AuthorizationGrantType.DEVICE_CODE);
            });
            param.getGrantTypes().stream().filter("client_credentials"::equals).findAny().ifPresent(it -> {
                builder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
            });
            param.getGrantTypes().stream().filter("jwt_bearer"::equals).findAny().ifPresent(it -> {
                builder.authorizationGrantType(AuthorizationGrantType.JWT_BEARER);
            });
        }
        if (!param.getMethods().isEmpty()) {
            param.getMethods().stream().filter("client_secret_post"::equals).findAny().ifPresent(it -> {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
            });
            param.getMethods().stream().filter("client_secret_jwt"::equals).findAny().ifPresent(it -> {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
            });
            param.getMethods().stream().filter("private_key_jwt"::equals).findAny().ifPresent(it -> {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
            });
        }
        for (String redirectUris : param.getRedirectUris()) {
            builder.redirectUri(redirectUris);
        }
        for (String scope : param.getScopes()) {
            builder.scope(scope);
        }

        RegisteredClient registeredClient = builder.build();

        jpaRegisteredClientRepository.save(registeredClient);
        clientDao.updateSecretByClientId(clientId,secret);
    }

    @Transactional
    @Override
    public String updateSecret(String clientId) throws IllegalAccessException {
        String secret = UUID.randomUUID().toString().replace("-", "");
        Optional<ClientEntity> optional = clientDao.findByClientId(clientId);
        if (optional.isEmpty()) {
            throw new IllegalAccessException("客户端不存在");
        }
        ClientEntity clientEntity = optional.get();
        clientEntity.setClientSecret(passwordEncoder.encode(secret));
        clientEntity.setSecret((secret));
        RegisteredClient registeredClient = jpaRegisteredClientRepository.toObject(clientEntity);
        jpaRegisteredClientRepository.save(registeredClient);
        clientDao.updateSecretByClientId(clientId,secret);
        return secret;
    }


    String createClientId() {
        String id = String.valueOf(Math.random()).substring(2, 10);
        Optional<ClientEntity> optional = clientDao.findByClientId(id);
        if (optional.isPresent()) {
            return createClientId();
        }
        return id;
    }
}

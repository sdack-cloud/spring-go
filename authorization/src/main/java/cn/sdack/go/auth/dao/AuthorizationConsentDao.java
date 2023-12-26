package cn.sdack.go.auth.dao;

import cn.sdack.go.auth.entity.AuthorizationConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author sdack
 * @date 2023/12/23
 */
@Repository
public interface AuthorizationConsentDao extends JpaRepository<AuthorizationConsentEntity, String> {

    Optional<AuthorizationConsentEntity> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}

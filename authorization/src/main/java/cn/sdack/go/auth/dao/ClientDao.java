package cn.sdack.go.auth.dao;

import cn.sdack.go.auth.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author sdack
 * @date 2023/12/23
 */
@Repository
public interface ClientDao extends JpaRepository<ClientEntity, String> {

    Optional<ClientEntity> findByClientId(String clientId);

    Optional<ClientEntity> findByClientName(String clientName);

    @Modifying
    @Query("update ClientEntity a set a.secret = :secret where a.clientId = :clientId")
    int updateSecretByClientId(String clientId,String secret);
}

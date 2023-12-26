package cn.sdack.go.auth.dao;

import cn.sdack.go.auth.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author sdack
 * @date 2023/12/23
 */
@Repository
public interface AccountDao extends JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor<AccountEntity> {

    Optional<AccountEntity> findByAccount(String account);

    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findByMobile(Long mobile);

}

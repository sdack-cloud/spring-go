package cn.sdack.go.users.dao;

import cn.sdack.go.common.entities.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author sdack
 * @date 2024/1/4
 */
@Repository
public interface UserDao extends JpaRepository<UserEntity,Long> {

    UserEntity findByAccount(String account);

}

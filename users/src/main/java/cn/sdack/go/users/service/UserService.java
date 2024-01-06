package cn.sdack.go.users.service;

import cn.sdack.go.common.entities.users.UserEntity;

/**
 * @author sdack
 * @date 2024/1/4
 */
public interface UserService {

    UserEntity userinfo(String account);

}

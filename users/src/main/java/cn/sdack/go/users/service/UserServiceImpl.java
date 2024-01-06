package cn.sdack.go.users.service;

import cn.sdack.go.common.entities.users.UserEntity;
import cn.sdack.go.users.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sdack
 * @date 2024/1/4
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao usersDao;


    @Override
    public UserEntity userinfo(String account) {
        UserEntity userEntity = usersDao.findByAccount(account);
        if (userEntity != null) {
            userEntity.setPhone("");
            userEntity.setEmail("");
        }
        return userEntity;
    }


}

package cn.sdack.go.users.controller;

import cn.sdack.go.common.entities.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sdack
 * @date 2023/12/26
 */
@RestController
public class UserController {

//    @PreAuthorize("hasAnyAuthority('')")
    @GetMapping({"/index","/authority"})
    public String index() {


        UserEntity userEntity = new UserEntity();

        return "ok";
    }

}

package cn.sdack.go.users.controller;

import cn.sdack.go.common.entities.JsonResult;
import cn.sdack.go.common.entities.users.UserEntity;
import cn.sdack.go.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sdack
 * @date 2023/12/26
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;



//    @PreAuthorize("hasAnyAuthority('')")
    @GetMapping("/userinfo")
    public JsonResult<UserEntity> userinfo(@RequestParam(name = "u") String account) {

        try {
            UserEntity userinfo = userService.userinfo(account);
            return JsonResult.toJson(userinfo);
        } catch (Exception e) {
            String message = e.getMessage().toString();
            if (message.length() > 200) {
                message = message.substring(0,200);
            }
            return JsonResult.toJson(false,message);
        }
    }

}

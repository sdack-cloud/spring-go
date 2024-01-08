package cn.sdack.go.users.controller;

import cn.sdack.go.common.entities.JsonResult;
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


    @GetMapping("/")
    public JsonResult index(@RequestParam(name = "q") String q) {

        return JsonResult.toJson(false);
    }


}

package cn.sdack.go.auth.controller;

import cn.sdack.go.auth.entity.JsonResult;
import cn.sdack.go.auth.query.ClientQuery;
import cn.sdack.go.auth.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author sdack
 * @date 2023/12/24
 */

@RestController
public class ClientController {

    @Autowired
    ClientService clientService;

    @GetMapping("/test")
    public JsonResult test(@RequestParam(name = "q") String q) {
        JsonResult jsonResult = JsonResult.toJson(true);
        return jsonResult;
    }

    @PostMapping("/client")
    public JsonResult save(@RequestBody @Validated(ClientQuery.Add.class) ClientQuery param, BindingResult errors) {
        if (errors.hasErrors()){
            return JsonResult.toJson(false,errors.getAllErrors().get(0).getDefaultMessage());
        }
        try {
            clientService.save(param);
            return JsonResult.toJson(true);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.length() > 100) {
                message = message.substring(0,80);
            }
            return JsonResult.toJson(false,message);
        }
    }

    @GetMapping("/client/resetSecret")
    public JsonResult updateSecret(@RequestParam("clientId") String clientId) {
        try {
            String secret = clientService.updateSecret(clientId);
            return JsonResult.toJson(secret);
        }catch (Exception e) {
            String message = e.getMessage();
            if (message.length() > 100) {
                message = message.substring(0,80);
            }
            return JsonResult.toJson(false,message);
        }
    }


}

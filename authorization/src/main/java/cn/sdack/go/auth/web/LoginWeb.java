package cn.sdack.go.auth.web;

import cn.hutool.core.util.ReUtil;
import cn.sdack.go.auth.entity.AccountEntity;
import cn.sdack.go.auth.query.RegisterQuery;
import cn.sdack.go.auth.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sdack
 * @date 2023/12/23
 */
@Controller
public class LoginWeb {

    @Autowired
    AccountService accountService;


    @RequestMapping(value = {"/", "/index"})
    public String welcome(HttpSession session, Model model) {
//        String username = principal.getName();
//        System.out.println(principal.toString());
//        AccountEntity account = (AccountEntity) user.;
//        model.addAttribute("user", account);
        return "welcome";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @RequestMapping("/success")
    public String success(HttpSession session,Model model) {
        Object request = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (request == null) {
            return "redirect:/login";
        }
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request;
        String[] msg = defaultSavedRequest.getParameterValues("msg");
        if (msg.length > 0) {
            model.addAttribute("msg",msg[0]);
        }
        return "success";
    }


    @GetMapping("/login")
    public String login(HttpSession session) {
        return "login";
    }

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String registe(@Validated RegisterQuery param, BindingResult errors,Model model) {
        if (errors.hasErrors()){
            model.addAttribute("error",errors.getAllErrors().get(0).getDefaultMessage());
        } else {
            if (param.getEmail().isEmpty() && param.getMobile() < 100000) {
                model.addAttribute("error","邮箱或账号选择一个注册");
                return "register";
            }
            if (!param.getEmail().isEmpty()) {
                String reEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
                boolean isEmail = ReUtil.isMatch(reEmail, param.getEmail());
                if (!isEmail) {
                    model.addAttribute("error", "邮箱格式不正确");
                    return "register";
                }
            }
            String rePwd1 = "^[\\u4e00-\\u9fa5]+$";
            boolean isPwd1 = ReUtil.isMatch(rePwd1, param.getPassword());
            if (isPwd1) {
                model.addAttribute("error","密码 格式不正确");
                return "register";
            }

        }

        try {
            AccountEntity register = accountService.register(param);
            return "login";
        }catch (Exception e) {
            String message = e.getMessage();
            if (message.length() > 100) {
                message = message.substring(0,80);
            }
            model.addAttribute("error",message);
            return "register";
        }
    }


}

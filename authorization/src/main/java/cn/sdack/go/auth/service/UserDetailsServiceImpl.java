package cn.sdack.go.auth.service;

import cn.hutool.core.util.ReUtil;
import cn.sdack.go.auth.dao.AccountDao;
import cn.sdack.go.auth.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author sdack
 * @date 2023/12/23
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserDetailsPasswordService {

    @Autowired
    AccountDao accountDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

//    @Autowired
    public PasswordEncoder passwordEncoder;


    String reEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    String reMobile = "^((13[0-9])|(14[5,7])|(15[0-9])|(16[6])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {

        if (account.isBlank()) {
            throw new UsernameNotFoundException("用户不存在");
        }
        AccountEntity user = null;

        boolean isEmail = ReUtil.isMatch(reEmail, account);
        if (isEmail) {
            Optional<AccountEntity> optional = accountDao.findByEmail(account);
            if (optional.isPresent()) {
                user = optional.get();
            }
        }

        boolean isMobile = ReUtil.isMatch(reMobile, account);
        if (isMobile) {
            try {
                Optional<AccountEntity> optional = accountDao.findByMobile(Long.parseLong(account));
                if (optional.isPresent()) {
                    user = optional.get();
                }
            } catch (Exception ignored) {}
        }

        if (user == null) {
            Optional<AccountEntity> optional = accountDao.findByAccount(account);
            if (optional.isPresent()) {
                user = optional.get();
            }
        }
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BadCredentialsException("密码未设置,请先重置密码");
        }

        return user;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String username = user.getUsername();
        Optional<AccountEntity> optional = accountDao.findByAccount(username);
        if (optional.isPresent()) {
            AccountEntity accountEntity = optional.get();
            String encode = passwordEncoder.encode(newPassword);
            accountEntity.setPwd(encode);
        }
        return user;
    }
}

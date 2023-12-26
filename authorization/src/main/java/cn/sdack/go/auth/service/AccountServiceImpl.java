package cn.sdack.go.auth.service;

import cn.sdack.go.auth.dao.AccountDao;
import cn.sdack.go.auth.entity.AccountEntity;
import cn.sdack.go.auth.query.RegisterQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author sdack
 * @date 2023/12/24
 */
@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    AccountDao accountDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    List<Integer> k = Arrays.asList(133, 149, 153, 173, 177, 180, 181, 189, 190, 191, 193, 199, 134, 135, 136, 137, 138, 139,
            147, 148, 150, 151, 152, 157, 157, 159, 172, 130, 131, 132, 145, 155, 156, 166, 167, 171, 175, 176, 185, 186, 196);

    Random random = new Random();

    @Transactional
    @Override
    public AccountEntity register(RegisterQuery param) throws IllegalAccessException {
        if (!param.getEmail().isEmpty()) {
            Optional<AccountEntity> optional = accountDao.findByEmail(param.getEmail());
            if (optional.isPresent()) {
                throw new IllegalAccessException("邮箱已存在");
            }
        }
        if (param.getMobile() > 100000) {
            Optional<AccountEntity> optional = accountDao.findByMobile(param.getMobile());
            if (optional.isPresent()) {
                throw new IllegalAccessException("手机号已存在");
            }
        }
        AccountEntity entity = new AccountEntity();
        String code = randomNumber();
        entity.setAccount(code);
        entity.setEmail(param.getEmail());
        entity.setPwd(passwordEncoder.encode(param.getPassword()));
        entity.setNickname(param.getNickname());
        entity.setIssActive(false); // 新注册未激活
        entity.setIssLock(false);
        return accountDao.save(entity);
    }

    /**
     * 根据电话号码创建简单账户并设置已激活
     * 需要认证 由资源服务器发起的注册
     * @param mobile
     * @return
     * @throws IllegalAccessException
     */
    @Transactional
    @Override
    public AccountEntity createSimpleAccount(Long mobile) throws IllegalAccessException {
        if (mobile > 100000) {
            Optional<AccountEntity> optional = accountDao.findByMobile(mobile);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        AccountEntity entity = new AccountEntity();
        String code = randomNumber();
        entity.setAccount(code);
        entity.setEmail("");
        entity.setPwd(null);
        entity.setNickname(code.toString());
        entity.setIssActive(true);
        entity.setIssLock(false);
        return accountDao.save(entity);
    }


    String randomNumber() {
        int i = random.nextInt(k.size());
        Integer first = k.get(i);
        double random1 = Math.random();
        String last = String.valueOf(random1).substring(2, 11);
        String code = first + last;
        Optional<AccountEntity> optional = accountDao.findByAccount(code);
        if (optional.isPresent()) {
            return randomNumber();
        }
        return code;
    }
}

package cn.sdack.go.auth.service;

import cn.sdack.go.auth.entity.AccountEntity;
import cn.sdack.go.auth.query.RegisterQuery;
import jakarta.transaction.Transactional;

/**
 * @author sdack
 * @date 2023/12/24
 */
public interface AccountService {

    AccountEntity register(RegisterQuery param) throws IllegalAccessException;

    AccountEntity createSimpleAccount(Long mobile) throws IllegalAccessException;
}

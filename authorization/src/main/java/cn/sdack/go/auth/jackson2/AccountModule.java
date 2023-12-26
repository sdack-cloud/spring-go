package cn.sdack.go.auth.jackson2;

import cn.sdack.go.auth.entity.AccountEntity;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author sdack
 * @date 2023/12/23
 */
public class AccountModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(AccountEntity.class,AccountMixin.class);
    }
}

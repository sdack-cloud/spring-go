package cn.sdack.go.auth.authentication;

import cn.sdack.go.auth.config.GrantTypeNames;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 自定义短信验证码授权token类型
 * @author sdack
 * @date 2023/12/28
 */
public class SmsCodeGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {


    private final String smsCode;

    protected SmsCodeGrantAuthenticationToken(String smsCode, Authentication clientPrincipal, @Nullable Map<String, Object> additionalParameters) {
        super(new AuthorizationGrantType(GrantTypeNames.SMS_CODE), clientPrincipal, additionalParameters);
        Assert.hasText(smsCode, "smsCode cannot be empty");
        this.smsCode = smsCode;
    }

    @Nullable
    public String getSmsCode() {
        return smsCode;
    }

}

package cn.sdack.go.auth.query;

/**
 * @author sdack
 * @date 2023/12/24
 */
public class RegisterQuery {

    private String nickname = "";

    private String password = "";

    private String email = "";

    private Long mobile = 0L;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }
}

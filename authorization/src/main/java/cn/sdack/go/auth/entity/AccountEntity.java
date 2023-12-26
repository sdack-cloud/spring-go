package cn.sdack.go.auth.entity;

/**
 * 账户
 *
 * @author sdack
 * @date 2023/12/23
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;


/**
 * 账户
 * @author sdack
 * @date 2023/12/23
 */
@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_account_email", columnList = "email"),
        @Index(name = "idx_account_mobile", columnList = "mobile"),
        @Index(name = "idx_account_account", columnList = "account"),
})
public class AccountEntity implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = null;

    // 账号
    @Column(length = 100, nullable = false)
    public String account = "";

    // 昵称
    @Column(length = 200, nullable = false)
    public String nickname = "";

    // 邮箱
    @Column(length = 150, nullable = false)
    public String email = "";

    // 手机号
    @Column(nullable = false)
    public Long mobile = 0L;

    // 手机号
    @Column(length = 50,nullable = true)
    public String phone = null;

    // 姓名
    @Column(length = 150, nullable = false)
    public String name = "";

    // 化身
    @Column(length = 500, nullable = true)
    public String avatar  = "";

    @JsonIgnore
    public String pwd = null;

    // 是否锁定
    @Column(name = "is_lock")
    public Boolean issLock = false;

    // 是否账户激活
    @Column(name = "is_active")
    public Boolean issActive = false;


    @Column(length = 200, nullable = false)
    public String remark = "";

    // 过期时间
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime expTime = null;


    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition="timestamp default now()",insertable = false, updatable = false)
    public LocalDateTime created = null;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition="timestamp default now()",insertable = false, updatable = false)
    public LocalDateTime modified = null;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(insertable = false)
    public LocalDateTime del = null;


    @Transient
    public String oldPwd = null;

    @Transient
    public HashSet<SimpleGrantedAuthority> authority= new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authority;
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return account;
    }

    @Override
    public boolean isAccountNonExpired() {
        if (expTime == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(expTime);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !issLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return issActive;
    }

    @Override
    public boolean isEnabled() {
        return del == null;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Boolean getIssLock() {
        return issLock;
    }

    public void setIssLock(Boolean issLock) {
        this.issLock = issLock;
    }

    public Boolean getIssActive() {
        return issActive;
    }

    public void setIssActive(Boolean issActive) {
        this.issActive = issActive;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getExpTime() {
        return expTime;
    }

    public void setExpTime(LocalDateTime expTime) {
        this.expTime = expTime;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public LocalDateTime getDel() {
        return del;
    }

    public void setDel(LocalDateTime del) {
        this.del = del;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public HashSet getAuthority() {
        return authority;
    }

    public void setAuthority(HashSet authority) {
        this.authority = authority;
    }
}

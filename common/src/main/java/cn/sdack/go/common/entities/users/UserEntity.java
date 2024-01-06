package cn.sdack.go.common.entities.users;

import cn.sdack.go.common.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

/**
 * @author sdack
 * @date 2023/12/26
 */
@Data
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_account",columnList = "account",unique = true)
})
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    // 授权服务器账号
    @Column(length = 50)
    private String account = "0";

    @Column(length = 180)
    private String nickname = "";

    @Column(length = 180)
    private String username = "";

    @Column(length = 150)
    private String email = "";

    @Column(length = 30)
    private String phone = "";

    @Column(length = 500)
    private String avatar = "";



}

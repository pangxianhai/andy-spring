package com.andy.spring.auth;

import java.io.Serializable;
import lombok.Data;

/**
 * 当前登录信息
 *
 * @author 庞先海 2018-07-10
 */
@Data
public class LoginInfo implements Serializable {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实名称
     */
    private String realName;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 角色类型
     */
    private RoleType roleType;

    /**
     * 附加字段
     */
    private String attach;
}

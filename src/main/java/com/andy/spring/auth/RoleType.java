package com.andy.spring.auth;

import com.andy.spring.type.BaseType;

/**
 * 基础模块-权限:
 *
 * <p>角色类型
 *
 * @author 庞先海 2018-07-10
 */
public class RoleType extends BaseType {

    public final static int MANAGER_CODE = 1;

    public final static int CUSTOMER_CODE = 2;

    public final static int PARTNER_CODE = 3;

    public final static RoleType MANAGER = new RoleType(MANAGER_CODE, "管理员");

    public final static RoleType CUSTOMER = new RoleType(CUSTOMER_CODE, "用户");

    public final static RoleType PARTNER = new RoleType(PARTNER_CODE, "合作方");

    private RoleType(int code, String desc) {
        super(code, desc);
    }

    public RoleType() {}

}

package com.andy.spring.exception;

/**
 * 几个基础的返回码及信息
 *
 * @author 庞先海 2019-11-14
 */
public enum CommonCode {

    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 权限不足
     */
    UNAUTHORIZED(401),
    /**
     * 被禁止
     */
    FORBIDDEN(403),
    /**
     * 系统错误
     */
    SYS_ERROR(500),
    /**
     * 太多的查询结果
     */
    TOO_MANY_RESULT(501),
    /**
     * 参数错误
     */
    PARAM_ERROR(600),
    /**
     * 未登录
     */
    NOT_LOGIN(601);


    private int code;

    CommonCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CommonCode parseCode(int code) {
        CommonCode[] codes = CommonCode.values();
        for (CommonCode c : codes) {
            if (c.getCode() == code) {
                return c;
            }
        }
        return null;
    }
}

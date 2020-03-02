package com.andy.spring.base;

import com.andy.spring.exception.CommonCode;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

/**
 * 统一返回结果
 *
 * @author 庞先海 2019-11-14
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 4543483086776965612L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public Result() {}

    public Result(T data) {
        this.code = CommonCode.SUCCESS.getCode();
        this.msg = CommonCode.SUCCESS.toString();
        this.data = data;
    }

    public Result(int code, String message) {
        this.code = code;
        CommonCode ccode = CommonCode.parseCode(code);
        if (null != ccode && StringUtils.isEmpty(message)) {
            this.msg = ccode.toString();
        } else {
            this.msg = message;
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code == CommonCode.SUCCESS.getCode();
    }
}

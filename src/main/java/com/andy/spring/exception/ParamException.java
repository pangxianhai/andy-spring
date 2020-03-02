package com.andy.spring.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * 参数异常定义
 *
 * @author 庞先海 2019-11-17
 */

public class ParamException extends CommonException {

    public ParamException(String message) {
        super(CommonCode.PARAM_ERROR.getCode());
        this.message = message;
    }

    public ParamException(String message, Throwable e) {
        super(CommonCode.PARAM_ERROR.getCode(), e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (StringUtils.isEmpty(message)) {
            return super.getMessage();
        } else {
            return message;
        }
    }
}

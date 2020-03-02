package com.andy.spring.exception;


import com.andy.spring.message.MessageHandler;

/**
 * 公共异常类型
 *
 * @author 庞先海 2019-11-14
 */
public class CommonException extends RuntimeException {

    private static final long serialVersionUID = 5487323432667207519L;
    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    protected String message;

    public CommonException() {

    }

    public CommonException(int code) {
        this.code = code;
        setMessage(code);
    }

    public CommonException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonException(int code, Object... args) {
        this.code = code;
        setMessage(code, args);
    }


    public CommonException(int code, Throwable e) {
        super(e);
        this.code = code;
        setMessage(code);
    }


    public CommonException(int code, Throwable e, Object... args) {
        super(e);
        this.code = code;
        setMessage(code, args);
    }

    private void setMessage(int code, Object... args) {
        if (null != args && args.length > 0) {
            this.message = MessageHandler.getMessage(code, args);
        } else {
            this.message = MessageHandler.getMessage(code);
        }
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

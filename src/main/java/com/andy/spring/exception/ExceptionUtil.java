package com.andy.spring.exception;


import com.andy.spring.base.Result;
import com.andy.spring.message.MessageHandler;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常工具
 *
 * @author 庞先海 2019-11-17
 */
public class ExceptionUtil {

    public static <T> Result<T> parseException(Exception e) {

        if (e instanceof UndeclaredThrowableException) {
            Throwable throwable = ((UndeclaredThrowableException)e).getUndeclaredThrowable().getCause();
            if (throwable instanceof CommonException) {
                CommonException commonException = (CommonException)throwable;
                return new Result<>(commonException.getCode(), commonException.getMessage());
            }
        }
        if (e instanceof CommonException) {
            CommonException commonException = (CommonException)e;
            return new Result<>(commonException.getCode(), commonException.getMessage());
        } else {
            int sysCode = CommonCode.SYS_ERROR.getCode();
            return new Result<>(sysCode, MessageHandler.getMessage(sysCode));
        }
    }
}

package com.andy.spring.handler;

import com.andy.spring.context.ServletContext;
import com.andy.spring.base.Result;
import com.andy.spring.exception.CommonCode;
import com.andy.spring.exception.ExceptionUtil;
import com.andy.spring.util.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能描述
 *
 * @author 庞先海 2020-01-27
 */
@Slf4j
public class MyExceptionHandler {

    private String charsetName;

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object jsonErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e)
        throws Exception {
        if (response != null) {
            response.setHeader("Content-type", "text/html;charset=" + charsetName);
        }
        Object resultObj = this.parseException(e);
        if (! (resultObj instanceof Result)) {
            return resultObj;
        }
        Result<?> result = (Result<?>)resultObj;
        //用户传入的参数发生的错误不打印日志
        if (result.getCode() != CommonCode.PARAM_ERROR.getCode()) {
            printErrorLog(result, e);
        }
        return result;
    }

    protected Object parseException(Exception e) {
        return ExceptionUtil.parseException(e);
    }

    protected void printErrorLog(Result<?> result, Exception e) {
        String errorMessage = "";
        HttpServletRequest request = ServletContext.getRequest();
        if (request != null) {
            errorMessage += request.getMethod() + " address:" + request.getRequestURI() + ". params:"
                            + ServletUtil.parseParam(request);
        }
        if (null == result) {
            log.error(errorMessage, e);
        } else {
            errorMessage += ". error code:" + result.getCode() + ". error message:" + result.getMsg();
            if (CommonCode.SYS_ERROR.getCode() == result.getCode()) {
                log.error(errorMessage, e);
            } else {
                log.warn(errorMessage, e);
            }
        }
    }
}

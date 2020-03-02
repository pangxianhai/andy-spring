package com.andy.spring.context;

import com.andy.spring.auth.LoginInfo;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础模块-WEB基础:
 *
 * <p> servlet 上下文 主要保存request response
 *
 * @author 庞先海 2017-07-12
 */
public class ServletContext {

    protected final static ThreadLocal<ServletContext> CONTROLLER_CONTEXT = new ThreadLocal<>();

    private Map<String, Object> context;

    private final static String REQUEST = "base.request";

    private final static String RESPONSE = "base.response";

    private final static String PARAM = "source.param";

    private final static String RESULT = "result";

    private final static String LOGIN_INFO = "login.info";

    protected ServletContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * 初始化 该方法在ControllerFilter中调用其他地方不能调用
     *
     * @author 庞先海 2017-07-12
     */
    public static void init(HttpServletRequest request, HttpServletResponse response) {
        CONTROLLER_CONTEXT.set(new ServletContext(new HashMap<>(16)));
        CONTROLLER_CONTEXT.get().setValue(REQUEST, request);
        CONTROLLER_CONTEXT.get().setValue(RESPONSE, response);
    }

    public static void clean() {
        CONTROLLER_CONTEXT.remove();
        CONTROLLER_CONTEXT.set(null);
    }

    /**
     * 获取 request
     *
     * @author 庞先海 2017-07-12
     */
    public static HttpServletRequest getRequest() {
        if (CONTROLLER_CONTEXT.get() == null) {
            return null;
        }
        return (HttpServletRequest)CONTROLLER_CONTEXT.get().getValue(REQUEST);
    }

    /**
     * 获取 response
     *
     * @author 庞先海 2017-07-12
     */
    public static HttpServletResponse getResponse() {
        if (CONTROLLER_CONTEXT.get() == null) {
            return null;
        }
        return (HttpServletResponse)CONTROLLER_CONTEXT.get().getValue(RESPONSE);
    }


    public static void setParam(String param) {
        if (null == CONTROLLER_CONTEXT.get()) {
            CONTROLLER_CONTEXT.set(new ServletContext(new HashMap<>(16)));
        }
        CONTROLLER_CONTEXT.get().setValue(PARAM, param);
    }

    public static String getParam() {
        if (null == CONTROLLER_CONTEXT.get()) {
            return null;
        }
        return (String)CONTROLLER_CONTEXT.get().getValue(PARAM);
    }

    public static void setResult(Object value) {
        if (null == CONTROLLER_CONTEXT.get()) {
            CONTROLLER_CONTEXT.set(new ServletContext(new HashMap<>(16)));
        }
        CONTROLLER_CONTEXT.get().setValue(RESULT, value);
    }

    public static Object getResult() {
        if (null == CONTROLLER_CONTEXT.get()) {
            return null;
        }
        return CONTROLLER_CONTEXT.get().getValue(RESULT);
    }

    public static void setLoginInfo(LoginInfo loginInfo) {
        if (null == CONTROLLER_CONTEXT.get()) {
            CONTROLLER_CONTEXT.set(new ServletContext(new HashMap<>(16)));
        }
        CONTROLLER_CONTEXT.get().setValue(LOGIN_INFO, loginInfo);
    }

    public static LoginInfo getLoginInfo() {
        if (null == CONTROLLER_CONTEXT.get()) {
            return null;
        }
        return (LoginInfo)CONTROLLER_CONTEXT.get().getValue(LOGIN_INFO);
    }

    protected Object getValue(String key) {
        return context.get(key);
    }

    protected void setValue(String key, Object value) {
        context.put(key, value);
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}

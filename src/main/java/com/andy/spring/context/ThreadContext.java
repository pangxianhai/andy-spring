package com.andy.spring.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义线程上下文
 *
 * @author 庞先海 2017-10-26
 */
public class ThreadContext {

    private static final ThreadLocal<ThreadContext> CONTROLLER_CONTEXT = new ThreadLocal<>();

    private Map<String, Object> context;


    private ThreadContext(Map<String, Object> context) {
        this.context = context;
    }

    public static void init() {
        CONTROLLER_CONTEXT.set(new ThreadContext(new HashMap<>(16)));
    }

    public static void clean() {
        CONTROLLER_CONTEXT.remove();
        CONTROLLER_CONTEXT.set(null);
    }


    public static void setValue(String key, Object value) {
        CONTROLLER_CONTEXT.get().context.put(key, value);
    }

    public static Object getValue(String key) {
        return CONTROLLER_CONTEXT.get().context.get(key);
    }
}

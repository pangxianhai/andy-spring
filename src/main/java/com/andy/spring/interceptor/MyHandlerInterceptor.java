package com.andy.spring.interceptor;

import com.andy.spring.context.ThreadContext;
import com.andy.spring.util.DateUtil;
import com.andy.spring.util.JsonUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务连接器 统计接口响应时间 打印返回结果等
 *
 * @author 庞先海 2019-12-30
 */
@Slf4j
public class MyHandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("monitorLogger");


    protected void myDoBefore(JoinPoint joinPoint) {
        ThreadContext.init();
        Instant startTime = Instant.now();
        ThreadContext.setValue("startTime", startTime);
    }

    protected void myDoAfterReturn(JoinPoint joinPoint, Object returnValue) {
        Instant startTime = (Instant)ThreadContext.getValue("startTime");
        long duration = Duration.between(startTime, Instant.now()).toMillis();
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Map<String, Object> logItem = new HashMap<>(3);
        logItem.put("costTime", duration);
        logItem.put("method", method);
        logItem.put("time", DateUtil.format("yyyy-MM-dd HH:mm:ss:SSS"));
        logItem.put("params", JsonUtil.toJson(joinPoint.getArgs()));
        logItem.put("result", JsonUtil.toJson(returnValue));
        logger.info(JsonUtil.toJson(logItem));
    }
}

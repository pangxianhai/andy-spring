package com.andy.spring.interceptor;

import com.andy.spring.context.ServletContext;
import com.andy.spring.converter.MyHttpMessageConverter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 基础拦截器
 *
 * @author 庞先海 2018-05-24
 */
@Slf4j
public class MyWebHandlerInterceptor implements HandlerInterceptor {

    protected HttpMessageConverters httpMessageConverters;


    protected String charsetName;

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }


    public void setHttpMessageConverters(HttpMessageConverters httpMessageConverters) {
        this.httpMessageConverters = httpMessageConverters;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        response.setHeader("Content-type", "text/html;charset=" + charsetName);
        ServletContext.init(request, response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        if (ServletContext.getResult() == null) {
            this.rewriteNullResult(response, handler);
        }
        ServletContext.clean();
    }

    protected void rewriteNullResult(HttpServletResponse response, Object handler) throws IOException {
        if (! (handler instanceof HandlerMethod)) {
            return;
        }
        if (CollectionUtils.isEmpty(httpMessageConverters.getConverters())) {
            return;
        }
        MyHttpMessageConverter myHttpMessageConverter = null;
        for (HttpMessageConverter<?> httpMessageConverter : httpMessageConverters.getConverters()) {
            if (httpMessageConverter instanceof MyHttpMessageConverter) {
                myHttpMessageConverter = (MyHttpMessageConverter)httpMessageConverter;
                break;
            }
        }
        if (myHttpMessageConverter == null) {
            return;
        }
        ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        MethodParameter methodParameter = handlerMethod.getReturnType();
        if (methodParameter.getParameterType().isAssignableFrom(Collection.class)) {
            myHttpMessageConverter.write(Collections.emptyList(), MediaType.APPLICATION_JSON_UTF8,
                servletServerHttpResponse);
        } else {
            myHttpMessageConverter.write(new Object(), MediaType.APPLICATION_JSON_UTF8, servletServerHttpResponse);
        }
    }
}

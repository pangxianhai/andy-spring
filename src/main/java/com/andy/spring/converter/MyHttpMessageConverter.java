package com.andy.spring.converter;

import com.andy.spring.base.Result;
import com.andy.spring.context.ServletContext;
import com.andy.spring.util.JsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.util.PatternMatchUtils;

/**
 * 基础模块-转化器:
 *
 * <p> httpMessage转化器 转化为json 支持result格式 支持callback 默认gson转换
 *
 * @author 庞先海 2017-07-12
 */
public class MyHttpMessageConverter extends AbstractConverter implements HttpMessageConverter {

    private static String CHARSET_NAME = "UTF-8";

    /**
     * 忽略的URI
     */
    private List<String> ignoreURIList;


    public MyHttpMessageConverter(String charsetName) {
        super();
        CHARSET_NAME = charsetName;
        ignoreURIList = new ArrayList<>(1);
    }

    public MyHttpMessageConverter() {
        this(CHARSET_NAME);
    }

    public void addIgnoreURIList(String ignoreURI) {
        if (StringUtils.isNotBlank(ignoreURI)) {
            this.ignoreURIList.add(StringUtils.trim(ignoreURI));
        }
    }

    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        return ! isInIgnoreURI();
    }

    @Override
    public boolean canWrite(Class clazz, MediaType mediaType) {
        return ! isInIgnoreURI();
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        List<MediaType> supportedMediaTypes = new ArrayList<>(5);
        supportedMediaTypes.add(MediaType.valueOf("text/plain;charset=" + CHARSET_NAME));
        supportedMediaTypes.add(MediaType.valueOf("text/html;charset=" + CHARSET_NAME));
        supportedMediaTypes.add(MediaType.valueOf("application/javascript;charset=" + CHARSET_NAME));
        supportedMediaTypes.add(MediaType.valueOf("application/json;charset=" + CHARSET_NAME));
        supportedMediaTypes.add(MediaType.ALL);
        return supportedMediaTypes;
    }

    @Override
    @NonNull
    public Object read(@NonNull Class clazz, @NonNull HttpInputMessage httpInputMessage)
        throws IOException, HttpMessageNotReadableException {
        String paramText = this.readParamFromInput(httpInputMessage);
        //请求原始参数记录到上下文中
        ServletContext.setParam(paramText);
        Object object = this.read(paramText, clazz);
        this.checkParam(object);
        return object;
    }

    protected Object read(String body, Class<?> clazz) {
        Source sourceTag = AnnotationUtils.findAnnotation(clazz, Source.class);
        if (null != sourceTag) {
            return body;
        } else {
            return JsonUtil.fromJson(body, clazz);
        }
    }

    @Override
    public void write(@NonNull Object o, MediaType mediaType, @NonNull HttpOutputMessage httpOutputMessage)
        throws IOException, HttpMessageNotWritableException {
        ServletContext.setResult(o);
        OutputStream out = httpOutputMessage.getBody();
        String resultText = buildResultJson(o);
        String callback = "";
        HttpServletRequest request = ServletContext.getRequest();
        if (request != null) {
            callback = request.getParameter("callback");
        }
        if (StringUtils.isNotBlank(callback)) {
            resultText = String.format("%s(%s)", callback, resultText);
        }
        HttpServletResponse response = ServletContext.getResponse();
        if (response != null) {
            response.addHeader("Content-type", "application/json;charset=" + CHARSET_NAME);
        }
        if (null != resultText) {
            out.write(resultText.getBytes(CHARSET_NAME));
        }
    }

    private String buildResultJson(Object object) throws IOException {
        HttpServletResponse response = ServletContext.getResponse();
        if (null != response) {
            if (HttpServletResponse.SC_OK != response.getStatus()) {
                response.sendError(response.getStatus());
                return null;
            }
        }
        return write(object);
    }

    protected String write(Object object) {
        if (isInIgnoreURI() || object instanceof Result) {
            return JsonUtil.toJson(object);
        } else {
            return JsonUtil.toJson(new Result<>(object));
        }
    }

    protected boolean isInIgnoreURI() {
        if (CollectionUtils.isEmpty(ignoreURIList)) {
            return false;
        }
        HttpServletRequest request = ServletContext.getRequest();
        if (null == request) {
            return false;
        }
        String uri = request.getRequestURI();
        for (String igUri : ignoreURIList) {
            if (PatternMatchUtils.simpleMatch(igUri, uri)) {
                return true;
            }
        }
        return false;
    }

    protected String readParamFromInput(HttpInputMessage httpInputMessage) throws IOException {
        InputStream inputStream = httpInputMessage.getBody();
        StringBuilder paramsBuffer = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = inputStream.read(b)) != - 1; ) {
            paramsBuffer.append(new String(b, 0, n));
        }
        return paramsBuffer.toString();
    }
}

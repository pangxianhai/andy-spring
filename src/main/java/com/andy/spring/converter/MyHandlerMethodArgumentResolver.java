package com.andy.spring.converter;

import com.andy.spring.util.ObjectUtil;
import com.andy.spring.util.ReflectionUtil;
import javax.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 参数转换
 *
 * @author 庞先海 2018-01-06
 */
public class MyHandlerMethodArgumentResolver extends AbstractConverter implements HandlerMethodArgumentResolver {

    /**
     * 前缀标识符
     */
    private static String PREFIX_TIPS = ".";

    private final static Class<?>[] BASE_CLASS_TYPE = new Class[] {String.class, Integer.class, int.class,
                                                                   Boolean.class, boolean.class, Long.class, long.class,
                                                                   Double.class, double.class};

    private static Validator validator;


    public MyHandlerMethodArgumentResolver() {
        super();
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(MyParameter.class);
    }

    protected void setPrefixTips(String prefixTips) {
        PREFIX_TIPS = prefixTips;
    }

    protected Object getParameterValue(NativeWebRequest webRequest, String parameterKey) {
        String value = webRequest.getParameter(parameterKey);
        return StringUtils.trimToNull(value);
    }

    protected Object resolveObject(Class<?> targetType, boolean hasPrefix, String objectName, MethodParameter parameter,
        NativeWebRequest webRequest, WebDataBinder binder) throws Exception {
        //将request的中参数绑定到对象上
        Object target = ObjectUtil.instantiateClass(targetType);
        ReflectionUtil.doWithFields(targetType, field -> {
            ReflectionUtil.makeAccessible(field);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            if (hasPrefix) {
                //如果有前缀读取含前缀的参数 如前端传入的user.name
                fieldName = objectName + PREFIX_TIPS + fieldName;
            }
            Object argValue = getParameterValue(webRequest, fieldName);
            if (null == argValue) {
                return;
            }
            Object arg = binder.convertIfNecessary(argValue, fieldType, parameter);
            if (null != arg) {
                try {
                    field.set(target, arg);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return target;
    }

    protected Object resolveBaseObject(Class<?> targetType, boolean hasPrefix, String objectName,
        MethodParameter parameter, NativeWebRequest webRequest, WebDataBinder binder) {
        String parameterKey = parameter.getParameterName();
        if (hasPrefix) {
            parameterKey = objectName + PREFIX_TIPS + parameterKey;
        }
        Object value = getParameterValue(webRequest, parameterKey);
        return binder.convertIfNecessary(value, targetType, parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (binderFactory == null) {
            return null;
        }
        String parameterName = parameter.getParameterName();
        boolean hasPrefix = false;
        if (parameterName.contains(PREFIX_TIPS)) {
            hasPrefix = true;
        }

        Class<?> targetType = parameter.getParameterType();
        MyParameter myParameter = parameter.getParameterAnnotation(MyParameter.class);
        String objectName = this.getObjectName(myParameter, targetType, hasPrefix);
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, objectName);

        for (Class baseType : BASE_CLASS_TYPE) {
            if (targetType.isAssignableFrom(baseType)) {
                return resolveBaseObject(targetType, hasPrefix, objectName, parameter, webRequest, binder);
            }
        }
        Object value = resolveObject(targetType, hasPrefix, objectName, parameter, webRequest, binder);
        this.checkParam(value);

        return value;
    }

    private String getObjectName(MyParameter myParameter, Class<?> targetType, boolean hasPrefix) {
        if (! hasPrefix) {
            return StringUtils.EMPTY;
        }
        String prefix = myParameter.prefix();
        if (StringUtils.isEmpty(prefix)) {
            prefix = getDefaultClassName(targetType);
        }
        return prefix;
    }

    private String getDefaultClassName(Class<?> targetType) {
        return ClassUtils.getShortNameAsProperty(targetType);
    }
}

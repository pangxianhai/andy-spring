package com.andy.spring.util.asserts;


import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 断言检查工具
 *
 * @author 庞先海 2019-11-14
 */
public class AssertUtil {

    /**
     * 判断是否为true
     *
     * @param condition 条件
     * @param supplier  异常回调
     */
    public static void assertTrue(Boolean condition, Supplier<Throwable> supplier) {
        if (condition == null || ! condition) {
            throwException(supplier.get());
        }
    }

    /**
     * 判断是否为false
     *
     * @param condition 条件
     * @param supplier  异常回调
     */
    public static void assertFalse(Boolean condition, Supplier<Throwable> supplier) {
        if (condition == null || condition) {
            throwException(supplier.get());
        }
    }

    /**
     * 判断对象是否为空
     *
     * @param object   对象
     * @param supplier 异常回调
     */
    public static void assertNull(Object object, Supplier<Throwable> supplier) {
        if (null == object) {
            throwException(supplier.get());
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param text     文本值
     * @param supplier 异常回调
     */
    public static void assertEmpty(String text, Supplier<Throwable> supplier) {
        if (StringUtils.isEmpty(text)) {
            throwException(supplier.get());
        }
    }

    /**
     * 判断list是否为空
     *
     * @param list     列表
     * @param supplier 异常回调
     */
    public static void assertListEmpty(List<?> list, Supplier<Throwable> supplier) {
        if (CollectionUtils.isEmpty(list)) {
            throwException(supplier.get());
        }
    }

    /**
     * 判断list是否为空
     *
     * @param map      集合map
     * @param supplier 异常回调
     */
    public static void assertMapEmpty(Map<?, ?> map, Supplier<Throwable> supplier) {
        if (CollectionUtils.isEmpty(map)) {
            throwException(supplier.get());
        }
    }

    private static void throwException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        } else {
            throw new RuntimeException(throwable);
        }
    }
}

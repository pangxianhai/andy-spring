package com.andy.spring.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串与数字转化
 *
 * @author 庞先海 2019-11-14
 */
public class NumberUtil {

    public static Double parseDouble(String text) {
        return parseDouble(text, null);
    }

    /**
     * 转换为double
     *
     * @param defaultValue 转换失败返回默认值
     */
    public static Double parseDouble(String text, Double defaultValue) {
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Integer parseInt(String text) {
        return parseInt(text, null);
    }

    /**
     * 转换为int
     *
     * @param defaultValue 转换失败返回默认值
     */
    public static Integer parseInt(String text, Integer defaultValue) {
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Float parseFloat(String text) {
        return parseFloat(text, null);
    }


    /**
     * 转换为 float
     *
     * @param defaultValue 转换失败返回默认值
     */
    public static Float parseFloat(String text, Float defaultValue) {
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long parseLong(String text) {
        return parseLong(text, null);
    }

    /**
     * 转换为Long
     *
     * @param defaultValue 转换失败返回默认值
     */
    public static Long parseLong(String text, Long defaultValue) {
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 转换为Short
     */
    public static Short parseShort(String text) {
        return parseShort(text, null);
    }

    /**
     * 转换为Short
     *
     * @param defaultValue 转换失败返回默认值
     */
    public static Short parseShort(String text, Short defaultValue) {
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        try {
            return Short.parseShort(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取最小值
     *
     * @param numbers 数值列表
     * @return 数值列表中最小值
     */
    public static Long min(Long... numbers) {
        Long min = numbers[0];
        for (Long n : numbers) {
            if (n < min) {
                min = n;
            }
        }
        return min;
    }

    /**
     * 获取最大值
     *
     * @param numbers 数值列表
     * @return 数值列表中最大值
     */
    public static Long max(Long... numbers) {
        Long max = numbers[0];
        for (Long n : numbers) {
            if (n > max) {
                max = n;
            }
        }
        return max;
    }
}

package com.andy.spring.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * object 反射工具类
 *
 * @author 庞先海 2019-11-14
 */
public class StringUtil {

    public static String EMPTY = "";

    private final static Pattern EMAIL_PATTERN = Pattern.compile("[\\w\\.\\-_]+@([\\w]+\\.)+[\\w]+",
        Pattern.CASE_INSENSITIVE);

    private final static Pattern MOBILE_PATTERN = Pattern.compile("^1\\d{10}$");

    /**
     * 清除字符串 StringUtil.clean(null) == "" StringUtil.clean("") == "" StringUtil.clean("abc") == "abc" StringUtil.clean("
     * abc ") == "abc"
     */
    public static String clean(String text) {
        return text == null ? EMPTY : text.trim();
    }

    /**
     * 判断字符串是否是email地址
     */
    public static boolean isEmailAddress(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        return matcher.matches();
    }

    /**
     * 判断字符串是否是手机号
     */
    public static boolean isMobile(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }

        Matcher matcher = MOBILE_PATTERN.matcher(text);
        return matcher.matches();
    }

    /**
     * 转换为char 转化失败返回null
     */
    public static Character parseChar(String text) {
        return parseChar(text, null);
    }

    /**
     * 转换为char 转化失败返回默认值
     */
    public static Character parseChar(String text, Character defaultValue) {
        if (StringUtils.isEmpty(text) || text.length() != 1) {
            return defaultValue;
        }
        return text.charAt(0);
    }

    /**
     * 字符串解析  转化失败返回null
     */
    public static Boolean parseBoolean(String text) {
        return parseBoolean(text, null);
    }

    /**
     * 字符串解析  转化失败返回默认值
     */
    public static Boolean parseBoolean(String text, Boolean defaultValue) {
        if (StringUtils.isEmpty(text)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(text);
    }

    /**
     * 字符串截取
     *
     * @param length 截取长度
     * @return text的length大于length 则截取字符串并加上...后缀 否则返回text
     */
    public static String substring(String text, int length) {
        return substring(text, length, "...");
    }

    /**
     * 字符串截取
     *
     * @param length  截取长度
     * @param postfix 后缀
     * @return text的length大于length 则截取字符串并加上postfix后缀 否则返回text
     */
    public static String substring(String text, int length, String postfix) {
        if (StringUtils.isEmpty(text) || text.length() <= length) {
            return text;
        } else {
            return text.substring(0, length) + postfix;
        }
    }

    public static List<String> split(String text, String regex) {
        return StringUtil.split(text, regex, String.class);
    }


    /**
     * 字符串分裂 对转化失败的类型会返回null
     *
     * @param clazz class类新 只能时基础类型String Integer Long Double  Float Short 其他类型不支持
     */
    public static <T> List<T> split(String text, String regex, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        String[] textArr = text.split(regex);
        List<T> resultList = new ArrayList<>(textArr.length);
        for (String t : textArr) {
            Object value;
            if (clazz.isAssignableFrom(Long.class)) {
                value = NumberUtil.parseLong(t);
            } else if (clazz.isAssignableFrom(Integer.class)) {
                value = NumberUtil.parseInt(t);
            } else if (clazz.isAssignableFrom(Double.class)) {
                value = NumberUtil.parseDouble(t);
            } else if (clazz.isAssignableFrom(Float.class)) {
                value = NumberUtil.parseFloat(t);
            } else if (clazz.isAssignableFrom(Short.class)) {
                value = NumberUtil.parseShort(t);
            } else if (clazz.isAssignableFrom(String.class)) {
                value = t;
            } else {
                throw new RuntimeException("unsupported clazz type" + clazz);
            }
            resultList.add(clazz.cast(value));
        }
        return resultList;
    }

    public static String join(List<?> list, String regex) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); ++ i) {
            Object item = list.get(i);
            if (i == list.size() - 1) {
                builder.append(item.toString());
            } else {
                builder.append(item.toString()).append(regex);
            }
        }
        return builder.toString();
    }

    /**
     * 将字符串转化为单字母的list
     *
     * @param text 传入字符串
     * @return text转换后的list
     */
    public static List<String> convertStringToList(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        List<String> list = new ArrayList<>(text.length());
        for (char c : text.toCharArray()) {
            list.add(String.valueOf(c));
        }
        return list;
    }

    /**
     * 截取字符串
     *
     * @param text       原始字符串
     * @param startIndex 开始位置 <0从后先前多少的位置
     * @param length     长度 <0从当前未知向前取
     * @return 截取后的字符串
     */
    public static String cutString(String text, int startIndex, int length) {
        if (startIndex >= 0) {
            if (length < 0) {
                length = length * - 1;
                if (startIndex - length < 0) {
                    length = startIndex;
                    startIndex = 0;
                } else {
                    startIndex = startIndex - length;
                }
            }
            if (startIndex > text.length()) {
                return EMPTY;
            }

        } else {
            if (length < 0) {
                return EMPTY;
            } else {
                if (length + startIndex > 0) {
                    length = length + startIndex;
                    startIndex = 0;
                } else {
                    return EMPTY;
                }
            }
        }
        if (text.length() - startIndex < length) {
            length = text.length() - startIndex;
        }
        return text.substring(startIndex, startIndex + length);
    }

    public static String cutString(String str, int startIndex) {
        return cutString(str, startIndex, str.length());
    }

    /**
     * 将字符串补充到len的长度, 若text的长度超过了len直接返回
     *
     * @param text    原始字符串
     * @param isFront true在前补充 false在后补充
     * @param c       用于补充的字符串
     * @param len     补足后的长度
     * @return 补足后的字符串
     */
    public static String suppleText(String text, boolean isFront, char c, int len) {
        if (StringUtils.isEmpty(text)) {
            text = StringUtils.trimToEmpty(text);
        }
        if (text.length() >= len) {
            return text;
        }
        StringBuilder suppleBuilder = new StringBuilder();
        for (int i = 0; i < len - text.length(); ++ i) {
            suppleBuilder.append(c);
        }
        if (isFront) {
            return suppleBuilder.toString() + text;
        } else {
            return text + suppleBuilder.toString();
        }
    }

    /**
     * 将字符串补充到len的长度,在字符串前面补充, 若text的长度超过了len直接返回
     *
     * @param text 原始字符串
     * @param c    用于补充的字符串
     * @param len  补足后的长度
     * @return 补足后的字符串
     */
    public static String suppleFrontText(String text, char c, int len) {
        return suppleText(text, true, c, len);
    }

    /**
     * 二进制数据转十六进制字符串
     *
     * @param bytes 二进制数据
     * @return 转换为十六进制字符串
     */
    public static String byteToHexString(byte[] bytes) {
        StringBuilder text = new StringBuilder();
        for (byte b : bytes) {
            String part = Integer.toHexString(b & 0xFF);
            if (part.length() == 1) {
                part = "0" + part;
            }
            text.append(part);
        }
        return text.toString();
    }

    /**
     * 十六进制数据转二进制字符串
     *
     * @param text 十六进制数据
     * @return 转换为二进制
     */
    public static byte[] hexStringToBytes(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        text = text.toUpperCase();
        int length = text.length() / 2;
        char[] hexChars = text.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 将url格式参数 如 a=b&c=d格式转换为参数类型 重复key会被覆盖
     *
     * @param queryParam url参数
     * @return 结果
     */
    public static Map<String, String> parseQueryParam(String queryParam) {
        if (StringUtils.isEmpty(queryParam)) {
            return null;
        }
        String[] paramNode = queryParam.split("&");
        Map<String, String> paramMap = new HashMap<>(paramNode.length);
        for (String param : paramNode) {
            String[] pp = param.split("=");
            paramMap.put(pp[0], pp[1]);
        }
        return paramMap;
    }

    private static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }
}

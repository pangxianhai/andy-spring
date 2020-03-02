package com.andy.spring.util;

import com.andy.spring.constant.Constant;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.CollectionUtils;

/**
 * xml解析工具
 *
 * @author 庞先海 2019-11-14
 */
public class XmlUtil {

    private static final List<String> IGNORE_FIELD = Arrays.asList("serialVersionUID");

    public static Map<String, Object> fromXml(String xmlText) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Document doc = DocumentHelper.parseText(xmlText);
            Element rootElt = doc.getRootElement();
            parseElement(rootElt, map);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static <T> T fromXml(String xmlText, Class<T> clazz) {
        return fromXml(xmlText, clazz, Constant.DEFAULT_CHARSET);
    }

    public static <T> T fromXml(String xmlText, Class<T> clazz, Charset charset) {
        T t = ObjectUtil.instantiateClass(clazz);
        try {
            Document doc = DocumentHelper.parseText(xmlText);
            Element rootElt = doc.getRootElement();
            parseElement(rootElt, t, charset);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static String toXml(Object object) {
        return XmlUtil.toXml(object, false, Constant.DEFAULT_PATTERN);
    }

    public static String toXml(Object object, String pattern) {
        return XmlUtil.toXml(object, false, DateTimeFormatter.ofPattern(pattern));
    }

    public static String toXml(Object object, boolean isCdata, DateTimeFormatter formatter) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(object.getClass().getSimpleName());
        parseObject(object, root, isCdata, formatter);
        return document.asXML();
    }

    public static String toXml(Map map, String rootName) {
        return XmlUtil.toXml(map, rootName, false);
    }

    public static String toXml(Map map, String rootName, boolean isCdata) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(rootName);
        for (Object key : map.keySet()) {
            Element childElement = root.addElement(key.toString());
            if (isCdata) {
                childElement.addCDATA(map.get(key).toString());
            } else {
                childElement.addText(map.get(key).toString());
            }
        }
        return document.asXML();
    }

    private static void parseObject(Object object, Element element, boolean isCdata, DateTimeFormatter formatter) {
        ReflectionUtil.doWithFields(object.getClass(), field -> {
            String name = field.getName();
            if (IGNORE_FIELD.contains(name)) {
                return;
            }
            try {
                Element childElement = element.addElement(name);
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                boolean typeIsBoolean = field.getType().equals(Boolean.class) || field.getType().equals(boolean.class);
                Method m;
                if (typeIsBoolean) {
                    m = object.getClass().getMethod("is" + name);
                } else {
                    m = object.getClass().getMethod("get" + name);
                }
                Object value = m.invoke(object);
                if (null == value) {
                    return;
                }
                if (value instanceof String || ClassUtil.isBaseType(value.getClass())) {
                    if (isCdata) {
                        childElement.addCDATA(String.valueOf(value));
                    } else {
                        childElement.addText(String.valueOf(value));
                    }
                } else if (value instanceof Date) {
                    if (isCdata) {
                        childElement.addCDATA(DateUtil.format((Date)value, formatter));
                    } else {
                        childElement.addText(DateUtil.format((Date)value, formatter));
                    }
                } else {
                    parseObject(value, childElement, isCdata, formatter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void parseElement(Element element, Map<String, Object> map) {
        List childElements = element.elements();
        if (CollectionUtils.isEmpty(childElements)) {
            return;
        }
        Map<String, Integer> nameCount = new HashMap<>(childElements.size());
        for (Object object : childElements) {
            Element child = (Element)object;
            if (! nameCount.containsKey(child.getName())) {
                nameCount.put(child.getName(), 1);
            } else {
                nameCount.put(child.getName(), nameCount.get(child.getName()) + 1);
            }
        }
        for (Object object : childElements) {
            Element child = (Element)object;
            if (CollectionUtils.isEmpty(child.elements())) {
                map.put(child.getName(), child.getData());
            } else {
                Map<String, Object> childMap = new HashMap<>(16);
                parseElement(child, childMap);
                Integer count = nameCount.get(child.getName());
                if (null == count || count <= 1) {
                    map.put(child.getName(), childMap);
                } else {
                    if (! map.containsKey(child.getName())) {
                        List<Map<String, Object>> list = new ArrayList<>(count);
                        list.add(childMap);
                        map.put(child.getName(), list);
                    } else {
                        ((List<Map<String, Object>>)map.get(child.getName())).add(childMap);
                    }
                }
            }
        }
    }

    private static void parseElement(Element element, Object object, Charset charset) {
        List childElements = element.elements();
        if (CollectionUtils.isEmpty(childElements)) {
            return;
        }
        for (Object childObj : childElements) {
            Element child = (Element)childObj;
            ReflectionUtil.doWithFields(object.getClass(), field -> {
                if (! field.getName().equals(child.getName())) {
                    return;
                }
                try {
                    String name = child.getName();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method m = object.getClass().getMethod("set" + name, field.getType());
                    if (ClassUtil.isBaseType(field.getType())) {
                        String o = (String)child.getData();
                        if (ClassUtil.isAssignable(field.getType(), Integer.class)) {
                            m.invoke(object, NumberUtil.parseInt(o));
                        } else if (ClassUtil.isAssignable(field.getType(), Long.class)) {
                            m.invoke(object, NumberUtil.parseLong(o));
                        } else if (ClassUtil.isAssignable(field.getType(), Double.class)) {
                            m.invoke(object, NumberUtil.parseDouble(o));
                        } else if (ClassUtil.isAssignable(field.getType(), Float.class)) {
                            m.invoke(object, NumberUtil.parseFloat(o));
                        } else if (ClassUtil.isAssignable(field.getType(), Short.class)) {
                            m.invoke(object, NumberUtil.parseShort(o));
                        } else if (ClassUtil.isAssignable(field.getType(), Character.class)) {
                            m.invoke(object, o.toCharArray()[0]);
                        } else if (ClassUtil.isAssignable(field.getType(), Byte.class)) {
                            m.invoke(object, o.getBytes()[0]);
                        } else if (ClassUtil.isAssignable(field.getType(), Boolean.class)) {
                            m.invoke(object, Boolean.parseBoolean(o));
                        }
                    } else if (ClassUtil.isAssignable(field.getType(), String.class)) {
                        Object o = child.getData();
                        m.invoke(object, o);
                    } else if (ClassUtil.isAssignable(field.getType(), Date.class)) {
                        Object o = child.getData();
                        m.invoke(object, DateUtil.parse((String)o, charset.name()));
                    } else if (ClassUtil.isAssignable(field.getType(), Timestamp.class)) {
                        Object o = child.getData();
                        Date date = DateUtil.parseDate((String)o, charset.name());
                        if (null != date) {
                            m.invoke(object, new Timestamp(date.getTime()));
                        }
                    } else {
                        Object o = ObjectUtil.instantiateClass(field.getType());
                        m.invoke(object, o);
                        parseElement(child, o, charset);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
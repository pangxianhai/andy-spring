package com.andy.spring.type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础类型定义-code为int
 *
 * @author 庞先海 2019-11-14
 */
public class BaseType implements Serializable {

    private static final long serialVersionUID = - 7682774577353002860L;
    /**
     * 编码
     */
    private int code;

    /**
     * 描述
     */
    private String desc;


    protected BaseType(Integer code, String desc) {
        if (code != null) {
            this.code = code;
            this.desc = desc;
        }
    }

    public BaseType() {

    }

    public static <T> List<T> getValues(Class<T> typeClass) {
        Field[] fields = typeClass.getDeclaredFields();
        List<T> values = new ArrayList<T>();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())) {
                try {
                    T v = (T)f.get(typeClass);
                    if (v instanceof BaseType) {
                        values.add(v);
                    }
                } catch (IllegalArgumentException e) {

                } catch (IllegalAccessException e) {

                }
            }
        }
        return values;
    }

    public static <T extends BaseType> T parse(List<T> values, Integer code) {
        if (code == null) {
            return null;
        }
        for (T v : values) {
            if (v.getCode().intValue() == code) {
                return v;
            }
        }
        String message = String.format("%d not found in type:%s", code, values.get(0).getClass().getName());
        throw new RuntimeException(message);
    }

    public BaseType cloneBaseType() {
        return new BaseType(this.code, this.desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! (o instanceof BaseType)) {
            return false;
        }

        BaseType baseType = (BaseType)o;

        return code == baseType.code;
    }

    @Override
    public int hashCode() {
        return code;
    }

    public final Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "{" + "code=" + code + ", desc='" + desc + '\'' + '}';
    }
}

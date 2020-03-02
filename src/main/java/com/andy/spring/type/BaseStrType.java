package com.andy.spring.type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础类型定义-code为字符串
 *
 * @author 庞先海 2019-11-14
 */
public class BaseStrType implements Serializable {

    private static final long serialVersionUID = - 2588114755335638146L;
    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;


    protected BaseStrType(String code, String desc) {
        if (code != null) {
            this.code = code;
            this.desc = desc;
        }
    }

    public BaseStrType() {

    }

    public static <T> List<T> getValues(Class<T> typeClass) {
        Field[] fields = typeClass.getDeclaredFields();
        List<T> values = new ArrayList<T>();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())) {
                try {
                    T v = (T)f.get(typeClass);
                    if (v instanceof BaseStrType) {
                        values.add(v);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    continue;
                }
            }
        }
        return values;
    }

    public static <T extends BaseStrType> T parse(List<T> values, String code) {
        if (code == null) {
            return null;
        }
        for (T v : values) {
            if (code.equals(v.getCode())) {
                return v;
            }
        }
        String message = String.format("%s not found in type:%s", code, values.get(0).getClass().getName());
        throw new RuntimeException(message);
    }

    public BaseStrType cloneBaseStrType() {
        return new BaseStrType(this.code, this.desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! (o instanceof BaseStrType)) {
            return false;
        }

        BaseStrType baseStrType = (BaseStrType)o;

        return baseStrType.code.equals(this.getCode());
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    public final String getCode() {
        return code;
    }

    public void setCode(String code) {
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

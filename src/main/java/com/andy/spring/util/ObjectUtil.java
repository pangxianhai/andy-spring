package com.andy.spring.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * object 工具类
 *
 * @author 庞先海 2019-11-14
 */
public class ObjectUtil {

    private static Map<Class, Object> INIT_VALUE = new IdentityHashMap<>(8);

    static {
        INIT_VALUE.put(Boolean.class, false);
        INIT_VALUE.put(Byte.class, (byte)0);
        INIT_VALUE.put(Character.class, ' ');
        INIT_VALUE.put(Double.class, 0.0);
        INIT_VALUE.put(Float.class, 0.0F);
        INIT_VALUE.put(Integer.class, 0);
        INIT_VALUE.put(Long.class, 0L);
        INIT_VALUE.put(Short.class, (short)0);
    }

    /**
     * clazz 实例化一个对象
     */
    public static <T> T instantiateClass(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new RuntimeException(clazz.getName() + " is an interface");
        }
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Class<?>[] parameterTypes = null;
            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameterTypes0 = constructor.getParameterTypes();
                if (parameterTypes0.length == 0) {
                    return instantiateClass((Constructor<T>)constructor);
                }
                if (parameterTypes == null || parameterTypes0.length < parameterTypes.length) {
                    parameterTypes = parameterTypes0;
                }
            }
            if (null == parameterTypes) {
                return null;
            }
            //没有无参构造函数 找一个参数最少的构造函数
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++ i) {
                Class<?> ac = parameterTypes[i];
                boolean isBase = false;
                for (Class ic : INIT_VALUE.keySet()) {
                    if (ClassUtil.isAssignable(ac, ic)) {
                        args[i] = INIT_VALUE.get(ic);
                        isBase = true;
                        break;
                    }
                }
                if (! isBase) {
                    args[i] = null;
                }
            }
            return instantiateClass(clazz.getDeclaredConstructor(parameterTypes), args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(clazz.getName() + " no default constructor found", e);
        }
    }

    /**
     * clazz 实例化一个对象
     *
     * @param args 构造函数参数列表
     */
    public static <T> T instantiateClass(Class<T> clazz, Object... args) {
        if (clazz.isInterface()) {
            throw new RuntimeException(clazz.getName() + " is an interface");
        }
        try {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; ++ i) {
                parameterTypes[i] = args[i].getClass();
            }
            return instantiateClass(clazz.getDeclaredConstructor(parameterTypes), args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(clazz.getName() + " no default constructor found", e);
        }
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 == null || o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }


    public static byte[] serializationObject(Object object) {
        if (object == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(object);
            byte[] bytes = bao.toByteArray();
            oos.close();
            bao.close();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserializationObject(byte[] value) {
        if (value == null) {
            return null;
        }
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(value);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object object = ois.readObject();
            ois.close();
            bis.close();
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
        try {
            ReflectionUtil.makeAccessible(ctor);
            return ctor.newInstance(args);
        } catch (InstantiationException e) {
            throw new RuntimeException(ctor.getName() + " is an abstract class?", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(ctor.getName() + " is the constructor accessible?", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(ctor.getName() + " illegal arguments for constructor", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(ctor.getName() + " constructor threw exception", e);
        }
    }

}

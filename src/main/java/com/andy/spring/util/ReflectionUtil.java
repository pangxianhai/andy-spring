package com.andy.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * object 反射工具类
 *
 * @author 庞先海 2019-11-14
 */
public class ReflectionUtil {

    private final static Map<Class<?>, Method[]> DECLARED_METHODS_CACHE = new ConcurrentHashMap<Class<?>, Method[]>(
        256);

    private final static Map<Class<?>, Field[]> DECLARED_FIELDS_CACHE = new ConcurrentHashMap<Class<?>, Field[]>(256);

    /**
     * 遍历clazz类里所有的method 包括父类 和接口
     *
     * @param clazz    类类型
     * @param consumer 回调
     */
    public static void doWithMethods(Class<?> clazz, Consumer<Method> consumer) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            consumer.accept(method);
        }
        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), consumer);
        } else if (clazz.isInterface()) {
            for (Class<?> inter : clazz.getInterfaces()) {
                doWithMethods(inter, consumer);
            }
        }
    }

    /**
     * 遍历clazz类里所有的属性
     *
     * @param clazz    类类型
     * @param consumer 回调
     */
    public static void doWithFields(Class<?> clazz, Consumer<Field> consumer) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                consumer.accept(field);
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }

    /**
     * 将method 设置为可访问
     */
    public static void makeAccessible(Method method) {
        boolean canSet = (! Modifier.isPublic(method.getModifiers()) || ! Modifier.isPublic(
            method.getDeclaringClass().getModifiers())) && ! method.isAccessible();
        if (canSet) {
            method.setAccessible(true);
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        boolean canSet =
            (! Modifier.isPublic(ctor.getModifiers()) || ! Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
            && ! ctor.isAccessible();
        if (canSet) {
            ctor.setAccessible(true);
        }
    }

    public static void makeAccessible(Field field) {
        boolean canSet =
            (! Modifier.isPublic(field.getModifiers()) || ! Modifier.isPublic(field.getDeclaringClass().getModifiers())
             || Modifier.isFinal(field.getModifiers())) && ! field.isAccessible();
        if (canSet) {
            field.setAccessible(true);
        }
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) throws SecurityException {
        Method[] result = DECLARED_METHODS_CACHE.get(clazz);
        if (result != null) {
            return result;
        }
        Method[] declareMethods = clazz.getDeclaredMethods();
        List<Method> concreteMethods = findConcreteMethodsOnInterfaces(clazz);
        if (concreteMethods.size() > 0) {
            result = new Method[declareMethods.length + concreteMethods.size()];
            System.arraycopy(declareMethods, 0, result, 0, declareMethods.length);
            int index = declareMethods.length;
            for (Method m : concreteMethods) {
                result[index] = m;
                index++;
            }
        } else {
            result = declareMethods;
        }
        DECLARED_METHODS_CACHE.put(clazz, result);
        return result;
    }

    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> methods = new LinkedList<>();
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (! Modifier.isAbstract(ifc.getModifiers())) {
                    methods.add(ifcMethod);
                }
            }
        }
        return methods;
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields = DECLARED_FIELDS_CACHE.get(clazz);
        if (fields == null) {
            fields = clazz.getDeclaredFields();
            DECLARED_FIELDS_CACHE.put(clazz, fields);
        }

        return fields;
    }
}

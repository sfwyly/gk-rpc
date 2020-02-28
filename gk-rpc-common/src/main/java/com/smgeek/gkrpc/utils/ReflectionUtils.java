package com.smgeek.gkrpc.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ReflectionUtils
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/25 16:22
 * @Version 1.0
 **/
public class ReflectionUtils {
    /**
     * 根据class创建对象
     *
     * @param clazz 待创建兑现骨干的类
     * @param <T>   对象类型
     * @return 创建好的对象
     */
    public static <T> T newInstance(Class<T> clazz) throws IllegalStateException{

        try {
            return clazz.newInstance();
        } catch (Exception e) {
           throw new IllegalStateException(e);
        }
    }

    /**
     * 获取某个class的共有方法
     *
     * @param clazz
     * @return 当前类声明的共有方法
     */
    public static Method[] getPublicMethods(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> pmethods = new ArrayList<>();
        for (Method m : methods) {
            if (Modifier.isPublic(m.getModifiers())) {
                pmethods.add(m);
            }
        }
        return pmethods.toArray(new Method[0]);
    }

    /**
     * 调用指定对象的方法
     *
     * @param obj    被调用的对象
     * @param method 被调用的方法
     * @param args   方法的参数
     * @return 返回结果
     */
    public static Object invoke(Object obj, Method method, Object... args) throws IllegalStateException{
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
           throw new IllegalStateException(e);
        }
    }

}

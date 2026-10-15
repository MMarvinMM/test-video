package com.chinatower.receive.core.util;
import org.springframework.cglib.proxy.Enhancer;
import java.lang.reflect.Proxy;

public class ProxyClassUtils {

    /**
     * 获取对象的原始类（排除代理类干扰）
     */
    public static Class<?> getOriginalClass(Object obj) {
        if (obj == null) {
            return null;
        }

        Class<?> clazz = obj.getClass();

        // 处理 CGLIB 代理（Spring 动态代理）
        if (Enhancer.isEnhanced(clazz)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Enhancer.isEnhanced(superClass)) {
                return superClass;
            }
        }

        // 处理 JDK 动态代理（基于接口）
        if (Proxy.isProxyClass(clazz)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                return interfaces[0];
            }
        }

        return clazz;
    }

    /**
     * 获取对象原始类的简单类名（不含包名，不含代理后缀）
     */
    public static String getOriginalSimpleName(Object obj) {
        Class<?> originalClass = getOriginalClass(obj);
        return originalClass != null ? originalClass.getSimpleName() : null;
    }

    /**
     * 比对对象原始类的简单类名是否与目标名称一致
     */
    public static boolean matchesOriginalSimpleName(Object obj, String targetSimpleName) {
        String originalSimpleName = getOriginalSimpleName(obj);
        return originalSimpleName != null && originalSimpleName.equals(targetSimpleName);
    }
}

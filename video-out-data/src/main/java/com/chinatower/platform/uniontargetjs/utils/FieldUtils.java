package com.chinatower.platform.uniontargetjs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>
 * 类说明：操作实体对象属性工具类
 * <p>
 *
 * @author luo.zhenghong
 * @date 2022/7/21
 * <p>
 * ------------------------------------------------------------
 */
@Slf4j
public class FieldUtils {

    private FieldUtils() {

    }

    /**
     * 获取属性值不为空的属性名
     * @param obj 实体
     * @return
     */
    public static List<String> getNonNullPropertyNames(Object obj) {
        BeanWrapper src = new BeanWrapperImpl(obj);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        List<String> emptyNames = new ArrayList<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null && !"class".equals(pd.getName())) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames;
    }


    /**
     * 根据属性名获取属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getFieldValueByFieldName(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            log.error("获取指定属性名称错误!", e);
            return null;
        }
    }

    /**
     * 通过invoke getter 对应属性获取属性值
     * @param o 实体对象
     * @param fieldName 属性值
     * @return
     */
    public static Object invokeMethodGetFieldValue(Object o, String fieldName) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            log.error("获取属性值失败", e);
            return null;
        }
    }

    /**
     * 获取属性值不为空的属性名和属性值
     * @param obj 实体
     * @return key为属性名称, value为属性值
     */
    public static Map<String, Object> getNonNullPropertyMap(Object obj) {
        BeanWrapper src = new BeanWrapperImpl(obj);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Map<String, Object> map = new HashMap<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null
                    && !Objects.equals("class", pd.getName())
                    && !Objects.equals("deviceCode", pd.getName())
                    && !Objects.equals("monitorTime", pd.getName())
                    && !Objects.equals("collectionName", pd.getName())) {
                map.put(pd.getName(), srcValue);
            }
        }
        return map;
    }

}

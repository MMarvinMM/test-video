package com.chinatower.platform.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConvertUtil {
    /**
     * 处理对象转换
     * @param s
     * @param t
     * @param <S>
     * @param <T>
     * @return
     */
    public  static <S,T> T convert(S s, T t){
        BeanUtils.copyProperties(s,t);
        return t;
    }

    /**
     * 处理对象转换
     * @param s 源
     * @param clazz 目标
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S,T> T convert(S s, Class<T> clazz){
        T t = null;
        try{
            t = clazz.newInstance();
            BeanUtils.copyProperties(s,t);
        }catch (Exception e){
            e.printStackTrace();
        }
        return t;
    }
    // eg: Target t = convert(s,Target.class);

    /**
     * List集合转换，列表中的每一个实体转换目标类
     * @param iterable
     * @param clazz
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S,T> List<T> convert(Iterable<S> iterable, Class<T> clazz){
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(s -> convert(s, clazz))
                .collect(Collectors.toList());
    }
    // eg: List<Target> targets = convert(srcCollection,Target.class);
}

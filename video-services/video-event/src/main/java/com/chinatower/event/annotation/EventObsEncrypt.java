package com.chinatower.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: obsurl加密
 * @Author: 张帅
 * @Date: 2022/06/03 00:54
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventObsEncrypt {
}

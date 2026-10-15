package com.chinatower.video.modules.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设备订购判断切面
 * @return:
 * @author: zheng.yan
 * @date: 2022/3/16
 * @version:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeviceOrdAuthScope
{
}

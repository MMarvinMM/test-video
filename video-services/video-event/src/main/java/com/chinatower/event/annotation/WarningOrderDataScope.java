package com.chinatower.event.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderDataScope.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/25 17:05
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WarningOrderDataScope {
    /**
     * 用户表的别名
     */
    String userAlias() default "u";

    /**
     * 创建用户字段名称
     */
    String userColumn() default "create_by";
}

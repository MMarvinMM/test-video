package com.chinatower.airline.anno;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liujingyang
 */
@Documented
@Constraint(validatedBy = EnumTypeValidator.class)
@Target({ ElementType.FIELD ,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumTypeValid {
    
    String message() default "";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    // 新增字段，用于指定枚举类
    Class<?> enumClass() ;
}

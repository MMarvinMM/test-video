package com.chinatower.airline.anno;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

/**
 * @author liujingyang
 */
public class EnumTypeValidator implements ConstraintValidator<EnumTypeValid, Integer> {
    
    private Class<?> enumClass;
    
    @Override
    public void initialize(EnumTypeValid constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }
    
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            // 空值不校验
            return true;
        }
        
        try {
            // 使用反射调用 GenderEnum.isValid(value)
            Method method = enumClass.getMethod("isValid", int.class);
            Boolean result = (Boolean) method.invoke(null, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}

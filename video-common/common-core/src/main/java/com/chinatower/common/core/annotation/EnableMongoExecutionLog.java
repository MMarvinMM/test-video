package com.chinatower.common.core.annotation;

import com.chinatower.common.core.config.MongoExecutionRegistrar;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MongoExecutionRegistrar.class)
@EnableAspectJAutoProxy
public @interface EnableMongoExecutionLog {
    
}
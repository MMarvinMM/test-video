package com.chinatower.platform.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 * EnableAspectJAutoProxy 表示通过aop框架暴露该代理对象,AopContext能够访问
 * @author iwhalecloud
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@Configuration
public class ApplicationConfig {

}

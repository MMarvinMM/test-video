package com.chinatower.common.core.config;

import com.chinatower.common.core.annotation.Trace;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Component
public class TraceAdvisor extends AbstractPointcutAdvisor
implements BeanFactoryAware{


    @Resource
    TraceInteceptor traceInteceptor;

    @PostConstruct
    public void init() {
      log.info("advisor 启动成功");
    }

    @Override
    public Pointcut getPointcut() {
        return AnnotationMatchingPointcut.forMethodAnnotation(Trace.class);
    }

    @Override
    public Advice getAdvice() {
        return traceInteceptor;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.getAdvice() instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.getAdvice()).setBeanFactory(beanFactory);
        }
    }
}

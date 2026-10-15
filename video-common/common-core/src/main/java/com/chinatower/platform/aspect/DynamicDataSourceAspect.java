package com.chinatower.platform.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName DynamicDataSourceAspect
 * @Deacription 数据源动态切换
 * @Author chenmf
 * @Date 2022/3/11 10:51
 * @Version 1.0
 **/
@Aspect
@Component
@Slf4j
public class DynamicDataSourceAspect {

    private static final String DYNAMIC_PREFIX = "#";

    @Pointcut("execution(* com.chinatower.platform.deal.DataTypeDeal.*(..))")
    public void aspect() {
        log.info("动态数据源切换操作");
    }

    @Before("aspect()")
    private void doBefore(JoinPoint point) {
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        Class<?> classz = target.getClass();// 获取目标类
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature())
                .getMethod().getParameterTypes();
        try {
            Method m = classz.getMethod(method, parameterTypes);
            if (m != null && m.isAnnotationPresent(DS.class)) {
                DS data = m.getAnnotation(DS.class);
                log.info("method :{},datasource:{}", m.getName(), data.value());
                String value = data.value();

                Object[] args = point.getArgs();
                //获取被拦截方法参数名列表(使用Spring支持类库)
                LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
                String[] paraNameArr = localVariableTable.getParameterNames(m);
                //使用SPEL进行key的解析
                ExpressionParser parser = new SpelExpressionParser();
                //SPEL上下文
                StandardEvaluationContext context = new StandardEvaluationContext();
                //把方法参数放入SPEL上下文中
                for (int i = 0; i < paraNameArr.length; i++) {
                    context.setVariable(paraNameArr[i], args[i]);
                }
                // 使用变量方式传入业务动态数据
                if (value.startsWith(DYNAMIC_PREFIX)) {
                    value = parser.parseExpression(value).getValue(context, String.class);
                }
                DynamicDataSourceContextHolder.push(value);
            }
        } catch (Exception e) {
            log.error("get datasource error ", e);
            DynamicDataSourceContextHolder.push("");
        }
    }

    @After("aspect()")
    public void after(JoinPoint point) {
        DynamicDataSourceContextHolder.poll();
    }


    @AfterThrowing(pointcut = "aspect()", throwing = "ex")
    public void doException(JoinPoint joinPoint,Exception ex) {
        log.error("业务异常:",ex);
        DynamicDataSourceContextHolder.poll();
    }

}

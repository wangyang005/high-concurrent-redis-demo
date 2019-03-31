package com.high.concurrent.redis.demo.highconcurrentredis.aop;

import com.high.concurrent.redis.demo.highconcurrentredis.annotation.Cache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 定义一个缓存切面，实现逻辑：如果方法贴上了Cache注解，就表明使用缓存策略
 * @Author daituo
 * @Date 2019-3-31
 **/
@Component
@Aspect
@Slf4j
public class CacheAop {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 使用环绕通知@Around增强，并通过注解@annotation(xxx)定义切入点
     * @param joinPoint 切点
     */
    @Around(value = "@annotation(com.high.concurrent.redis.demo.highconcurrentredis.annotation.Cache)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object key = null;
        log.info("<<<<<<<<<<<<<<<<<<进入@Around增强");
        /** 1.从注解中获取key */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //获取方法签名,签名中包含了方法的一些信息
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(),signature.getParameterTypes());  //获取方法实例
        Cache annotation = method.getAnnotation(Cache.class);   //获取方法上的cache注解
        String keyEL = annotation.key();

        /** 2.通过springEL表达式解析key，因为定义key时，使用了EL表达式 */
        ExpressionParser parser = new SpelExpressionParser();   //创建解析器
        Expression expression = parser.parseExpression(keyEL);  //解析注解的key
        EvaluationContext context = new StandardEvaluationContext();    //设置解析上下文，配置占位符及其对应的值
        /** 把占位符与值一一对应起来*/
        Object[] args = joinPoint.getArgs();    //获取所有的参数值
        String[] parameterNames = signature.getParameterNames();    //获取所有的参数名
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        key = expression.getValue(context).toString();  //获取最终EL解析完后的key值

        /** 3.根据key判断缓存中是否存在 */
        Object value = redisTemplate.opsForValue().get(key);
        if (null != value) {
            log.info("<<<<<<<<<<<从缓存中获取到ticket:{}", value.toString());
            return value;
        }

        /** 4.调用真实的方法，从数据库中查询*/
        value = joinPoint.proceed();

        /** 5.同步存储到redis缓存中 */
        redisTemplate.opsForValue().set(key, value);
        return value;
    }
}

package com.high.concurrent.redis.demo.highconcurrentredis.annotation;

import java.lang.annotation.*;

/**
 * AOP+注解：实现自定义缓存注解，操作日志和审计日志也可以通过该方式实现
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    /**
     * 定义缓存key的规则
     */
    String key() default "";
}

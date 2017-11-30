package com.free.dquery.config;

import com.free.dquery.handle.DQueryHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zhangzhidong
 * @date 2017/11/27
 */
@Aspect
@Configuration
public class DynamicSqlConfig {

    @Resource
    SessionFactory sessionFactory;

    @Bean(name = "sessionFactory")
    public SessionFactory sessionFactory( HibernateEntityManagerFactory hemf){
        return hemf.getSessionFactory();
    }

    @Pointcut(value = "execution(* com.free.dquery.dao..*(..))")
    public void controllerMethodPointcut(){
    }

    @Around("controllerMethodPointcut()")
    public Object Interceptor(ProceedingJoinPoint pjp) throws Throwable {

        return new DQueryHandler(sessionFactory).handler(pjp);
    }
}

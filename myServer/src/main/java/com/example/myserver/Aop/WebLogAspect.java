package com.example.myserver.Aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


/**
 * 描述：打印请求和响应信息
 */
@Aspect
@Component
public class WebLogAspect {

    private final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(public * com.example.myserver.controller.*.*(..))")
    public void webLog(){

    }

    //捕获，并打印请求的相关信息
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        //收到请求，记录请求内容
        ServletRequestAttributes attributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=attributes.getRequest();
        //打印请求的url
        log.info("URL : " + request.getRequestURL().toString());
        //打印请求方法
        log.info("HTTP_METHOD : " +request.getMethod());
        //打印请求的Ip地址
        log.info("IP : " + request.getRemoteAddr());
        //打印请求处理类的一些信息
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        //打印请求的参数
        log.info("ARGS : "+ Arrays.toString(joinPoint.getArgs()));
    }
    //捕获，并打印响应相关的信息
    @AfterReturning(returning = "res",pointcut = "webLog()")
    public void doAfterReturning(Object res) throws JsonProcessingException {
        //处理完请求，返回内容
        log.info("RESPONSE : "+new ObjectMapper().writeValueAsString(res));
    }

}



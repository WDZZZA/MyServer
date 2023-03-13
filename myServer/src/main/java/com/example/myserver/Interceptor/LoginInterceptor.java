package com.example.myserver.Interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.alibaba.fastjson.JSONObject;
//拦截器，可以在请求到达api层之前，先进行一层校验（所以适用于通用的校验比较好）
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        logger.info("拦截器1 最后执行");
        OutputStream outputStream =response.getOutputStream();


    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        logger.info("拦截器1 在控制器执行之后执行");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //通过流读取到json格式的数据(还要配置两个类：一个是过滤器，一个是重写的wrapper类）
        /*logger.info("拦截器1 在控制器执行之前执行");
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        String s=streamReader.readLine();
        logger.info(s);*/
        return true;
    }
}


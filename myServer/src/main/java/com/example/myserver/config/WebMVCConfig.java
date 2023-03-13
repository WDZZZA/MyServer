package com.example.myserver.config;

import com.example.myserver.Interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Resource
    LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器配置信息
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**");
    }
}

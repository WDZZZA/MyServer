package com.example.myserver.Filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Component
//该注解的作用就是过滤器的注册配置信息
@WebFilter(urlPatterns = "/Filter/*", filterName = "myTestFilter")
@Slf4j
public class MyTestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("[ {} ] 创建啦...", this.getClass().getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("[ {} ] 执行啦...", this.getClass().getSimpleName());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("[ {} ] 被摧毁啦...", this.getClass().getSimpleName());
    }
}
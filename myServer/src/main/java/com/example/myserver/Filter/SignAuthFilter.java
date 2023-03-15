package com.example.myserver.Filter;

import com.alibaba.druid.util.StringUtils;
import com.example.myserver.Util.SignUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author 大橙子
 * @deprecated 用于做防重放+防篡改
 * @date 2023/3/14 11:15
 */

@Slf4j
//记录过滤器的一个大坑：@WebFilter与@Componment注解不能同时使用,因为这样会初始化两次过滤，一次是全局，一次是筛选的url
@WebFilter(urlPatterns = "/doubleWeek/pwd444")
public class SignAuthFilter implements Filter {



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("初始化 SignAuthFilter...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 过滤不需要签名验证的地址，目前应该不需要
       // String requestUri = httpRequest.getRequestURI();
        /*for (String ignoreUri : signAuthProperties.getIgnoreUri()) {
            if (requestUri.contains(ignoreUri)) {
                log.info("当前URI地址：" + requestUri + "，不需要签名校验！");
                chain.doFilter(request, response);
                return;
            }
        }
        if(requestUri.equals("/swagger-ui.html")){
            log.info("当前URI地址：" + requestUri + "，不需要签名校验！");
            chain.doFilter(request, response);
            return;
        }*/
            // 获取签名和时间戳(此时签名中包含时间戳，请求头中有一份时间戳)
            String sign = httpRequest.getHeader("Sign");
            String timestampStr = httpRequest.getHeader("Timestamp");
            if (StringUtils.isEmpty(sign)) {
                responseFail("签名不能为空", response);
                return;
            }
            if (StringUtils.isEmpty(timestampStr)) {
                responseFail("时间戳不能为空", response);
                return;
            }

            // 重放时间限制
            long timestamp = Long.parseLong(timestampStr);
            if (System.currentTimeMillis() - timestamp >= 0) {
                responseFail("签名已过期", response);
                return;
            }
            // 校验签名，重点是校验外面的参数与时间戳是否与签名的内容是否相同
            //下面这个方法可以让拦截器直接读取请求体里的参数(并形成map集合)
            Map<String, String[]> parameterMap = httpRequest.getParameterMap();
            //该签名中包含着    上送的参数+时间戳
            if (SignUtils.verifySign(parameterMap, sign, timestampStr)) {
                chain.doFilter(httpRequest, response);
            } else {
                responseFail("签名校验失败", response);
            }
        }


    /**
     * 响应错误信息
     */
    private void responseFail(String msg, ServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println(msg);
        out.flush();
        out.close();
    }

    @Override
    public void destroy() {
        log.info("销毁 SignAuthFilter...");
    }
}

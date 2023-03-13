package com.example.myserver.Filter;
import com.example.myserver.Common.MyServletRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @描述：用来处理请求拦截器中读取流数据只能读取一次问题
 * @创建人 caoju
 */

@Component
public class RequestReplaceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof MyServletRequestWrapper)) {
            request = new MyServletRequestWrapper(request);
        }
        filterChain.doFilter(request, response);

        /*//如果有文件上传的业务场景，需要用下面的代码进行处理，不然文件上传的流会有问题
        String contentType = request.getContentType();
        //如果contentType是空
        //或者contentType是多媒体的上传类型则忽略，不进行包装，直接return
        if (contentType == null) {
            filterChain.doFilter(request, response);
            return;
        }else if(request.getContentType().startsWith("multipart/")){
            filterChain.doFilter(request, response);
            return;
        }else if (!(request instanceof MyServletRequestWrapper)) {
            request = new MyServletRequestWrapper(request);
        }
        filterChain.doFilter(request, response);
        */
    }
}
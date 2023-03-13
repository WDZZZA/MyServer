package com.example.myserver.Util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Yanchi {
    //表明使用哪个线程池来执行该异步方法
    @Async("asyncTaskExecutor")
    public void Ther(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        System.out.println("延迟中。。。");
    }
}

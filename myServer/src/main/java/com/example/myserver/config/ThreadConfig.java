package com.example.myserver.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
// @PropertySource是找的target目录下classes目录下的文件，resources目录下的文件编译后会生成在classes目录
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound=false, encoding="UTF-8")
@Slf4j
public class ThreadConfig {
    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;
    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;
    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;
    @Value("${async.executor.thread.name.prefix}")
    private String namePrefix;
    @Value("${async.executor.thread.keep_alive_seconds}")
    private int keepAliveSeconds;
    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        log.info("启动");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 任务队列大小
        executor.setQueueCapacity(queueCapacity);
        // 线程前缀名
        executor.setThreadNamePrefix(namePrefix);
        // 线程的空闲时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程初始化
        executor.initialize();
        return executor;
    }
}
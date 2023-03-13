package com.example.myserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.InetSocketAddress;

//fegin启动
@Slf4j
@EnableFeignClients
@SpringBootApplication
//eureka启动
@EnableEurekaServer
//线程池配置启动
@EnableAsync
//swagger配置启动
@EnableSwagger2
public class MyServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyServerApplication.class, args);
    }

}

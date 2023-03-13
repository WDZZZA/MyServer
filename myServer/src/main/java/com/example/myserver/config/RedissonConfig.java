package com.example.myserver.config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;

    //@Value("${spring.redis.password}")
    //private String password;

    /**
     * RedissonClient,单机模式
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }
}


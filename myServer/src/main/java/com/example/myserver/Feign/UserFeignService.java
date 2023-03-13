package com.example.myserver.Feign;

import com.example.myserver.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-eureka-wdc")
public interface UserFeignService {
    @GetMapping("/user/login")
     String login(@RequestBody User user);
}

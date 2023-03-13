package com.example.myserver.Dao.Dao1;

import com.example.myserver.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    @Cacheable(cacheNames = "用户信息插入缓存")
    int insertuser(User user);
    List<User> selectbyusername(String username);
    int updateuser(User user);
    List<User> selAll();
    @Cacheable(cacheNames = "用户查询结果缓存")
    List<User> selectbynamepws(String username,String password);
    List<User> selectByPhone(String phone);
}

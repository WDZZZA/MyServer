package com.example.myserver.Dao.Dao1;

import com.example.myserver.Request.MiaoShaReq;
import com.example.myserver.entity.Order;
import com.example.myserver.entity.Product;
import com.example.myserver.entity.SeckillOrder;
import com.example.myserver.entity.User;
import feign.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductMapper {
    int insertProduct(Product product);

    //第一次查询用数据库，后米都用缓存了
    Product selectByGroupId(String productid);

    SeckillOrder selectByUserPro(String userid, String productid);
//该方法加了一个版本号，即当库存减1后，版本就会更新（+1）
    //并且每次更新数据后同时在redis中更新一下，从而两者保持一至

    int reduceProduct(String productid);

    void createOrder(MiaoShaReq miaoShaReq);
}

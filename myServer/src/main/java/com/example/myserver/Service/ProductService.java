package com.example.myserver.Service;

import com.example.myserver.Dao.Dao1.ProductMapper;
import com.example.myserver.Dao.Dao1.UserMapper;
import com.example.myserver.Dao.Dao2.SysMapper;
import com.example.myserver.Request.EmailRequest;
import com.example.myserver.Request.MiaoShaReq;
import com.example.myserver.Request.PageRequest;
import com.example.myserver.Response.LoginResp;
import com.example.myserver.entity.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.w3c.dom.css.CSSStyleSheet;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;


import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@CacheConfig(cacheNames = "user")
//悲观锁案例
public class ProductService {
    @Resource
    ProductMapper productMapper;
    @Resource
    RedissonClient redissonClient;
    @Autowired
    RedisTemplate redisTemplate;
    private final static String LOCK_KEY = "RESOURCE_KEY";

    public String seckill(MiaoShaReq miaoShaReq) {
        //定义锁,这个锁是悲观锁
        RLock lock = redissonClient.getLock(LOCK_KEY);
        //下面这一句我看有些教程是要用的，但作者在这里将他注释掉了
        //lock.lock();
        //上面一句是新建一个锁，然后后面的tryLock就是争抢这个锁
        try {
            //尝试加锁,最大等待时间300毫秒，上锁30毫秒自动解锁
            if (lock.tryLock(300, 30, TimeUnit.MILLISECONDS)) {
                log.info("这位大哥获得了锁");
                //具体的秒杀流程：查库存，查订单，扣库存，生成订单
                //根据产品名称，查询产品信息
                Product product1 = productMapper.selectByGroupId(miaoShaReq.getProductid());
                Integer t=Integer.valueOf(product1.getProductnum());
                //当产品数量不够时
                if (t <= 0) {
                    return "产品数量已经不够了";
                }
                //根据用户信息和产品信息查询到对应的秒杀订单
                SeckillOrder order = productMapper.selectByUserPro(miaoShaReq.getUserid(), miaoShaReq.getProductid());
                //如果客户之前有这个订单，不能进行秒杀
                if (order != null) {
                    return "已经参加过了";
                }
                //进行秒杀
                String s = miaosha(miaoShaReq, product1);
                log.info("线程:" + Thread.currentThread().getName() + "获得了锁");
                return s;
            }
        } catch (Exception e) {
            log.error("程序执行异常:{}", e);
        } finally {
            //释放锁
            lock.unlock();
        }
        return "所有流程已经走完";
    }

    public String miaosha(MiaoShaReq miaoShaReq, Product product) {
        //减少商品数量
        productMapper.reduceProduct(miaoShaReq.getProductid());
        productMapper.createOrder(miaoShaReq);
        return "商品削减成功";
    }
}
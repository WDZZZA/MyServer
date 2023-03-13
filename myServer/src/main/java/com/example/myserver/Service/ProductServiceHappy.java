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
//乐观锁案例
public class ProductServiceHappy {
    @Resource
    ProductMapper productMapper;
    @Resource
    RedissonClient redissonClient;
    @Autowired
    RedisTemplate redisTemplate;
    private final static String LOCK_KEY = "RESOURCE_KEY";
    public String seckill(MiaoShaReq miaoShaReq) {
                Product product1 = productMapper.selectByGroupId(miaoShaReq.getProductid());
                //当产品数量不够时
        Integer t=Integer.valueOf(product1.getProductnum());
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
                String s=miaosha(miaoShaReq,product1);
                return "秒杀成功";
    }

    public String miaosha(MiaoShaReq miaoShaReq,Product product){
        //考虑在每次减少库存数这里加一个乐观锁（主要是在缓存这里加一个）
        //创建一个jedis对象
        Jedis jedis =new Jedis("127.0.0.1", 6379);
        String watchkeys = "watchkeys";
        //该键初始版本号为0
        jedis.set(watchkeys, "0");
        //watch方法用于监控目前使用watchkeys这个键的值，若这个键的值对不上，则会报错
        jedis.watch(watchkeys);
        //开启一个新事务
        Transaction tx = jedis.multi();
        //减少商品数量(目前不知道加的位置对不对)
        productMapper.reduceProduct(miaoShaReq.getProductid());
        //标识键自增1(相当于数据库中version中加了一个1)(先对数据处理，再版本号加1)
        tx.incr("watchkeys");
        //(目前的场景是有多个请求会抢夺这个方法），A请求先将watchkeys更新（还没提交事务），如果这时B事务也来用，他先提交事务的话就会watchkeys对应不上，也就会导致出错
        //exec方法就是提交事务，如果版本号对的上，那么就是有具体信息的，如果没有，那么就是null
        List<Object> list = tx.exec();// 提交事务，如果此时watchkeys被改动了，则返回null
        if(list!=null){
            //生成秒杀订单
            log.info("抢购成功，商品数量已被扣除");
            productMapper.createOrder(miaoShaReq);
            return "ssssss";
        }
        else {
            return "另一个方法强用该方法，导致数据错乱";
        }
    }
}
package com.example.myserver.entity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SeckillProduct {
    String seckill_product_id;
    //产品编号
    String productid;
    //秒杀铲平数量
    String seckill_product_num;
    //秒杀产品价格
    String seckill_product_price;
    //秒杀产品生效时间
    Date seckill_product_starttime;
    //秒杀产品失效时间
    Date seckill_product_endtime;
}

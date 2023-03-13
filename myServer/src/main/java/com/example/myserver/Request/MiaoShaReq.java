package com.example.myserver.Request;
import lombok.Data;

import java.io.Serializable;
@Data
//产品基本信息
//秒杀业务传进来的信息主要包含两个方面：用户信息，商品信息
public class MiaoShaReq implements Serializable {
    String productid;
    String productname;
    String productprice;
    String productnum;
    String username;
    String password;
    String phone;
    String email;
    String userid;
}

package com.example.myserver.entity;
import lombok.Data;

import java.io.Serializable;
@Data
public class Order {
    String orderid;
    String productid;
    String orderstatus;
    String orderpay;
    String createtime;
    String updatetime;
    String userid;
}

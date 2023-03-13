package com.example.myserver.entity;
import lombok.Data;

import java.io.Serializable;
@Data
//产品基本信息
public class Product implements Serializable {
    String productid;
    String productname;
    String productprice;
    String productnum;
    String version;
}
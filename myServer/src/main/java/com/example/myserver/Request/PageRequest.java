package com.example.myserver.Request;

import lombok.Data;

@Data
public class PageRequest {
    //当前页码
    int pageNum;
    //每页数量
    int pageSize;
}

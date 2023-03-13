package com.example.myserver.Request;

import lombok.Data;


@Data
public class AddCategoryReq {

        private String name;
        private Integer type;
        private Integer parentId;
        private Integer orderNum;
}

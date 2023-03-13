package com.example.myserver.entity;
import lombok.Data;

import java.io.Serializable;
@Data
//学生基本信息
public class Stu implements Serializable {
    private String id;
    private String name;
    private String age;
    private String gender;
}

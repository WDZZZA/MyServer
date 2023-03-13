package com.example.myserver.Response;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author tzw
 * @version 1.0
 * 返回结果
 */
@Data
@Accessors(chain = true)
public class Resp {
    //操作是否成功
    private Boolean success;
    //状态码
    private Integer code;
    //消息
    private  String message;


}


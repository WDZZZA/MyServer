package com.example.myserver.Response;

import lombok.Data;

/**
 * @author tzw
 * @version 1.0
 */
@Data
public class LoginResp extends Resp {
    private UserResp data;
}
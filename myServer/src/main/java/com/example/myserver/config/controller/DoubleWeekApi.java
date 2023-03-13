package com.example.myserver.config.controller;

import com.example.myserver.Service.DoubleWeekService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/doubleWeek")
@Api(tags = "双周分享作业")
public class DoubleWeekApi {
    @Autowired
    DoubleWeekService doubleWeekService;

    /**
     * 获取短信验证码
     *
     * @param mobileNo 用户手机号
     * @return
     */
    @ResponseBody
    @GetMapping("/sms1")
    public String getSMS(String mobileNo) {
        return doubleWeekService.getSMS(mobileNo);
    }

    /**
     * 校验短信验证码
     *
     * @param mobileNo 手机号码
     * @param code     短信验证码
     * @return
     */
    @ResponseBody
    @PostMapping("/sms2")
    public String checkSMS(String mobileNo, String code) {
        return doubleWeekService.checkSMS(mobileNo, code);
    }

    /**
     * 校验密码
     *
     * @param mobileNo 手机号
     * @param pwd      密码
     * @return 成功或失败
     */
    @ResponseBody
    @PostMapping("/pwd333")
    public String checkPwd(String mobileNo, String pwd) {
        return doubleWeekService.checkPwd(mobileNo, pwd);
    }

/**
     * 转账交易（需要先进行短信验证码与密码校验）
     *
     * @param content 交易要素
     * @return
     */
    @ResponseBody
    @PostMapping("/pwd444")
    //用来标注请求头(因为请求体的参数可以直接在参数中表现出来)
    @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String", paramType = "header")
    public String doTrans(String phoneIn,String phoneOut, String content, String token) {
        return doubleWeekService.doTrans( phoneIn, content, token, phoneOut);
    }
}

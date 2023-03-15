package com.example.myserver.config.controller;

import com.example.myserver.Service.DoubleWeekService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
/**
 * 实现功能：获得验证码，验证码校验(验证码登录)，密码登录，转账支付
 *
 * @describe:该项目已经满足以下几种安全规范
 * 1.防跳步(必须先登录，获得验证码才能实现转账功能)(用token共享)
 * 2.防止重复点击(存储redis，确保每一次实现功能需要间隔一段时间)
 * 3.防篡改，防重放(加上时间戳，确保防重放；加上数字签名，确保参数与数字签名一致，确保防篡改)
 * 4.次数限制(在一定时间内，只能实现确定次数的接口)
 * 5.事务的一致性(两个为事务的操作，必须确保一致性)pwd444接口
 *
 */
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Timestamp", value = "Timestamp", required = false, dataType = "Date", paramType = "header"),
            @ApiImplicitParam(name = "Sign", value = "Sign", required = false, dataType = "String", paramType = "header")
    })
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", required =false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "Timestamp", value = "Timestamp", required = false, dataType = "Date", paramType = "header"),
            @ApiImplicitParam(name = "Sign", value = "Sign", required = false, dataType = "String", paramType = "header")
    })
    public String doTrans(String phoneIn, String phoneOut, String content, HttpServletRequest request) {
        //获取头部的token标识
        String token = request.getHeader("token");
        return doubleWeekService.doTrans(phoneIn, content, token, phoneOut);
    }
}

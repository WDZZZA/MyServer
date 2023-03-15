package com.example.myserver.config.controller;

import com.example.myserver.Dao.Dao1.ProductMapper;
import com.example.myserver.Feign.UserFeignService;
import com.example.myserver.Jwt.JwtProdce;
import com.example.myserver.Request.EmailRequest;
import com.example.myserver.Request.MiaoShaReq;
import com.example.myserver.Request.PageRequest;
import com.example.myserver.Response.LoginResp;
import com.example.myserver.Service.*;
import com.example.myserver.Util.Yanchi;
import com.example.myserver.entity.Product;
import com.example.myserver.entity.Stu;
import com.example.myserver.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/user")
@Api(tags = "大橙子第一个测试")
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    LoginService loginService;
    @Autowired(required = false)
    UserFeignService userFeignService;
    @Resource
    Yanchi yanchi;
    @Autowired
    SkillService skillService;
    @Resource
    ProductMapper productMapper;
    @Autowired
    ProductService productService;
    @Autowired
    ProductServiceHappy productServiceHappy;
    @Autowired
    JwtProdce jwtProdce;




    //注册
    @PostMapping("/register")
    @ApiOperation(value = "注册",notes = "该接口仅用于测试")
    public String register(@RequestBody User user) {
        return loginService.register(user);
    }

    //登录
    @PostMapping("/login")
    @ApiOperation(value = "登录",notes = "该接口仅用于测试")
    public LoginResp login(@RequestBody User user) {
        //yanchi.Ther();
        return loginService.login2(user);
    }

    //分页查询用户数据
    @GetMapping("/selByPage")
    @ApiOperation(value = "分页查询用户数据",notes = "该接口仅用于测试")
    @ResponseBody
    public PageInfo<User> selByPage(PageRequest pageRequest) {
        PageInfo<User> pageInfo = loginService.selByPage(pageRequest);
        return pageInfo;
    }

    //fegin调用微服务接口
    @GetMapping("/feign")
    @ApiOperation(value = "fegin调用微服务接口",notes = "该接口仅用于测试")
    public String feign(@RequestBody User user) {
        return userFeignService.login(user);
    }

    //邮件发送服务
    @PostMapping("/mail")
    @ApiOperation(value = "邮件发送服务",notes = "该接口仅用于测试")
    public String mail(@RequestBody EmailRequest emailinfo) {
        return loginService.sendEmail(emailinfo);
    }

    //手机验证码服务
    @GetMapping("/phone")
    @ApiOperation(value = "手机验证码服务",notes = "该接口仅用于测试")
    public String phonecheck(@RequestParam(value = "phone") String phone) {
        return loginService.phonesend(phone);
    }

    //第二数据源服务
    @GetMapping("/db2")
    @ApiOperation(value = "第二数据源服务",notes = "该接口仅用于测试")
    public String db2Service(@RequestBody Stu stu) {
        return loginService.db2act(stu);
    }

    //redis分布式锁的实验（利用多线程来实验）
    @GetMapping("/feginlock")
    @ApiOperation(value = "redis分布式锁的实验",notes = "该接口仅用于测试")
    public void TestSkillService() {
        for (int i = 10; i < 60; i++) { //开50个线程
            SkillThread skillThread = new SkillThread(skillService, "skillThread->" + i);
            skillThread.start();
        }
        //redis分布式锁的实验（利用多请求来实验）
    }

    //redis分布式锁实验第二弹(结合下面的秒杀服务来处理)
    @GetMapping("/feginlocksss")
    @ApiOperation(value = "redis分布式锁实验第二弹",notes = "该接口仅用于测试")
    public void SkillService(@RequestBody MiaoShaReq miaoShaReq) {
        for (int i = 10; i < 60; i++) { //开50个线程
            SkillThreadMiaoSha skillThreadMiaoSha = new SkillThreadMiaoSha(productService, "skillThread->" + i);
            skillThreadMiaoSha.start();
        }
        //redis分布式锁的实验（利用多请求来实验）
    }

    //秒杀业务
    @GetMapping("/do_miaosha")
    @ApiOperation(value = "秒杀业务",notes = "该接口仅用于测试")
    public String miaosha(@RequestBody MiaoShaReq miaoShaReq) {
        //String t=productService.seckill(miaoShaReq);
        String t = productServiceHappy.seckill(miaoShaReq);
        return t;
    }

    //商品入库
    @PostMapping("/addproduct")
    @ApiOperation(value = "商品入库",notes = "该接口仅用于测试")
    public String addProduct(@RequestBody Product product) {
        productMapper.insertProduct(product);
        return "增加成功";
    }



    //使用jwt生成的token
    @PostMapping("/jwt")
    @ApiOperation(value = "使用jwt生成的token",notes = "该接口仅用于测试")
    public void jwt() {
        jwtProdce.JWTTest();
    }



}




   /* //忘记密码
    @GetMapping("/forlogin")
    public String forlogin(@RequestBody User user) {
        return loginService.login(user);
    }

    //手机验证码登录
    @GetMapping("/phonelogin")
    public String phonelogin(@RequestBody User user) {
        return loginService.login(user);
    }

}*/

package com.example.myserver.Service;


import com.example.myserver.Dao.Dao1.UserMapper;
import com.example.myserver.Dao.Dao2.SysMapper;
import com.example.myserver.Request.EmailRequest;
import com.example.myserver.Request.PageRequest;
import com.example.myserver.Response.LoginResp;
import com.example.myserver.entity.Stu;
import com.example.myserver.entity.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@CacheConfig(cacheNames = "user")
public class LoginService {
    private Logger logger = LoggerFactory.getLogger(LoginService.class);
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${spring.mail.username}")
    private String from;
    @Resource
    private JavaMailSender javaMailSender;
    @Autowired
    private SysMapper sysMapper;
    @Autowired
    private RedissonClient redissonClient;
    /*public String login(User user) {
        String password = user.getPassword();
        String username = user.getUsername();
        User redisuser = (User) redisTemplate.opsForValue().get(username);
        if (redisuser.getUsername().equals("")) {
            User userinfo = userMapper.selectbyusername(username);
            if (userinfo.getUsername().equals(null)) {
                return "没有该用户，请注册";
            }
            if (!userinfo.getPassword().equals(password)) {
                return "账户或密码错误";
            }
            redisTemplate.opsForValue().set(username, userinfo);
            logger.info(userinfo.getPassword());
            return "登录成功";
        }
        if (!redisuser.getUsername().equals(username)) {
            return "账户或密码错误";
        }
        return "登录成功";
    }*/

    public LoginResp login2(User user) {
        LoginResp loginResp = new LoginResp();
        logger.info("查询数据库");
        //判断用户信息是否为空
        if (user == null) {
            return (LoginResp) loginResp.setSuccess(false).setMessage("用户名或密码不存在");
        }
        String username = user.getUsername();
        String password = user.getPassword();
        //根据密码查询用户信息
        List<User> userinfo = userMapper.selectbynamepws(username, password);
        //查询不到
        if (userinfo == null) {
            return (LoginResp) loginResp.setSuccess(false).setMessage("输入密码和用户名错误");
        }
        //返回登录成功信息
        return (LoginResp) loginResp.setSuccess(false).setMessage("登录成功");
    }

    //User redisuser = (User) redisTemplate.opsForValue().get(username);
    //return redisuser.getUsername() + "登录成功";


    public String register(User user) {
        String password = user.getPassword();
        String username = user.getUsername();
        if ("".equals(password) || "".equals(username)) {
            return "输入不能为空";
        }
        userMapper.insertuser(user);
        //userMapper.updateuser(user);
        //redisTemplate.opsForValue().set(username,user);
        return "注册成功";
    }

    public PageInfo<User> selByPage(PageRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageHelper.startPage(pageNum, pageSize);  //传参数构建PageHelper
        List<User> studentList = userMapper.selAll();  //获取所有学生列表
        return new PageInfo<User>(studentList);  //将分页结果返回
    }

    public String sendEmail(EmailRequest emailinfo) {
        //这是核心，所有的短信方法都在这个对象里，后面只需要调用这个即可
        SimpleMailMessage message = new SimpleMailMessage();
        //设置短信发送者
        message.setFrom(from);
        //设置短信接收者
        String a = emailinfo.getEmailinfo();
        message.setTo(a);
        //短信主题
        message.setSubject("登录验证码");
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10);
            code.append(r);
            String text = "您的验证码：" + code;
            message.setText(text);
            try {
                javaMailSender.send(message);
                return "发送成功";
            } catch (MailException e) {
                e.printStackTrace();
            }

        }
        return "发送失败";
    }

    public String phonesend(String phone) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        int r = random.nextInt(10);
        String b = String.valueOf(r);
        redisTemplate.opsForValue().set(b, r, 20, TimeUnit.SECONDS);
        if (phone != null) {
            //把后台生成的code和所发送的手机号传进发送消息类，调用执行。
            SmsService.send(phone, b);
            return "0";
        } else {
            return "1";
        }
    }

    public String db2act(Stu stu) {
        sysMapper.insertstu(stu);
        return "成功使用第二数据源";
    }



    }

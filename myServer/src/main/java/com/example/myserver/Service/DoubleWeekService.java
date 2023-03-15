package com.example.myserver.Service;

import com.example.myserver.Dao.Dao1.AccountMapper;
import com.example.myserver.Dao.Dao1.UserMapper;
import com.example.myserver.Jwt.JwtUtils;
import com.example.myserver.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DoubleWeekService {
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    UserMapper userMapper;
    @Resource
    AccountMapper accountMapper;

    public String getSMS(String phone) {
        //1.定义手机号(一天可以试用多少次)、验证码标识、验证码间隔时间校验
        String phoneKey = phone + "_count";
        String codeKey = phone + "_code";
        String codeWaitKey = phone + "_codeWait";
        ValueOperations opsForValue = redisTemplate.opsForValue();
        //2.校验两次验证码的请求是否间隔超过一分钟(防重逻辑)
        if (!redisTemplate.hasKey(codeWaitKey)) {
            opsForValue.set(codeWaitKey, 0, 3, TimeUnit.SECONDS);
            //3.每个号码只能发送三次,如果这个手机号码没有缓存，则新建缓存(校验码生效时间)
            if (!redisTemplate.hasKey(phoneKey)) {
                opsForValue.set(phoneKey, 3, 30, TimeUnit.SECONDS);
            }
            //4.当号码的次数被用完了
            if (Integer.parseInt(String.valueOf(opsForValue.get(phoneKey))) == 0) {
                return "可用次数已经用完了";
            }
            //5.如果有缓存，则将缓存中次数减一
            else {
                redisTemplate.boundValueOps(phoneKey).increment(-1);
            }
            //6.获取随机6位验证码
            String code = this.getCode();
            //7.验证码存入redis并设置过期时间(2min)
            opsForValue.set(codeKey, code, 2, TimeUnit.MINUTES);
            return code;
        } else {
            return "间隔时间没有超过3s!!!!!";
        }
    }

    /**
     * 随机获取六位数字
     *
     * @return
     */
    public String getCode() {
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        return String.valueOf(code);
    }


    /**
     * 校验验证码
     *
     * @return
     */
    public String checkSMS(String phone, String code) {
        String codeKey = phone + "_code";
        //1.校验验证码是否存在
        if (!redisTemplate.hasKey(codeKey)) {
            return "暂无该用户手机号";
        }
        //2.校验验证码和手机号是否能对应的上
        if (!(redisTemplate.opsForValue().get(codeKey)).equals(code)) {
            return "验证码和手机号不匹配";
        }
        //3.根据手机号查询用户信息,看是否有对应的用户信息
        List<User> check = userMapper.selectByPhone(phone);
        for (User user : check) {
            if (user.getAccount() == null) {
                return "无该用户信息，请先注册";
            } else {
                //生成对应的token，此后转账交易必须经过校验
                String token = JwtUtils.createJWT(user.getPhone(), 24 * 3600);
                redisTemplate.opsForValue().set("jwt" + token, user, 20, TimeUnit.MINUTES);
                return "验证信息成功，登录成功" + token;
            }
        }
        return "出现其他异常情况";
    }

    /**
     * 密码校验(目前是用在登录功能上)
     *
     * @return
     */
    public String checkPwd(String phone, String pwd) {
        String pwdKey = phone + "_pwdKey";
        String pwdWaitKey = phone + "_pwdWait";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        ValueOperations opsForValue = redisTemplate.opsForValue();
        //1.输入密码的频次不能超过1秒
        if (!redisTemplate.hasKey(pwdWaitKey)) {
            opsForValue.set(pwdWaitKey, 0, 1, TimeUnit.SECONDS);
            //2.每个密码只能在一分钟内输入五次
            if (!redisTemplate.hasKey(pwdKey)) {
                opsForValue.set(pwdKey, 5, 1, TimeUnit.MINUTES);
            }
            //3.当一分钟内的密码输入次数被用完了
            if (Integer.parseInt(String.valueOf(opsForValue.get(pwdKey))) == 0) {
                return "1分钟内可用次数已用完，请稍后再试";
            }
            //4.如果有缓存，则将缓存中次数减一
            else {
                redisTemplate.boundValueOps(pwdKey).increment(-1);
            }
            //5.查询用户信息，并校验这个密码的两种情况(无用户和密码不正确)
            List<User> check = userMapper.selectByPhone(phone);
            for (User user : check) {
                if (user.getAccount() == null) {
                    return "无该用户信息，请先注册";
                }
                //6.前面传一个明文密码 后面传一个编码后的密码(用来比较上送密码和数据库密码是否相同)
                boolean matches = bCryptPasswordEncoder.matches(pwd, user.getPassword());
                if (!matches) {
                    return "用户密码错误";
                } else {
                    //7.根据用户账号生成token,并将token+用户信息存入到redis(以后所有的操作，第一步先校验这个token，如果没有这个则是水平越权)
                    String token = JwtUtils.createJWT(user.getPhone(), 24 * 3600);
                    //String token = this.getCode();
                    redisTemplate.opsForValue().set("jwt" + token, user, 20, TimeUnit.MINUTES);
                    return "验证信息成功，登录成功" + token;
                }
            }
        } else {
            return "密码输入频率过快，请稍后再试";
        }
        return "出现其他异常情况";
    }

    /**
     * 转账交易（需要先进行短信验证码与密码校验）
     *
     * @return
     */
//该事务注解的最大作用就是需要两个数据同时发生改变，但当一个数据发生改变时，突发异常，无法对另一个数据进行改变的时候，那第一个数据的改变也取消，从而保证事务的一致性。
    @Transactional(propagation = Propagation.REQUIRED)
    public String doTrans(String phoneIn, String content, String token, String phoneOut) {
        //1.先校验是否有对应的session会话
        if (redisTemplate.opsForValue().get("jwt" + token) == null) {
            return "请先登录";
        } else {
            //进行收账，出账操作
            accountMapper.in(content, phoneIn);
            // 抛出异常(用来监测事务的一致性)
           // int i = 1/0;
            accountMapper.out(content, phoneOut);
            return "转账成功";
        }
    }
}
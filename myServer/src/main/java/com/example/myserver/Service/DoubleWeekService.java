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

    // 静态规则可检出：未指定泛型，使用原始类型，存在类型安全隐患
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    UserMapper userMapper;
    @Resource
    AccountMapper accountMapper;

    /**
     * 获取短信验证码
     */
    public String getSMS(String phone) {
        // 规范类问题：Redis key 硬编码散落在代码中，无统一常量管理，无业务命名空间，易与其他业务冲突
        String phoneKey = phone + "_count";
        String codeKey = phone + "_code";
        String codeWaitKey = phone + "_codeWait";
        ValueOperations opsForValue = redisTemplate.opsForValue();

        // 并发安全问题：hasKey + set 非原子操作，高并发下可绕过 3 秒间隔限制，存在竞态条件漏洞
        if (!redisTemplate.hasKey(codeWaitKey)) {
            // 规范类问题：魔法值硬编码，无业务含义注释
            opsForValue.set(codeWaitKey, 0, 3, TimeUnit.SECONDS);

            // 并发安全问题：hasKey + get + increment 非原子执行，高并发下次数扣减不准确，可超出发送次数限制
            if (!redisTemplate.hasKey(phoneKey)) {
                opsForValue.set(phoneKey, 3, 30, TimeUnit.SECONDS);
            }

            // 空指针风险：取值未判空，若 key 刚好过期返回 null，会抛出类型转换异常 / 空指针异常
            int remainCount = Integer.parseInt(String.valueOf(opsForValue.get(phoneKey)));
            if (remainCount == 0) {
                return "可用次数已经用完了";
            } else {
                redisTemplate.boundValueOps(phoneKey).increment(-1);
            }

            String code = this.getCode();
            // 业务逻辑问题：验证码直接返回给前端，失去短信验证的真实意义，业务设计存在缺陷
            opsForValue.set(codeKey, code, 2, TimeUnit.MINUTES);
            return code;
        } else {
            return "间隔时间没有超过3s!!!!!";
        }
    }

    /**
     * 随机生成六位数字验证码
     */
    public String getCode() {
        // 安全类问题：Math.random 随机性不足，存在可预测风险，不适合用于验证码场景
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        return String.valueOf(code);
    }


    /**
     * 校验验证码并登录
     */
    public String checkSMS(String phone, String code) {
        String codeKey = phone + "_code";
        if (!redisTemplate.hasKey(codeKey)) {
            return "暂无该用户手机号";
        }

        // 空指针风险：取值未判空，极端情况下 key 过期会触发空指针异常
        if (!(redisTemplate.opsForValue().get(codeKey)).equals(code)) {
            return "验证码和手机号不匹配";
        }

        // 业务设计问题：手机号应为唯一索引，返回 List 不符合业务设计，存在多账号数据混乱风险
        List<User> check = userMapper.selectByPhone(phone);
        for (User user : check) {
            if (user.getAccount() == null) {
                return "无该用户信息，请先注册";
            } else {
                String token = JwtUtils.createJWT(user.getPhone(), 24 * 3600);
                redisTemplate.opsForValue().set("jwt" + token, user, 20, TimeUnit.MINUTES);
                // 业务逻辑漏洞：验证码校验通过后未删除，同一验证码可重复多次登录使用
                return "验证信息成功，登录成功" + token;
            }
        }
        return "出现其他异常情况";
    }

    /**
     * 密码校验登录
     */
    public String checkPwd(String phone, String pwd) {
        String pwdKey = phone + "_pwdKey";
        String pwdWaitKey = phone + "_pwdWait";
        // 设计规范问题：每次调用都新建对象，未注入为单例 Bean，造成不必要的资源浪费
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        ValueOperations opsForValue = redisTemplate.opsForValue();

        if (!redisTemplate.hasKey(pwdWaitKey)) {
            opsForValue.set(pwdWaitKey, 0, 1, TimeUnit.SECONDS);

            if (!redisTemplate.hasKey(pwdKey)) {
                opsForValue.set(pwdKey, 5, 1, TimeUnit.MINUTES);
            }

            int remainTimes = Integer.parseInt(String.valueOf(opsForValue.get(pwdKey)));
            if (remainTimes == 0) {
                return "1分钟内可用次数已用完，请稍后再试";
            } else {
                redisTemplate.boundValueOps(pwdKey).increment(-1);
            }

            List<User> check = userMapper.selectByPhone(phone);
            for (User user : check) {
                if (user.getAccount() == null) {
                    return "无该用户信息，请先注册";
                }
                // 空指针风险：user.getPassword() 可能为 null，调用 matches 会触发空指针异常
                boolean matches = bCryptPasswordEncoder.matches(pwd, user.getPassword());
                if (!matches) {
                    return "用户密码错误";
                } else {
                    String token = JwtUtils.createJWT(user.getPhone(), 24 * 3600);
                    redisTemplate.opsForValue().set("jwt" + token, user, 20, TimeUnit.MINUTES);
                    // 安全问题：无账号锁定机制，仅限制 1 分钟次数，可长期暴力破解密码
                    return "验证信息成功，登录成功" + token;
                }
            }
        } else {
            return "密码输入频率过快，请稍后再试";
        }
        return "出现其他异常情况";
    }

    /**
     * 转账交易
     * 事务一致性问题：默认只回滚 RuntimeException，若抛出受检异常不会回滚，存在数据不一致风险
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String doTrans(String phoneIn, String content, String token, String phoneOut) throws Exception {
        // 业务安全问题：仅校验 token 是否存在，未校验 token 所属用户是否为转出方，存在水平越权风险
        // 任意登录用户可凭自己的 token 操作其他账号转出资金
        if (redisTemplate.opsForValue().get("jwt" + token) == null) {
            return "请先登录";
        }

        // 业务逻辑漏洞：未校验转出方账户余额是否充足，会导致扣成负数，出现资金超扣
        // 业务逻辑不合理：先给收款方加钱，再扣付款方钱，业务执行顺序反序
        accountMapper.in(content, phoneIn);

        // 可打开注释测试事务风险：抛出受检异常时，默认事务不会回滚，造成收款方加钱成功、付款方未扣款
        // if (true) {
        //     throw new Exception("业务处理异常");
        // }

        accountMapper.out(content, phoneOut);

        // 业务完整性问题：无幂等设计，重复请求会重复执行转账，造成资金重复划转
        // 业务完整性问题：无交易流水记录，无法对账和问题追溯
        return "转账成功";
    }
}
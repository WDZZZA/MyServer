package com.example.myserver.Util;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.StringUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author 13929
 * @date 2023/3/14 13:57
 */
@Slf4j
@UtilityClass
public class SignUtils {
    /**
     * 验证签名
     * 验证算法：把timestamp + JsonUtil.object2Json(SortedMap)合成字符串，然后MD5
     */
    @SneakyThrows
    public  boolean verifySign(Map<String, String[]> map,String sign,String timestamp) {
        Map<String, String> ParameterMap = new HashMap<String, String>(); //map参数
        for(String key :map.keySet()) { //遍历数组(map中的value本身为数组格式，现在转化为字符串格式，因为数组无法被转化为字符串)
            ParameterMap.put(key, map.get(key)[0]); //将值key，key对应的的value 赋值到map参数中
        }
        //将基本参数和时间戳合并成一个新参数
        String params = JSONUtils.toJSONString(ParameterMap)+timestamp;
        //将这个参数进行加密(这个和前端加密的规则是一样的)
        String checkParams =DigestUtils.md5DigestAsHex(params.getBytes()).toUpperCase();
        //将这个加密字符串与数字签名进行校验
        return  sign.equals(checkParams);
    }
}

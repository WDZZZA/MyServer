package com.example.myserver.Dao.Dao1;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 13929
 * @date 2023/3/13 9:58
 */
@Mapper
@Repository
public interface AccountMapper {
    //扣钱
    int  out(String money,String phone);
    //存钱
    int  in (String money,String phone);
}

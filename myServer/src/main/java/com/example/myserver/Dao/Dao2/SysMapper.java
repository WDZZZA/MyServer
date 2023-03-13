package com.example.myserver.Dao.Dao2;

import com.example.myserver.entity.Stu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;



@Mapper
@Repository
public interface SysMapper {
    int insertstu(Stu stu);
}

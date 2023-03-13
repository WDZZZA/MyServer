package com.example.myserver.exception;


/**
 * 描述类：异常枚举类
 */
public enum MallExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    PASSWORD_TOO_SHORT(10003,"密码长度不能小于八位"),
    NEED_USER_PASSWORD(10002,"用户密码不能为空"),
    NAME_EXISTED(10004,"用户名已存在，注册失败"),
    INSERT_FAILED(10005,"插入失败，请重试"),
    SYSTEM_ERROR(20000,"系统异常");
    //异常码
    Integer code;
    //异常信息
    String msg;

    MallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}


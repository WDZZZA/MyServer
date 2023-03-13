package com.example.myserver.exception;


/**
 * 描述：  统一异常描述类
 */
public class MallException extends Exception{
    private final Integer code;
    private final String message;

    public MallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public MallException(MallExceptionEnum exceptionEnum){
        this(exceptionEnum.code, exceptionEnum.msg);
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


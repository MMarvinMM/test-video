package com.chinatower.airline.enums;

import com.chinatower.common.entity.IResultCode;

/**
 * 任务策略枚举类
 * @Author lize
 * @Date 2024/11/4 14:56
 */
public enum TaskStrategyEnum implements IResultCode {
    IMMEDIATELY(1,"立即"),
    SINGLE_TIMING(2,"单次定时"),
    REPETITIVE_TIMING(3,"重复定时")
    ;

    private int code;
    private String msg;

    TaskStrategyEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "TaskStrategy{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}

package com.chinatower.airline.enums;

import com.chinatower.common.entity.IResultCode;

/**
 * 执行状态枚举类
 * @Author lize
 * @Date 2024/11/4 14:27
 */
public enum ExecutionStatusEnum implements IResultCode {
    PENDING_EXECUTION(1,"待执行"),
    IN_PROGRESS(2,"执行中"),
    PARTIAL_EXECUTION(3,"部分执行"),
    EXECUTION_SUCCEEDED(4,"执行成功"),
    EXECUTION_FAILED(5,"执行失败"),
    TERMINATION_OF_EXECUTION(6,"执行终止"),
    SUSPENDED(7,"已挂起")
    ;

    private int code;
    private String msg;


    @Override
    public String toString() {
        return "ExecutionStatus{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    ExecutionStatusEnum(int code, String msg){
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

}

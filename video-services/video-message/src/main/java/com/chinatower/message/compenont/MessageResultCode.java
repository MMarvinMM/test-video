package com.chinatower.message.compenont;

import com.chinatower.common.entity.IResultCode;


/**
 * @title
 * @description 消息服务 返回编码枚举类
 * @author LiZe
 * @updateTime 2022/1/20 14:27
 * @throws
 */
public enum MessageResultCode implements IResultCode {
    MESSAGE_CHANNEL_CONNECT_FAIL(9001,"用户与消息服务通道连接已断开"),
    MESSAGE_RECORD_ERROR(9002,"记录推送消息异常"),
    REQ_PARAMS_ERROR(9002,"入参:用户Id,告警Id,告警状态,通知类型不能为空"),
    REQ_PARAM_ERROR(9002,"入参:通知类型不能为空"),
    MESSAGE_PERMISSIONS_ERROR(9002,"获取站内消息权限异常"),
    MESSAGE_NO_USER_TO_TRANSFORM_ERROR(9003,"目标用户连接，不存在于消息服务"),
    SUCCESS(200, "操作成功"),
    FAILURE(400, "业务异常");



    final int code;
    final String msg;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    private MessageResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
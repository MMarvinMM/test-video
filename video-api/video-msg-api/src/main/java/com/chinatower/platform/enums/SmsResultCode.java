package com.chinatower.platform.enums;

import com.chinatower.common.entity.IResultCode;

/**
 * 短信操作结果
 *
 * @author qq on 2023/4/13
 */
public enum SmsResultCode implements IResultCode {
    /**
     *
     */
    COMMON(16000, "业务出错"),
    SEND_TOO_FAST(16001, "发送频率过快"),
    SEND_TIMES_LIMIT(16002, "发送次数限制"),
    VERIFY_TIME_OUT(16003, "验证码失效"),
    VERIFY_ERROR(16004, "验证码错误"),
    ;

    private final int code;
    private final String msg;

    SmsResultCode(int code, String msg) {
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

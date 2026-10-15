package com.chinatower.platform.entity.request;

import lombok.Data;

@Data
public class MsgRequestVO {

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 短信模板编码
     */
    private String smsTemplateCode;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 毫秒当前请求时间戳
     */
    private String timestamp;

    /**
     * 参数map用于替换短信模板，json串
     */
    private String paraMap;

    /**
     * 签名
     */
    private String sign;


}

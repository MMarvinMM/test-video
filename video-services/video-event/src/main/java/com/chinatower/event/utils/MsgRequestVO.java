package com.chinatower.event.utils;

import lombok.Data;

@Data
public class MsgRequestVO {

    private String phoneNumber;
    private String smsTemplateCode;
    private String tenantId;
    private String timestamp;
    private String paraMap;
    private String sign;


}

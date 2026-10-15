package com.chinatower.platform.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: 短信模板
 * @Author: 张帅
 * @Date: 2022/01/24 14:40
 **/
@Component
@RefreshScope
@Data
public class SmsTemplateConf {

    @Value("${sms.template:【中国铁塔】 验证码code，您正在登录铁塔帐号（若非本人操作，请忽略本短信）}")
    private String msgTemplate;

    @Value("${sms.privateKey:1a7a9865e98d1524b26d94e16a3b3d71f0df04326dc7bba58b77d33995fb644c}")
    private String privateKey;

    @Value("${sms.sysCode:CTS00464}")
    private String sysCode;

    @Value("${sms.urlCall:http://120.52.40.57:15349/sms/asyncMass}")
    private String urlCall;


    @Value("${sms.signCode:azfz_iykl}")
    private String signCode;

    @Value("${sms.templateCode:whlp_mscl}")
    private String templateCode;

}

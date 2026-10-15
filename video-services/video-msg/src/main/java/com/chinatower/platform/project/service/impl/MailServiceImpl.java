package com.chinatower.platform.project.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.entity.request.CommonMailRequest;
import com.chinatower.platform.project.service.MailService;
import com.chinatower.platform.project.utils.MailHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 邮件组件发送服务
 */
@Service
@Slf4j
@RefreshScope
public class MailServiceImpl implements MailService {

    private static final String MAIL_SUCCESS_CODE = "200";
    private static final String ERROR_RECEIVE_MAIL = "收件箱不能为空";
    private static final String ERROR_MAIL_CONTENT = "邮件正文与模板编码不能同时为空";
    private static final String ERROR_MAIL_CONFIG = "邮件组件配置不完整";

    @Value("${mail.url:}")
    private String mailUrl;

    @Value("${mail.tenantId:}")
    private String tenantId;

    @Value("${mail.pri:}")
    private String privateKey;

    @Value("${mail.sendMail:}")
    private String sendMail;

    @Value("${mail.nickName:中国铁塔视联通用平台}")
    private String nickName;

    @Value("${mail.authCode:}")
    private String authCode;

    @Value("${mail.aesKey:chinatowerenergy}")
    private String aesKey;

    @Override
    public Result sendMail(CommonMailRequest request) {
        log.info("sendMail入参:{}", JSON.toJSONString(request));
        if (StringUtils.isBlank(request.getReceiveMail())) {
            return Result.fail(ERROR_RECEIVE_MAIL);
        }
        if (StringUtils.isBlank(request.getMailTxt()) && StringUtils.isBlank(request.getMailTemplateCode())) {
            return Result.fail(ERROR_MAIL_CONTENT);
        }
        if (StringUtils.isAnyBlank(mailUrl, tenantId, privateKey, sendMail, authCode)) {
            log.error("邮件组件配置缺失, url:{}, tenantId:{}, sendMail:{}", mailUrl, tenantId, sendMail);
            return Result.fail(ERROR_MAIL_CONFIG);
        }
        try {
            JSONObject params = buildRequestParams(request);
            String signContent = SmsServiceImpl.getSignContent(params);
            String sign = RsaUtils.sign(signContent + "&key=" + privateKey, privateKey);
            params.put("sign", sign);

            JSONObject response = MailHttpUtil.doPost(params.toJSONString(), mailUrl);
            log.info("sendMail响应:{}", JSON.toJSONString(response));
            if (response != null && MAIL_SUCCESS_CODE.equals(String.valueOf(response.get("code")))) {
                return Result.data(response);
            }
            return Result.fail(response == null ? "邮件发送失败" : String.valueOf(response.get("msg")));
        } catch (Exception e) {
            log.error("sendMail异常", e);
            return Result.fail("邮件发送异常");
        }
    }

    /**
     * 组装邮件组件 request 参数（参与签名）
     */
    private JSONObject buildRequestParams(CommonMailRequest request) {
        JSONObject params = new JSONObject();
        params.put("tenantId", tenantId);
        params.put("sendMail", sendMail);
        params.put("nickName", nickName);
        params.put("authCode", encryptAuthCode(authCode));
        params.put("receiveMail", request.getReceiveMail());
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (StringUtils.isNotBlank(request.getCcMail())) {
            params.put("ccMail", request.getCcMail());
        }
        if (StringUtils.isNotBlank(request.getMailTitle())) {
            params.put("mailTitle", request.getMailTitle());
        }
        if (StringUtils.isNotBlank(request.getMailTxt())) {
            params.put("mailTxt", request.getMailTxt());
        }
        if (StringUtils.isNotBlank(request.getMailTemplateCode())) {
            params.put("mailTemplateCode", request.getMailTemplateCode());
        }
        if (StringUtils.isNotBlank(request.getParaMap())) {
            params.put("paraMap", request.getParaMap());
        }
        return params;
    }

    /**
     * 授权码 AES 加密
     */
    private String encryptAuthCode(String plainAuthCode) {
        return SecureUtil.aes(aesKey.getBytes()).encryptHex(plainAuthCode);
    }
}

package com.chinatower.common.core.utils.notify;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.FormatUtils;
import com.chinatower.common.core.utils.HttpRequestUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author luzc
 * @date 2021/6/15 10:38
 * @desc 邮件通知管理工具类
 */
@Slf4j
public class EmailNotifyUtils {

    /**
     * 发件人姓名
     */
    private static final String ADD_NAME = "北京铁塔视联运营管理平台";
    /**
     * 发件人账号（不填则使用铁塔默认账号密码）
     */
    private static final String ADD_EMAIL = "";
    /**
     * 发件人密码（不填则使用铁塔默认账号密码）
     */
    private static final String ADD_PWD = "";
    /**
     * 请求地址
     */
    private static final String URL = "http://114.116.254.193:38765/email/sendMail";
    /**
     * 邮件发送服务器
     */
    private static final String ADD_SERVER = "smtp.mxhichina.com";
    /**
     * 是否SSL加密 0-否，1-是
     */
    private static final String IS_SSL = "0";

    /**
     * 邮件发送请求
     *
     * @param subject 发送主题
     * @param content 发送内容
     * @param email   发送对象
     * @return JSONObject 发送结果
     * 样例数据：{
     * "code": "000000",
     * "msg": "邮件发送成功"
     * }
     */
    public static JSONObject sendEmail(String subject, String content, String email) throws Exception {

        if (!FormatUtils.checkEmailPattern(email)) {
            throw new Exception("目标邮件格式不正确，请重新输入！");
        }

        // 定义请求头
        HashMap<String, String> header = new HashMap<>();
        // 定义请求参数
        HashMap<String, String> param = new HashMap<>();

        // 发送人名称
        param.put("addName", ADD_NAME);
        // 邮件发送服务器
        param.put("addServer", ADD_SERVER);
        // 发送主题
        param.put("subject", subject);
        // 发送内容
        param.put("text", content);
        // 是否SSL加密
        param.put("isSSL", IS_SSL);
        // 发送地址
        param.put("reToEmail", email);

        String response = HttpRequestUtils.sendPostWithHeader(URL, param, header);
        JSONObject parse = (JSONObject) JSONObject.parse(response);
        if (parse == null) {
            log.error(subject + "-" + email + ":邮件发送异常，请求无返回或请求超时");
        } else {
            log.info(subject + "-" + email + "：邮件发送情况" + parse.toJSONString());
        }

        return parse;

    }

}

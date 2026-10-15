package com.chinatower.platform.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义短信模板配置
 *
 * @author qq on 2023/4/6
 */
@SuppressWarnings("all")
public class CustomSmsTemplateConstants {

    /**
     * 登录短信验证码
     */
    public static final String LOGIN = "tydu_zsfv";
    /**
     * 修改手机号验证码
     */
    public static final String UPDATE_PHONE = "jyjr_lijd";
    /**
     * 忘记密码验证码
     */
    public static final String UPDATE_PASSWORD = "jwzo_ignr";
    /**
     * 重置密码后短信通知用户
     */
    public static final String RESET_PASSWORD_NOTIFY = "adnn_zbxe";

    /**
     * 登录模板key
     */
    public static final String LOGIN_KEY = "login";
    /**
     * 修改手机号模板key
     */
    public static final String UPDATE_PHONE_KEY = "updatePhone";
    /**
     * 忘记密码模板key
     */
    public static final String UPDATE_PASSWORD_KEY = "updatePwd";
    /**
     * 重置密码后短信通知用户模板key
     */
    public static final String RESET_PASSWORD_NOTIFY_KEY = "resetPwd";

    /**
     * 对外短信模板key（为了不暴露短信模板code）
     */
    public static final Map<String, String> PUBLIC_TEMPLATE_KEY_MAP = new HashMap<>();

    static {
        PUBLIC_TEMPLATE_KEY_MAP.put(LOGIN_KEY, LOGIN);
        PUBLIC_TEMPLATE_KEY_MAP.put(UPDATE_PHONE_KEY, UPDATE_PHONE);
        PUBLIC_TEMPLATE_KEY_MAP.put(UPDATE_PASSWORD_KEY, UPDATE_PASSWORD);
        PUBLIC_TEMPLATE_KEY_MAP.put(RESET_PASSWORD_NOTIFY_KEY, RESET_PASSWORD_NOTIFY);
    }
}

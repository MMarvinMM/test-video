package com.chinatower.platform.constants;

/**
 * @Title 常量类
 * @Decription 常量类
 * @Author 李峥
 * @Date 2021-08-04 19:03
 */
public class CommonConstants {
    /**
     * 前端header传入参数,单点登陆token
     */
    public static final String AUTH_TOKEN = "Authorization";

    /**
     * 认证token前缀
     */
    public static final String AUTH_TOKEN_PREFIX = "Bearer ";

    /**
     * 前端header传入参数,前后端统一算法加密
     */
    public static final String HEADER_ENCRYPT_KEY = "encryptKey";
    /**
     * 前端header传入参数,前后端统一算法加密
     */
    public static final String HEADER_DYNAMIC = "dynamic";

    /**
     * 加密算法固定key
     */
    public static final String KEY_SUFFIX = "f27dc741d9";

    /**
     * UTF8
     */
    public static final String UTF8 = "utf-8";

    /**
     * NOT_EMPTY_MSG
     */
    public static final String NOT_EMPTY_MSG = "请求参数不能为空";


    /**
     * NOT_EMPTY_CODE
     */
    public static final int NOT_EMPTY_CODE = 400;

    /**
     * TOO_MANY_REQUESTS
     */
    public static final String TOO_MANY_REQUESTS = "访问过于频繁请稍后再试";

    /**
     * 用户登陆后redis中LOGIN_TOKEN前缀
     */
    public static final String LOGIN_TOKEN = "login:";
    /**
     * 短信LOGIN_SMS前缀
     */
    public static final String LOGIN_SMS ="sms:";
    /**
     * 判断短信发送是否频繁前缀 固定秒数内不可重复发送
     */
    public static final String SMS_INTERVAL_TIME_KEY="smsInterval:";
    /**
     * 判断短信发送是否频繁前缀 按分钟 10分钟允许发3次，每天限量20次
     */
    public static final String TEMP = "minutes:limit:";
    /**
     * 判断短信发送是否频繁前缀 按天 10分钟允许发3次，每天限量20次
     */
    public static final String DAILY ="dayily:limit:";

    public static final String WEB_LOGIN_TOKEN="login_tokens:";
    public static final String APP_LOGIN_TOKEN="app_login_tokens:";

    public static final String BS_DEL_FLAG_NO="0";
    public static final String BS_DEL_FLAG_YES="1";

    /**
     * 短信验证码发送成功后的key
     */
    public static final String SMS_SUCCESS_KEY = "msg:sms:";

    /**
     * 短信验证码校验次数key
     */
    public static final String SMS_FAIL_NUM_KEY = "sms_fail_num:";

    /**
     * 短信验证码校验最大错误次数
     */
    public static final int CAPTCHA_FAIL_MAX = 5;
    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * or关键字
     */
    public static final String OR = "or";
    private CommonConstants(){}
}

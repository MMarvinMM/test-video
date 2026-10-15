package com.chinatower.platform.common.utils;


/**
 * 通用常量信息
 *
 * @author chinatower
 */
public class OauthConstants {
    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";


    public static final String SMS_SUCCESS_KEY = "msg:sms:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 登录用户(app) redis key
     */
    public static final String LOGIN_APP_TOKEN_KEY = "login_tokens_app:";



    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * Redis令牌失效记录key前缀
     */
    public static final String LOGIN_FAIL_NUM_KEY = "login_fail_num:";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 用户登录类型web
     */
    public static final String USER_LOGIN_TYPE_WEB = "web";
    /**
     * 用户登录类型app
     */
    public static final String USER_LOGIN_TYPE_APP = "app";

    /**
     * 用户类型 manage 代表登录后台管理系统的一套用户
     */
    public static final String USER_LOGIN_TYPE_MANAGE = "manage";

    public static final String LOGIN_COUNT_ERROR_MSG = "该账号已锁定，请30分钟后再尝试。";

    public static final String LOGIN_COUNT_MOREERROR_MSG = "您的账号已锁定，请稍后再试或联系管理员处理。";

    /**
     * 字符串拼接魔术字
     */
    public static final String MAGIC_CHAR = "\\|";

    /**
     * 登录类型字典值
     */
    public static final String LOGIN_TYPE = "sys.platform.logintype";
    /**
     * 图形验证码登录
     */
    public static final String LOGIN_TYPE_GRAPH = "1";

    /**
     * 图形验证码登录
     */
    public static final String LOGIN_TYPE_SMS = "0";


    /**
     * 切换视角  key的形式是toweruserId-租户Id
     */
    public static final String SWITCH_USER_KEY = "switch_user:";


    public static final String WEB_MANAGE = "web_manage";


}

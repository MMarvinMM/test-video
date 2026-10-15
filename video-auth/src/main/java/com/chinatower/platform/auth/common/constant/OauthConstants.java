package com.chinatower.platform.auth.common.constant;


import org.apache.xmlbeans.impl.regex.Match;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用常量信息
 *
 * @author chinatower
 */
@SuppressWarnings("all")
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
     * 验证码错误次数
     */
    public static final Integer CAPTCHA_FAIL_MAX = 5;


    /**
     * Redis令牌失效记录key前缀
     */
    public static final String LOGIN_FAIL_NUM_KEY = "login_fail_num:";

    /**
     * 短信登录错误次数记录
     */
    public static final String SMS_FAIL_NUM_KEY = "sms_login_fail_num:";

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

    public static final int LOGIN_COUNT_MAX_ERROR_TIMES = 5;

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
     * 短信验证码登录
     */
    public static final String LOGIN_TYPE_SMS = "2";

    /**
     * 平板端客户端ID
     */
    public static final String PAD_MANAGE_CLIENT_ID = "pad_manage";


    /**
     * 切换视角  key的形式是toweruserId-租户Id
     */
    public static final String SWITCH_USER_KEY = "switch_user:";

    public static final String PASSWORD_REGULAR = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%^&*@])[\\da-zA-Z!#$%^&*@]{8,16}$";

    public static final String PASSWORD_REGULAR_NEW =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%^&*@,./])[\\da-zA-Z!#$%^&*@,./]{8,16}$";


    /**
     * 指纹登录账号锁定时长
     */
    public static final String FINGER_PRINT_LOCK_SECONDS = "finger_print_lock_seconds";

    public static void main(String[] args) {

        Pattern pattern = Pattern.compile(PASSWORD_REGULAR_NEW);
        System.out.println(pattern.matcher("123456").find());
        System.out.println(pattern.matcher("123abc").find());
        System.out.println(pattern.matcher("Abcd@12345").find());
        System.out.println(pattern.matcher("123456Aa,./").find());

    }




}

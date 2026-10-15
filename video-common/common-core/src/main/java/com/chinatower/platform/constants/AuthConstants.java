package com.chinatower.platform.constants;

/**
 * @author qq on 2023/4/18
 */
public class AuthConstants {
    /**
     * 登录失败次数key前缀
     */
    public static final String LOGIN_FAIL_NUM_KEY = "login_fail_num:";

    /**
     * 登录失败次数key
     *
     * @param username 登录名
     * @return key
     */
    public static String getLoginFailNumKey(String username) {
        return LOGIN_FAIL_NUM_KEY + username;
    }

    /**
     * 账号或密码错误(安全限制：替代“用户不存在”)
     */
    public static final String USERNAME_ERROR = "账号或密码错误";
    /**
     * 账号锁定时长
     */
    public static final int LOGIN_COUNT_DURATION_SECONDS = 1800;
    /**
     * 账号锁定阈值
     */
    public static final int LOGIN_COUNT_MAX_ERROR_TIMES = 5;
    /**
     * 账号锁定提示语1
     */
    public static final String LOGIN_COUNT_ERROR_MSG = "该账号已锁定，请30分钟后再尝试。";
    /**
     * 账号锁定提示语2
     */
    public static final String LOGIN_COUNT_MOREERROR_MSG = "您的账号已锁定，请稍后再试或联系管理员处理。";

    /**
     * 账号锁定提示语3
     *
     * @param failedTimes 登录错误次数
     * @return 锁定提示语
     */
    public static String getLoginCountMsg(int failedTimes) {
        return USERNAME_ERROR + "，" + (LOGIN_COUNT_MAX_ERROR_TIMES - failedTimes) + "次之后账号将被锁定30分钟";
    }

    /**
     * 判断是否到达锁定限制
     *
     * @param failedTimes 登录错误次数
     * @return 是否锁定
     */
    public static boolean isLoginCountLimit(int failedTimes) {
        return failedTimes >= LOGIN_COUNT_MAX_ERROR_TIMES;
    }

}

package com.chinatower.platform.auth.common.constant;

/**
 * 用户登录请求返回消息常量
 *
 * @author bruce
 * @date 2021-11-11
 */
@SuppressWarnings("all")
public class MessageConstant {
    /**
     * 用户名或密码错误
     */
    public static final String USERNAME_PASSWORD_ERROR = "账号或密码错误";
    @Deprecated
    public static final String USERNAME_ERROR = "用户名不存在";

    /**
     * 账号被禁用
     */
    public static final String ACCOUNT_DISABLED = "账号被禁用";
    /**
     * 账号被锁定
     */
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    /**
     * 账号过期
     */
    public static final String ACCOUNT_EXPIRED = "账号过期";
    /**
     * 证书失效
     */
    public static final String CREDENTIALS_EXPIRED = "证书失效";

    /**
     * 认证服务器参数错误
     */
    public static final String SERVER_PARAM_ERROR = "认证服务器参数错误";

    /**
     * 该账号不存在
     */
    @Deprecated
    public static final String USER_NOT_FOUND = "该账号不存在";

    /**
     * 该账号已禁用
     */
    public static final String USER_FORBIDDEN = "该账号已禁用";

    /**
     * 账号或密码错误
     */
    public static final String USERNAME_NOT_RIGHT = "账号或密码错误";
    /**
     * 手机号码与账号不匹配
     */
    public static final String USERNAME_PHONE_NOT_MATCH = "手机号码与账号不匹配";

    /**
     * 新密码不可与旧密码一致
     */
    public static final String PASSWORD_REPEAT = "新密码不可与旧密码一致";

    /**
     * 手机验证码错误
     */
    public static final String VERIFYCODE_ERROR = "手机验证码错误！";

    /**
     * 手机验证码失效
     */
    public static final String VERIFYCODE_INVALID = "手机验证码失效！";
    /**
     * 重复密码必须与新密码相同
     */
    public static final String PASSWORD_NOT_CONFIRMED = "重复密码必须与新密码相同";
    /**
     * 密码为8~16位非空字符,包含大小写字母、数字与特殊字符
     */
    public static final String PASSWORD_NOT_REGULAR = "密码为8~16位非空字符,包含大小写字母、数字与特殊字符。";

    public static final String PASSWORD_NOT_STANDARD = "密码不规范";

    /**
     * 发送短信失败
     */
    public static final String SEND_MSG_FAIL = "发送短信失败";
}

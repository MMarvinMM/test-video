package com.chinatower.common.core.constant;

/**
 * 通用常量信息
 *
 * @author ruoyi
 */
@SuppressWarnings("all")
public class Constants
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 密码重置
     */
    public static final String RESET_PWD = "Abcd@1234";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PWD = "1qaz!QAZ";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;

    public static final String DEL_FLAG = "delFlag";
    /**
     * 令牌有效期（分钟）
     */
    public final static long TOKEN_EXPIRE = 30;

    /**
     * 令牌有效期（分钟）
     */
    public final static long LOGIN_EXPIRE = 720;

    /**
     * 令牌有效期（分钟）
     */
    public final static long USER_EXPIRE = 30;

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 设备编码 cache key
     */
    public static final String DEVICE_CODE_KEY = "device_code:";

    /**
     * 智联03
     */
    public static final String ZHILIAN = "03";


    public static final String FILE_NAME = "filename";
    public static final String PARAM_FILE_NAME = "fileName";
    public static final String STORAGE_PATH = "storagePath";

    public static final String API = "1";
    public static final String SDK = "2";
    public static final String API_REQ_HEAD = "REQHEAD";
    public static final String API_URL_INPUT_PARAM = "URLINPUTPARAM";
    public static final String API_REQ_PARAM = "REQPARAM";
    public static final String API_REQ_CONTENT = "REQCONTENT";
    public static final String API_RES_CONTENT = "RESCONTENT";
    public static final String REFRESH_COUNT_PC = "refresh_count_pc";
    public static final String USER_KEY = "userKey";
    public static final String USER_REFRESH_COUNT = "userRefreshCount";
    public static final String TASK_MILES = "taskMiles";
    public static final String LOGIN_TIME_WEB_CONFIG = "login_time_web_config";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";
    public static final String ELEVEN = "11";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM = "yyyyMMddHHmm";

    public static final String POINT_FLY = "指点飞行";
    /**
     * 摄像机
     */
    public static final int DEVICE_TYPE_CAMERA = 1;
    /**
     * 喇叭
     */
    public static final int DEVICE_TYPE_HORN = 2;
}

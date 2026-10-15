package com.chinatower.common.core.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants
{
    /**
     * 令牌自定义标识
     */
    public static final String HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 权限LOG缓存前缀
     */
    public final static String LOGIN_LOG_KEY = "login_log_password:";

    /**
     * 权限LOG缓存前缀
     */
    public final static String LOGIN_USER_KEY = "login_user_password:";

    /**
     * 验证码缓存前缀
     */
    public final static String RESET_PASSWORD_VERIFICATION_CODE = "rest_password_verification_code:";

    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 授权信息字段
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * 行业编码信息
     */
    public static final String USER_INDUSTRY_CODE = "USER_INDUSTRY_CODE_";

    /**
     * 行业编码信息
     */
    public static final String TOWER_USER = "TOWER_USER_";

    /**
     * 获取用户缓存key
     * @param clientId
     * @param userId
     * @return
     */
    public static String getUserCacheKey(String clientId, String userId){
        return USER_INDUSTRY_CODE + clientId + ":" + userId;
    }

    /**
     * 用户登录token集合
     */
    public static final String USER_TOKENS = "USER_TOKENS_";

    /**
     * 获取用户token缓存key
     * @param clientId
     * @param userId
     * @return
     */
    public static String getUserTokensKey(String clientId, String userId) {
        return USER_TOKENS + clientId + ":" + userId;
    }

    /**
     * 黑名单前缀
     */
    public static final String USER_BLACK_LIST = "USER_BLACK_LIST_";
    /**
     * token黑名单前缀
     */
    public static final String TOKEN_BLACK_LIST = "TOKEN_BLACK_LIST_";

    /**
     * 获取用户token黑名单缓存key
     * @param clientId
     * @param tokenMd5 token加密值
     * @return
     */
    public static String getTokenBlackListKey(String clientId, String tokenMd5) {
        return TOKEN_BLACK_LIST + clientId + ":" + tokenMd5;
    }

    /**
     * 最终行业编码信息
     */
    public static final String USER_LAST_INDUSTRY_CODE ="USER_LAST_INDUSTRY_CODE";

    /**
     * 累计里程key
     */
    public static final  String UAV_ACCUMULATE_MILEAGE = "uav:accumulate:mileage:";
    /**
     * 累计返程里程key
     */
    public static final  String UAV_ACCUMULATE_MILEAGE_RETURN = "uav:accumulate:mileage:return:";

    //抽帧任务key
    public static final String EXTRACTION_TASK_ID_ = "extractionTaskId_";
}

package com.chinatower.platform.auth.common.constant;


/**
 * REDIS常量信息
 *
 * @author chinatower
 */
public class RedisConstants {

    public static final String NAME_SPACE_SPLIT = ":";

    public static final String SERVICE_PREFIX = "APP_DEVICE" + NAME_SPACE_SPLIT;

    public static final Long DICT_TIME_OUT = 1L;

    public static final String DICT_BY_CODE_PREFIX = SERVICE_PREFIX + "DICT_BY_CODE" + NAME_SPACE_SPLIT;

    public static final String DICT_BY_CODE_ITEMCODE_PREFIX = SERVICE_PREFIX + "DICT_BY_CODE_ITEMCODE" + NAME_SPACE_SPLIT;

    public static final String DICT_BY_CODE_ITEMVALUE_PREFIX = SERVICE_PREFIX + "DICT_BY_CODE_ITEMVALUE" + NAME_SPACE_SPLIT;

    public static final String DICT_BY_CODE_ITEMNAME_PREFIX = SERVICE_PREFIX + "DICT_BY_CODE_ITEMNAME" + NAME_SPACE_SPLIT;

    public static final String TENANT_TREE_SELECT_AREA_BY_DEPT = "TENANT:AREATREE:DEPT";

    public static final String SMS_SUCCESS_KEY = "msg:sms:";

    public static final String OAUTH_FAIL_MSG = "认证服务器异常！";

}

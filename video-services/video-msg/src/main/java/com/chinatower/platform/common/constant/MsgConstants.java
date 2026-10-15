package com.chinatower.platform.common.constant;

/**
 * @Description:静态属性类
 * @Author: 张帅
 * @Date: 2021/11/12 16:05
 **/
public class MsgConstants {
    /**
     * 短信记录标志 0代表发送失败 1代表发送成功
     */
    public static final String SMS_SUCCESS = "1";
    public static final String SMS_ERROR = "0";


    public static final String SMS_KEY = "msg:";
    /**
     * 短信发送间隔时间
     */
    public static final String SMS_INTERVAL_TIME_KEY = "msg:smsInterval:";


    /**
     * 十分钟内发送短信次数
     */
    public static final String SMS_LIMIT_COUNT_KEY = "msg:limitCount:";
    /**
     * 一天内发送短信次数
     */
    public static final String SMS_LIMIT_DAY_COUNT_KEY = "msg:limitDayCount:";

    /**
     * 发送成功后的key
     */
    public static final String SMS_SUCCESS_KEY = "msg:sms:";

    public static final String ZHONG_SHANG = "ZhongShang";
    public static final String AI_PAI = "AiPai";
    /**
     * 短信发送方式的configkey
     */
    public static final String SYS_SMS_TYPE = "sys.sms.type";
    /**
     * 已读
     */
    public static final String IF_READ = "1";

    public static final String JPUSH_RECORD_COLLECTION_NAME = "collectionName";
    public static final String JPUSH_RECORD_COLLECTION_NAME_VALUE = "bs_jpush_record";
    public static final String JPUSH_RECORD_ENTITY_CLASS = "entityClass";

    public static final String JPUSH_RECORD_FIELD_DEL_FLAG = "delFlag";
    public static final String JPUSH_RECORD_FIELD_DEL_FLAG_0 = "0";
    public static final String JPUSH_RECORD_FIELD_SEND_RESULT = "sendResult";
    public static final String JPUSH_RECORD_FIELD_SEND_RESULT_1 = "1";
    public static final String JPUSH_RECORD_FIELD_SEND_RESULT_0 = "0";
    public static final String JPUSH_RECORD_FIELD_SEND_IS_READ = "isRead";
    public static final String JPUSH_RECORD_FIELD_SEND_IS_READ_0 = "0";
    public static final String JPUSH_RECORD_FIELD_ALIAS = "alias";

    public static final String JPUSH_MSG_TYPE_NOTIFICATION = "1";
    public static final String JPUSH_MSG_TYPE_MESSAGE = "0";
    
    public static final String STRING_200 = "200";
    public static final String STRING_BLANK = "";
    public static final String SMS_TIMEOUT = "smsTimeout";
    public static final String SMS_TIMEOUT_INFO = "短信验证码超时时间设置（秒）：";
    
    public static final String SMS_TIME_INTERVAL = "smsTimeInterval";
    public static final String SMS_TIME_INTERVAL_INFO = "短信验证码间隔时间（秒）：";

    public static final String SMS_TEN_MINUTES_COUNT = "smsTenMinutesCount";
    public static final String SMS_TEN_MINUTES_COUNT_INFO = "短信验证码10分钟发送次数上限：";
    
    public static final String SMS_DAILY_COUNT = "smsDailyCount";
    public static final String SMS_DAILY_COUNT_INFO = "短信验证码一天发送次数上限：";
}

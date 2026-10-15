package com.chinatower.platform.constants;

import io.jsonwebtoken.Claims;

/**
 * 通用常量信息
 *
 * @author chinatower
 */
public class Constants {

    public static final String TOWER_ROOT_ORG_NAME = "中国铁塔";

    public static final String CENTER_LOCATION = "10000000001";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 渠道级别 一级渠道
     */
    public static final String CHANNEL_LEVEL_FIRST = "1";

    /**
     * 渠道级别 二级渠道
     */
    public static final String CHANNEL_LEVEL_SECOND = "2";

    /**
     * 0系统用户
     */
    public static final String SYS_USER_TYPE_0 = "0";
    /**
     * 1集团用户
     */
    public static final String SYS_USER_TYPE_1 = "1";

    public static final String BATTERY_ORG_TYPE_0 = "0";

    public static final String BATTERY_ORG_TYPE_1 = "1";

    public static final String BATTERY_ORG_TYPE_2 = "2";

    public static final String BATTERY_ORG_TYPE_4 = "4";

    public static final String ORG_TYPE_4 = "4";

    public static final String SYS_DEPT_1 = "1";
    public static final String SYS_DEPT_2 = "2";
    public static final String SYS_DEPT_3 = "3";

    public static final String SQL = "sql";

    public static final String TYPE_PERSON = "0";

    public static final String TYPE_CHANNEL = "1";

    public static final String TYPE_ENTERPRISE = "2";

    public static final String TYPE_POSTAL = "3";

    public static final String TYPE_BLOC = "4";

    public static final String PAGE_NUM = "pageNum";

    public static final String PAGE_SIZE = "pageSize";

    public static final String DEPT_ROOT_ID = "100000";

    public static final String INDEXFIELD_ALARM_TIME = "alarm_time";

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

    public static final String TWELVE = "12";

    public static final String NINETY_NINE = "99";

    public static final String AREA_FORMAT = "areaFormat";

    public static final String DEPT_FORMAT = "deptFormat";


    public static final String COUNTRY_DEPT = "COUNTRY";

    public static final String PROVINCE_DEPT = "PROVINCE";

    public static final String CITY_DEPT = "CITY";


    public static final String RID = "regionId";

    public static final String CID = "cityId";

    public static final String PID = "provinceId";

    public static final String DEL_FLAG = "delFlag";

    public static final String M_CTLY = "110000,120000,310000,500000";

    /**
     * 资源池消费 ASSETS_PREEMPT:占用,ASSETS_EXPENSE:消费,ASSETS_REFUND:退还
     */
    public static final String ASSETS_PREEMPT = "PREEMPT";
    public static final String ASSETS_EXPENSE = "EXPENSE";
    public static final String ASSETS_REFUND = "REFUND";

    public static final String FAIL_MSG_REQUEST = "非法请求!";

    public static final String FAIL_MSG_SMS = "发送短信失败，请联系管理员!";

    /**
     * 成功
     */
    public static final String S = "S";
    /**
     * 失败
     */
    public static final String F = "F";
    /**
     * 是
     */
    public static final String Y = "Y";
    /**
     * 否
     */
    public static final String N = "N";

    /**
     * 否
     */
    public static final String USER_REFUND_MSG_TOPIC = "userRefundMsg";
    public static final String BLANK_STR = "";


    /**
     * 新增类型 1-pws未录入 2-pws已录入
     */
    public static final String stationAddType1 = "1";
    public static final String stationAddType2 = "2";

    /**
     * 菜单类型
     */
    public static final String DIRECTORY = "M";
    public static final String MENU = "C";
    public static final String BUTTON = "F";

    public static final String USER_NAME = "ADMIN_REPORT";
    /**
     * 字典类型
     */
    public static final String BS_PROBLEM_TYPE = "bs_problem_type";


    public static final String LOGIN_FAIL_NUM_KEY = "login_fail_num:";

    public static final String MESSAGE_SYSTEM = "500";
    public static final String MESSAGE_NOTICE = "501";
    public static final String MESSAGE_ACTIVITY = "502";

    public static final String CHANNEL_ADMIN = "CHANNEL_ADMIN";//一级渠道管理员
    public static final String ENTERPRISE_ADMIN = "ENTERPRISE_ADMIN";//企业管理员
    public static final String SECOND_CHANNEL_ADMIN = "SECOND_CHANNEL_ADMIN";//二级渠道管理员
    public static final String IDCARD_ADMIN = "IDCARD_ADMIN";//身份证管理员 R-ZBZB-202107010746817 总部管理员放开身份证号显示

    /**
     * 骑手用户缓存前缀
     */
    public static final String REDIS_USER_INFO = "redis-cache::BsAppUserInfo:";
    /**
     * 骑手用户账户缓存前缀
     */
    public static final String REDIS_USER_ACCOUNT_INFO = "redis-cache::BsAppUserAccountInfo:";


    /**
     * 王子超 2021.09.07
     * 骑手导入类型标识
     */
    public static final String DRIVER_IMPORT = "driver.import.tmpl";

    /**
     * 卿燚 2021.09.29
     * 区分导出方式的数据量界限 初步定为一万条数据
     */
    public static final Integer EXPORT_COUNT = 10000;

    /**
     * 王子超 2021.09.09
     * mongodb把更新为空的特殊识别标识
     */
    public static final String EMPTY = "empty";

    public static final String LOGIN_USER_INFO = "loginuserInfo:";


    /**
     * wgs84: Google Earth采用，Google Map国外使用, 手机的GPS系统
     */
    public static final String WGS84_MAP_TYPE = "wgs84";

    /**
     * gcj02: 火星坐标系，中国国家测绘局制定的坐标系统，由wgs84机密后的坐标。Google国内地图、高德、QQ地图 使用
     */
    public static final String GCJ02_MAP_TYPE = "gcj02";

    /**
     * bd09ll:百度坐标，gcj02基础上二次加密而成
     */
    public static final String BD09_MAP_TYPE = "bd09ll";

    /**
     * "status":3003,"message":"指定track不存在"
     */
    public static final String NO_ENTITY = "3003";
    public static final String PARAM_ORG_NAME = "orgName";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_ID = "id";
    public static final String PARAM_ORG_ID = "orgId";
    public static final String DEVICE_CODE_IS_EMPTY = "设备编码为空";
    public static final String TENANT_ID_IS_EMPTY = "租户编码为空";
    public static final String LABEL_ID_IS_NOT_LEGAL = "非法的标签ID";

    public static final String PARAM_DEVICE_CODE = "deviceCode";
    public static final String CHANNEL_ONLINE = "0";
    public static final String SITE_CODE_IS_EMPTY = "siteCode不能为空";
    public static final String PARAM_LIST = "list";
    public static final String PARAM_CITY_ID = "cityId";
    public static final int HORI_PAZIMUTH_ANGLE_360 = 360;
    public static final String SYS_DEVICE_HORIZONTAL  = "sys_device_horizontal";

    public static final String WEATHER_FUTURE_DAY_INFO = "weather_future_day_info_";

    /**
     * 支持标定的厂家 cache key
     */
    public static final String CALIBRATION_MODEL_KEY = "calibration_model";

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    public static final int ZOOM_ZERO = 0;

    public static final String ZOOM_STR_ZERO = "0.00";

    public static final String ZOOM_STR_DIGITS = "4";

    public static final int ZOOM_DIGITS = 4;

    public static final String PERCENT_50 = "0.5";

    public static final String DEVICECODE = "deviceCode";
    public static final String TIMEFREQUENCY = "timeFrequency";
    public static final String TIMEFLAG = "timeFlag";

    public static final String RELATION_ID = "relationId";

    public static final String DEV_INFO_LIST = "devInfoList";
    public static final String LIST = "list";

    public static final String DEVICE_DELETE_FLAG_1 = "1";

    public static final String DEVICE_DELETE_FLAG_0 = "0";


    public static final int ZERO_NUM = 0;
    public static final int ONE_NUM = 1;
    public static final int TWO_NUM = 2;
    public static final int THREE_NUM = 3;

    public static final int FIVE_NUM = 5;

    public static final String UNDER_LINE = "_";

    public static final String PRESET_NAME = "预置位";

    public static final String DEVICE_CODE_NOT_EMPTY = "设备编码不能为空";
    public static final String COST_TIME = "耗时: {}";
    public static final String RECEIVE_PARAM_AND_COLLECTION_NAME = "receive param: {}, collectionName: {}";
    public static final String MONITOR_TIME = "monitorTime";
    public static final String SPECIES_TYPE = "species_type";
    public static final String SENSOR_EVENT_TYPE = "sensor_event_type";
    public static final String EVENT_ID = "eventId";
    public static final String IMG_URL = "imgUrl";

    public static final String LOCATION_PARAM = "?location=";
    public static final String KEY_PARAM = "&key=";

    public static final String BEGIN_DATE_PARAM = "&beginDate=";

    public static final String END_DATE_PARAM = "&endDate=";
    public static final String DAILY = "daily";
    public static final String WARNING = "warning";
    public static final String PUB_TIME_DATE = "pubTimeDate";
    public static final String OBS_TIME_DATE = "obsTimeDate";
    public static final String BETWEEN = "between";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String ORDER_ERROR_MSG = "订购关系变更未找到处理策略类";
    public static final String TOPIC_NAME = "topicName:";
    public static final String LOGIN_USER_ERROR = "登录用户异常";
    public static final String WEATHER_NOW = "weather/now";

    public static final String WEATHER_PRECIP = "weather/precip";
    public static final String AREA_INFO_CACHE = "areaInfoCache:";
    public static final String AIR_NOW = "air/now";
    public static final String WARNING_NOW = "warning/now";

    public static final String WARNING_HISTORY = "warning/history";
    public static final String WARNING_INFO_CACHE = "warningInfoCache:";

    public static final String WARNING_SEVERITY = "warning_severity";
    public static final String WARNING_SEVERITY_COLOR = "warning_severity_color";
    public static final String WARNING_TYPE = "warning_type_";

    public static final String UAV_CAMERA_MODEL = "uav_camera_model";

    public static final String NO_USER_INFO = "用户未登录！";
    public static final String AUTH_FAIL = "登录失败!";

    public static final String AUTH_SUCCESS = "登录成功!";

    public static final int TEN_NUM = 10;
    public static final String TEN_NUM_STRING = "10";

    //实时播放接口返回编码
    public static final String SUCCESS_CODE = "0000";
    public static final String ERROR_CODE = "8888";

    //无人机遥测轨迹mongo集合名
    public static final String TABLE_NAME_UAV_DEVICE_TRACK = "uav_device_track_";

    public static final String TABLE_NAME_RECORDER_DEVICE_TRACK = "recorder_device_track_";

    //无人机属性索引列
    public static final String INDEXFIELD_UAV_DEVICECODE = "deviceCode";
    public static final String INDEXFIELD_UAV_TRACKTIME = "trackTime";

    public static final String INDEXFIELD_TIME = "time";
    public static final String UAV_INDEX_NAME = "index_deviceCode_trackTime";

    public static final String RECORDER_INDEX_NAME = "index_recorder_deviceCode_time";

    public static final String INDEXFIELD_RECORDER_TIME = "time";

    public static final String DEVICE_TYPE_UAV_DEVICE = "11";

    public static final int FOUR_NUM = 4;
    public static final int NINE_NUM = 9;
    public static final String STREAM_URL = "streamUrl";
    public static final String STREAM_UUID = "streamUuid";
    public static final String PROTOCOL_TYPE = "protocolType";
    public static final String NET_TYPE = "netType";
    public static final String APP_ID = "web_manage:123456";
    public static final String DO_SWITCH_LOGIN_ERROR = "模拟登陆异常！";

    public static final String COLONY_RELATION_ALL = "COLONY_RELATION_ALL:";
    public static final String COLONY_RELATION_ALIAS_ALL = "COLONY_RELATION_ALIAS_ALL:";
    public static final String COMMA = ",";
    public static final String COMMA_CH = "，";

    public static final String X_CUSTOMER_CODE = "X-CustomerCode";

    public static final String PLATFORM_CODE = "platform_code";
    public static final String PARENT_CODE = "parentCode";
    public static final String CODE = "code";
    public static final String DATA = "data";
    public static final String MSG = "msg";

    public static final String CORE_TOWER_INDUSTRY_CODE = "999999";

    public static final String BLACK = "black";

    public static final String WHITE = "white";

    public static final String ITEM_DEVICE_CODE = "#item.deviceCode";

    public static final String COMPONENT_VERSION_IMAGE ="componentVersionImage";
    public static final String IMAGE ="image";

    public static final String COMPONENT_URL ="component_url";
    /**
     * 报错通用提示信息
     */
    public static final String DEFAULT_ERROR_MESSAGE = "系统繁忙，请稍后再试。";

    public static final String REGEX_START = "^.*";
    public static final String REGEX_END = ".*$";

    /**
     * 卫星火点告警类型
     */
    public static final String SATELLITE_FIRE_POINT_TYPE_VALUE = "4110100";

    //卫片执法字段
    public static final String NUMBER ="number";
    public static final String COUNTRY ="country";
    public static final String COUNTY ="county";
    public static final String VILLAGE ="village";
    public static final String RETURN_MSG ="returnMsg";

    public static final String TIAO_SUANFA = "条算法";
    public static final String RGBA = "rgba(255,255,255,1)";

    public static final String UPDATE_SUCCESS = "更新成功";

    public static final int TWO_HUNDRED = 200;

    public static final int TEN_THOUSAND = 10000;

    public static final String FEN_HAO = ":";

    public static final String HEART_BEAT_KEY = "heartBeat";

    public static final String CODE_401 = "401";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String ALARM_PUSH_TASK_KEY = "alarm_push_task_key";
}

package com.chinatower.platform.constants;

/**
 * Created by zheng.yan on 2022/1/18.
 */
public class DeviceConstants {
    /**
     * 正常状态
     */
    public static final String DELETE_FLAG_NORMAL = "0";

    public static final int DELETE_FLAG_NORMAL_INT = 0;

    public static final int DELETE_FLAG_EXCEPTION_INT = 1;

    /**
     * 删除状态
     */
    public static final String DELETE_FLAG_EXCEPTION = "1";

    /**
     * 录像计划状态 1-启用
     */
    public static final int RECORD_PLAN_STATUS_ENABLE = 1;

    /**
     * 录像计划状态 0-禁用
     */
    public static final int RECORD_PLAN_STATUS_STOP = 0;

    /**
     * 算法任务状态：0:启用
     */
    public static final String ALGORITHM_STATUS_ENABLE = "0";

    /**
     * 算法任务状态：1:停用
     */
    public static final String ALGORITHM_STATUS_STOP = "1";

    /**
     * 0在线
     */
    public static final int ON_LINE_FLAG = 0;

    /**
     * 1离线
     */
    public static final int OFF_LINE_FLAG = 1;

    /**
     * 2未配置设备
     */
    public static final int NO_SETFLAG = 2;

    /**
     * 巡航计划状态 1停用 2启用
     */
    public static final int PATROL_PLAN_STATUS_STOP = 1;
    public static final int PATROL_PLAN_STATUS_START = 2;

    public static final String COMMA = ",";
    public static final int ZERO_NUM = 0;
    public static final int ONE_NUM = 1;
    public static final int TWO_NUM = 2;
    public static final int THREE_NUM = 3;
    public static final int FOUR_NUM = 4;
    public static final int SIX_NUM = 6;
    public static final int SEVEN_NUM = 7;
    public static final int TEN_NUM = 10;
    public static final int ELEVEN_NUM = 11;
    public static final int THIRTEEN_NUM = 13;
    public static final int FIFTEEN_NUM = 15;
    public static final int SIXTEEN_NUM = 16;
    public static final int ONE_HUNDRED_AND_EIGHTY = 180;
    public static final int PAGE_NUM = 1;
    public static final int PAGE_SIZE = 10;
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String NO_DATA = "9999";
    public static final String CHECK_NODE = "check";
    public static final String VALID_STATE = "0";
    public static final String YES_FLAG = "1";
    public static final String TWO_FLAG = "2";
    public static final String THREE_FLAG = "3";
    public static final String FOUR_FLAG = "4";
    public static final String FIVE_FLAG = "5";
    public static final String SIX_FLAG = "6";
    public static final String NINE_FLAG = "9";
    public static final int ENUM_FLOW = 13;
    public static final String SUB_ORDER = "联合办理";
    public static final int RADIUS = 74;
    //api配置开启
    public static final String API_OPEN = "1";
    //api配置关闭
    public static final String API_CLOSE = "0";
    //客户状态失效
    public static final String CUSTOMER_STATUS_0 = "0";
    //客户状态有效

//    String CUSTOMER_STATUS_1 = "1";


    public static final String PAGE_NUM_STR = "pageNum";
    public static final String PAGE_SIZE_STR = "pageSize";
    public static final String USER_ID_STR = "userId";
    public static final String TENANT_NAME_STR = "tenantName";
    public static final String TENANT_ID_STR = "tenantId";
    public static final String PRESET_NAME_STR = "preset_name";
    public static final String PRESET_ID_STR = "preset_id";
    public static final String TASK_ID_STR = "task_id";
    public static final String PTZ_PAN_STR = "pan";
    public static final String PTZ_TILT_STR = "tilt";
    public static final String PTZ_ZOOM_STR = "zoom";
    public static final String CAN_NOT_GET_TIP = "无法获取当前登录用户信息，请重新登陆";

    public static final String CUSTOMER_STATUS_1 = "1";

    public static final String NOT_EMPTY_DEV_CODE = "设备编码不能为空";
    public static final String NOT_EMPTY_CHANNEL_CODE = "通道编码不能为空";
    public static final String DEV_NOT_ONLINE = "摄像机不在线";
    public static final String NOT_EMPTY_CUSTOMER_CODE = "客户编码不能为空";
    public static final String NOT_EMPTY_PLUGIN_ID = "协议类型为铁塔私协时，插件ID不能为空";
    public static final String NO_USER_INFO = "无法获取当前登录用户信息，请重新登陆";
    public static final String EXO_SYSTEM_EXCEPTION_INFO = "系统繁忙，请稍后再试！";
    public static final String TOWER_NO_SWITCH = "铁塔视角无法进行区划与组织切换";
    public static final String SUCCESS_STATUS_CODE = "0000";

    public static final String USER_ID = "userId";
    public static final String DATA = "data";
    public static final String ONLINE_STATUS_ZERO = "0";
    public static final String ONLINE_STATUS_ONE = "1";
    public static final String ORG_TYPE_TWO = "2";
    public static final String ORG_TYPE_ONE = "1";
    public static final String STREAM_TYPE = "streamType";
    public static final String STREAM_SWITCH_FLAG = "streamSwitchFlag";
    public static final String RETURN_CODE = "returnCode";
    public static final String STREAM_SWITCH_FLAG_CODE_ONE = "1";
    public static final String STREAM_SWITCH_FLAG_CODE_TWO = "2";
    public static final String SOURCE_CODE = "source";
    public static final String PARENT_ORG_ID = "parentOrgId";
    public static final String SOURCE_CODE_STATUS = "0";  //来源：0铁塔 1租户
    public static final String ALGOMINTYPECODE_STR = "algoMinTypeCode";
    public static final String ORDER_STR = "order";
    public static final String NOORDER_STR = "noorder";
    public static final String ID_STR = "id";
    public static final String ALGOMINTYPENAME_STR = "algoMinTypeName";
    public static final String ALGOTYPECODE_STR = "algoTypeCode";
    public static final String ALGOTYPENAME_STR = "algoTypeName";
    public static final String ALGOSUBTYPECODE_STR = "algoSubTypeCode";
    public static final String ALGOSUBTYPENAME_STR = "algoSubTypeName";

    public static final String EMUM_TYPE_ID_80 = "80";
    public static final String EMUM_TYPE_ID_74 = "74";
    public static final String TYPE_ID = "typeId";
    public static final String TYPE_NAME = "typeName";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";

    //云台数量
    public static String VIDEO_OPERATOR_NUM = "VIDEO_OPERATOR_NUM";
    //播放数量
    public static String VIDEO_PLAY_NUM = "VIDEO_PLAY_NUM";

    public static String AUTH_TOKEN = "authToken";
    public static int REDIS_EXPIRE = 86460;

    //枚举Redis前缀
    public static final String ENUM_INFO = "enum_info_";
    // 北向消息枚举值
    public static final int NORTH_INTERFACE_MESSAGE = 92;
    // 北向错误码
    public static final String ERROR_CODE = "errorCode";

    public static final String ERROR_CODE_MESSAGE = "错误码：";
    public static final String ERROR_TIME_MESSAGE = "请求时间：";
    public static String TOWER_ROOT_ORG = "100000";
    public static String TOWER_ROOT_ORG_NAME = "中国铁塔";
    public static String TOWER_ROOT_AREA = "100000";
    public static String TOWER_ROOT_AREA_NAME = "中国";

    public static final String ENUM_TYPE_ID = "enumTypeId";

    public static final String SUCCESS = "SUCCESS";

    public static final String LIST = "list";

    public static final String ENUM_CODE = "enumCode";
    public static final String ENUM_NAME = "enumName";

    public static final long ONE_HOUR = 3600L;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String RESULTSTAT = "resultStat";
    public static final String ERROR = "ERROR";
    public static final String ORDERCODE = "orderCode";
    public static final String STATUS = "status";
    public static final String _DEVLIST = "_devList";
    public static final String _CHNLIST = "_chnList";
    public static final String _INVALIDTIME = "_invalidTime";
    public static final String DATE_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String CUSTOMERCODE = "customerCode";
    public static final String ISOPEN = "isOpen";
    public static final String CUSTOMERSTATUS = "customerStatus";
    public static final String CUSTOMER = "customer";
    public static final String ORIGINALJSON = "originalJson";
    public static final String ERRORMSG = "errorMsg";
    public static final String VIDEOPLANLIST = "videoPlanList";
    public static final String RECORDPLANIDS = "recordPlanIds";


    public static final String MESS_1 = "操作失败";
    public static final String MESS_2 = "【订购关系编码】不能为空";
    public static final String MESS_3 = "订购关系增量推送失败";
    public static final String MESS_4 = "入参报文格式错误";
    public static final String MESS_5 = "停用预置位异常！";
    public static final String MESS_6 = "停用巡航路径异常！";
    public static final String MESS_7 = "停用录像计划异常！";
    public static final String MESS_8 = "启用预置位异常！";
    public static final String MESS_9 = "启用巡航路径异常！";
    public static final String MESS_10 = "启用录像计划异常！";

    public static final String MESS_1001 = "1001";
    public static final String MESS_1004 = "1004";
    public static final String MESS_10041 = "10041";

    public static final String REQUEST_FROM_API = "api";

    public static final String VIDEOS = "videos";


    public static final String EQUAL_SEPARATOR = "==========";
    public static final String REQUEST_MESSAGE = "请求报文:---";
    public static final String RESPONSE_MESSAGE = "返回报文：---";
    public static final String PATROL_ID = "patrolId";
    public static final String PATROLIDS = "patrolIds";
    public static final String IS_CRUISE = "isCruise";
    public static final String PATROL_NAME = "patrolName";
    public static final String CREATOR = "creator";
    public static final String CREATOR_DATE = "creatorDate";
    public static final String PATROLIST = "patrolList";
    public static final String PATROL_DEFAULT_NAME = "patrolDefaultName";
    public static final String PATROL_PLAN_DEFAULT_NAME = "巡航计划";
    public static final String BUSINESS_PROCESS_EXCEPTION = "业务处理异常";
    public static final String PATROL_ID_NOT_NULL = "巡航计划ID不能为空";
    public static final String PATROL_INFO_ADD_ERROR = "巡航计划落表失败";
    public static final String PATROL_INFO_DELETE_ERROR = "巡航计划删除失败";
    public static final String PATROL_BATCH_START_END_ERROR = "巡航计划批量启用/停用失败";
    public static final String PATROL_BATCH_START_END_REQ = "巡航计划批量启用/停用入参------";
    public static final String PATROL_START_NOT_UPDATE = "该巡航计划正在启动中，无法修改，请巡航结束后再试。";
    public static final String PATROL_START_NOT_DELETE = "该巡航计划正在启动中，无法删除，请巡航结束后再试。";
    public static final String QUERY_PLATFORM_CONTROL_IF_EXIST = "查询云台控制表是否存在数据------";
    public static final String BATCHDELPATROL_METHOD_EXCEPTION = "videoClient.batchDelPatrol方法异常==";
    public static final String START_TIME_PERIOD_ARR = "startTimePeriodArr";
    public static final String PATROL_NAME_REPEAT = "巡航计划名称重复，请重新修改！";
    public static final String PATROL_NAME_NOT_NULL = "巡航计划名称不能为空，请重新修改！";
    public static final String PATROL_START_TIME_NOT_NULL = "巡航计划启动时间段不能为空，请重新输入！";
    public static final String PATROL_NOT_ARRIVE_START_TIME = "当前不在巡航计划的运行时间内，将会在设定时间启动该巡航计划";
    public static final String PATROL_NOT_ARRIVE_START_TIME_BATCH = "巡航计划当前不在启动时间内，将会在设定时间启动该巡航计划";
    public static final String CHK_ORDER_DEVICES = "该设备已退订";
    public static final String PATROL_OCCUPY_NOT_START = "该通道巡航时间已被占用，无法启动巡航计划";
    public static final String OTHER_PATROL_EXEC = "该摄像机已有其他巡航计划在执行，请将其停用后再试。";
    public static final String PATROL_NOT_EXIST = "该巡航计划不存在，请重新操作！";
    public static final String PATROL_CUTOVER_START = "######------巡航计划历史数据割接接口------######---START---######";
    public static final String PATROL_CUTOVER_END = "######------巡航计划历史数据割接接口------######---END---######";
    public static final String PATROL_CUTOVER_PATROL_LIST = "---巡航计划历史数据割接接口---需要处理的巡航集合：";
    public static final String PATROL_CUTOVER_PATROL_CRUISE_STATUS = "---巡航计划历史数据割接接口---查询视频子平台该巡航状态：";
    public static final String BATCHSTARTORENDPATROL_METHOD_EXCEPTION = "videoClient.batchStartOrEndPatrol方法异常==";
    public static final String WAITING_IN_LINE = "云台正在排队中，请稍后再试！";
    public static final String THE_PATROL_ONLY = "该巡航仅在";
    public static final String TIME_PERIOD_CRUISE = "小时时间段内可以巡航！";
    public static final String DO_PATROL_REQUEST = "==========6.30启用/停用巡航路径doPatrol请求报文：";
    public static final String PATROL_TIME_PERIOD =
            "00:00,00:10,00:20,00:30,00:40,00:50," +
                    "01:00,01:10,01:20,01:30,01:40,01:50," +
                    "02:00,02:10,02:20,02:30,02:40,02:50," +
                    "03:00,03:10,03:20,03:30,03:40,03:50," +
                    "04:00,04:10,04:20,04:30,04:40,04:50," +
                    "05:00,05:10,05:20,05:30,05:40,05:50," +
                    "06:00,06:10,06:20,06:30,06:40,06:50," +
                    "07:00,07:10,07:20,07:30,07:40,07:50," +
                    "08:00,08:10,08:20,08:30,08:40,08:50," +
                    "09:00,09:10,09:20,09:30,09:40,09:50," +
                    "10:00,10:10,10:20,10:30,10:40,10:50," +
                    "11:00,11:10,11:20,11:30,11:40,11:50," +
                    "12:00,12:10,12:20,12:30,12:40,12:50," +
                    "13:00,13:10,13:20,13:30,13:40,13:50," +
                    "14:00,14:10,14:20,14:30,14:40,14:50," +
                    "15:00,15:10,15:20,15:30,15:40,15:50," +
                    "16:00,16:10,16:20,16:30,16:40,16:50," +
                    "17:00,17:10,17:20,17:30,17:40,17:50," +
                    "18:00,18:10,18:20,18:30,18:40,18:50," +
                    "19:00,19:10,19:20,19:30,19:40,19:50," +
                    "20:00,20:10,20:20,20:30,20:40,20:50," +
                    "21:00,21:10,21:20,21:30,21:40,21:50," +
                    "22:00,22:10,22:20,22:30,22:40,22:50," +
                    "23:00,23:10,23:20,23:30,23:40,23:50";
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String START_STOP = "startStop";
    public static final String IF_CRUISE = "ifCruise";


    public static final String ERROR_STORAGE_TYPE = "存储类型值有误";
    public static final String IS_ALL_Day = "isAllDay";
    public static final String TIMESECTIONS = "timeSections";
    public static final String RECORD_PLAN_ID = "recordPlanId";
    public static final String STORAGE_TYPE = "storageType";
    public static final String START_TIME_SUFFIX = " 00:00:00";
    public static final String END_TIME_SUFFIX = " 23:59:59";

    public static final String ORDERNUM = "orderNum";
    public static final String DEVNUM = "devNum";
    public static final String VALIDORDERNUM = "validOrderNum";
    public static final String ID = "id";
    public static final String APPKEY = "appKey";
    public static final String SECRETKEY = "secretKey";
    public static final String CUSTOMERNAME = "customerName";
    public static final String TYPE = "type";

    public static final String FALLBACKMSG = "请求异常,进入fallback，异常信息{}";
    public static final String ADMCODE = "admCode";
    public static final String AREAINFOLIST = "areaInfoList";
    public static final String AREACODE = "areaCode";
    public static final String SITECODE = "siteCode";
    public static final String CODE901 = "901";
    public static final String ALARMTYPELIST = "alarmTypeList";
    public static final String LOGMESS1 = "共获取数据";
    public static final String LOGMESS2 = "条，成功插入";
    public static final String LOGMESS3 = "条！成功删除";
    public static final String LOGMESS4 = "进入异常，异常信息为：";
    public static final String LOGMESS5 = "条！成功更新";
    public static final String ALGOMINTYPECODE = "algoMinTypeCode";
    public static final String WARN_MESS1 = "设备不能为空";
    public static final String WARN_MESS2 = "获取预置位图片失败，如需再次获取，请在实时视频中重新编辑该预置位或通过实时标绘进行算法配置。";

    public static final String CONFLICTEDLIST = "conflictedlist";
    public static final String STORAGEDAYLIST = "storageDaylist";
    public static final String CURRENTPAGE = "currentPage";
    public static final String RECORDTYPE = "recordType";
    public static final String STARTTIME = "startTime";
    public static final String ENDTIME = "endTime";
    public static final String NOT_FIND_VIDEO = "未查询到录像文件!";
    public static final String NOT_SET_VIDEO_PLAN = "该租户未设置录像计划!";

    public static final String PEDDING_DISPOSAL = "peddingDisposal";
    public static final String DEV_LIST = "devList";
    public static final String CHANNEL_COUNT_OFFLINE = "channelCountOffline";
    public static final String CHANNEL_COUNT_ONLINE = "channelCountOnline";
    public static final String CHANNEL_COUNT_TOTAL = "channelCountTotal";
    public static final String DEV_COUNT_ONLINE = "devCountOnline";
    public static final String DEV_COUNT_OFFLINE = "devCountOffline";
    public static final String DEVICE_COUNT_TOTAL = "deviceCountTotal";

    //地图类型01：高德  02：百度  03：天地图
    public static final String MAP_TYPE_AMAP = "01";
    public static final String MAP_TYPE_BAIDU = "02";
    public static final String MAP_TYPE_TIAN = "01";

    public static final String TENANT_ADMIN = "1";

    public static final String DOES_NOT_RENT_THIS_DEVICE = "当前用户未租用此设备";

    /**
     * 1-岸基雷达；2-传感器；3-运管侧雷达标识
     */
    public static final int IOT_DEVICE_RADAR = 1;
    public static final int IOT_DEVICE_SENSOR = 2;
    public static final Integer IOT_DEVICE_RADAR_3 = 3;

    public static final String DEVICE_LONG_LAT_REDIS_KEY = "device:gis:";

    public static final String TABLE_NAME = "sensor_attribute_report";
    public static final String TABLE_NAME_MOR = "sensor_attribute_report_";
    public static final String TABLE_NAME_DEV = "vm_iot_device_t_";
    public static final String TABLE_SENSOR_ATTRIBUTE_REPORT_ALL = "sensor_attribute_report_all_";
    public static final String TABLE_NAME_MINUTE_ALL = "sensor_attribute_report_minute_all";
    public static final String TABLE_NAME_HOUR_ALL = "sensor_attribute_report_hour_all";
    public static final String TABLE_NAME_DAY_ALL = "sensor_attribute_report_day_all";

    public static final String SENSOR_EVENT_TABLE_NAME = "vm_sensor_event_info";

    public static final String SENSOR_EVENT_FILE_TABLE_NAME = "vm_sensor_file_info";

    public static final String TABLE_NAME_MOR_STATUS = "vm_sensor_monitor_status_";

    public static final String MINUTE = "minute";
    public static final String HOUR = "hour";

    public static final String TABLE_RADAR_DEVICE_CONFIG_REL = "radar_device_config_rel";
    public static final String CURRENT_MONTH_FORMAT = "yyyyMM";

    public static final String FACTOR_PRESSURE = "pressure";
    public static final String FACTOR_TEMPERATURE = "temperature";
    public static final String FACTOR_HUMIDITY = "humidity";
    public static final String FACTOR_WINDSPEED = "windSpeed";
    public static final String FACTOR_PRECIP = "precip";

    public static final String FACTOR_PRESSURE_UNIT = "(MPa)";
    public static final String FACTOR_TEMPERATURE_UNIT = "(°C)";
    public static final String FACTOR_HUMIDITY_UNIT = "(HPa)";
    public static final String FACTOR_WINDSPEED_UNIT = "(m/s)";
    public static final String FACTOR_PRECIP_UNIT = "(mm)";

    public static final Integer DEVICETYPE_8 = 8;
    public static final Integer DEVICETYPE_10 = 10;
    //喇叭
    public static final Integer DEVICETYPE_12 = 12;

    public static final String SUCCESS_SMALL = "success";
    public static final String FAIL_SMALL = "fails";
    public static final String CHANNELCODE = "channelCode";
    public static final String ALARMLINKAGEID = "alarmLinkageId";

    public static final Integer OPT_TYPE_ADD = 0;

    public static final Integer OPT_TYPE_UPDATE = 1;

    public static final Integer OPT_TYPE_DELETE = 2;

    public static final String DELETE_LOG_REQ_MSG = "/acas/visit/config/delete 请求报文：{}";
    public static final String DELETE_LOG_RESP_MSG = "/acas/visit/config/delete 响应报文：{}";

    public static final String DELETE_ERROR_MSG = "deleteCascade error";

    public static final String ALLOCATION_LOG_REQ_MSG = "/acas/visit/config/allocation 请求报文：{}";

    public static final String ALLOCATION_LOG_RESP_MSG = "/acas/visit/config/allocation 响应报文：{}";

    public static final String ALLOCATION_LOG_ERROR_MSG = "allocation error";

    public static final String SYNC_CASCADE_PLATFORM_REQ_MSG = "基础平台同步级联平台信息入参报文: {}";

    public static final String SYNC_CASCADE_PLATFORM_SUCCESS_MSG = "基础平台同步级联平台信息报文: {}";

    public static final String SUCCESS_CODE = "0000";
    public static final String ERORR_CODE = "8888";
    public static int TWENTY = 20;
    public static final String NO_MORE_THAN_TWENTY = "巡航名称长度不能超过20个字符";

    //无人机
    public static final Integer DEVICETYPE_11 = 11;

    //执法记录仪
    public static final Integer DEVICETYPE_13 = 13;
    public static final String CHECKPOINT_ALARM_TYPES = "checkpoint_alarm_types";

    public static final String IMAGE = "image";

    public static final String RECORDER_DEVICE_STATUS = "recorder_device_status";

    public static final String RECORDER_DEVICE_MFGR_CODE = "recorder_device_mfgr_code";

    public static final String QUERY_DEVICE_TYPE_ERROR = "查询设备类型有误！";
}

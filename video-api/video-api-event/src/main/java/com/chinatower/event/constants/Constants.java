package com.chinatower.event.constants;


/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: Constants.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/24 09:45
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public class Constants {

    private Constants(){}

    /**
     * 分页参数
     */
    public static final int PAGE_NUM = 1;
    public static final int PAGE_SIZE = 10;

    public static final String TABLE_SCHEMA = "video_platform";

    /**
     * 告警缓存查询条件
     */
    public static final String ORDER_LIST_QUERY = "ORDER_LIST_QUERY_";
    public static final String TOWER_NAME = "中国铁塔";

    public static final String PARAM_ALARM_PICTURE_URL = "alarmPictureUrl";

    public static final String PARAM_AIR_VIDEO = "无人机视频-";

    public static final String ALPHA = ":alpha=";
    //枚举Redis前缀
    public static final String ENUM_INFO = "enum_info_";
    //简称前缀
    public static final String ABBREVIATION_PREFIX = "wpzf_";
    //初始化图层模板配置
    public static final String GDWPZF_TCMB = "gdwpzf_tcmb01";
    public static final String KCWPZF_TCMB = "kcwpzf_tcmb02";
    public static final String FNHWPZF_TCMB = "fnhwpzf_tcmb03";
    public static final String FLHWPZF_TCMB = "flhwpzf_tcmb04";

    public static final String COMMA = ",";

    public static final String QUESTION_MARK = "?";

    public static final String AND_SIGN = "&";

    public static final String EQUAL_SIGN = "=";

    // 告警状态枚举
    public static final int ORDER_STATUS = 3;

    // 告警来源枚举
    public static final int WARNING_SOURCE = 400;

    public static final int WARNING_SOURCE_NAME = 400;

    public static final String WARNING_SOURCE_COMMON = "warning_source_common";
    public static final String LOCALIZATION_FLAG = "localization_flag"; //是否为国产化环境

    public static final String SHOW_FLAG = "SHOW";
    // 事件等级枚举类型
    public static final int EMERGENCY_LEVEL = 41;

    // 告警等级枚举
    public static final String ALARM_LEVEL = "alarm_level";
    // 告警重要等级枚举
    public static final int IMPORTANCE_LEVEL = 90;
    //告警区域类型实时绘制
    public static final String ALARM_AREA_TYPE1 = "1";
    //告警区域类型PT值范围
    public static final String ALARM_AREA_TYPE2 = "2";

    public static final String FORMAT_S = "%s,%s";
    //摄像机厂家枚举
    public static final int MODEL_ID = 103;
    //物联设备厂家枚举
    public static final String IOT_MODEL_ID = "iot_device_mfgr_code";
    //无人机厂家枚举
    public static final String UAV_MANUFACTURER = "uav_manufacturer";
    //机巢厂家枚举
    public static final String UAV_NEST_MANUFACTURER = "uav_nest_manufacturer";
    /**
     * 告警研判表
     * vm_alarm_operate_t
     */
    public final static String WARN_ALARM_OPERATE = "vm_alarm_operate_t";

    /**
     * 告警压制表
     */
    public final static String WARN_WARNING_INTERCEPT = "vm_warning_intercept_t";

    public static final String WARN_ORDER_TEBLE = "vm_warning_order_t_";
    //通知类型枚举
    public static final int MSG_TYPE = 104;
    //通知类型枚举
    public static final int NOTICE_RESOURCE = 102;

    //告警环节枚举
    public static final int ENUM_FLOW = 13;

    //告警开关枚举
    public static final int ALARM_SWITCH = 91;
    public static final String ALARM_SWITCH_VAR = "91";

    // 默认告警压制时间
    public static final int DEFAULT_TIME = 93;

    //需发送短信摄像机的告警来源
    public static final String DEVICE_WARNING_SOURCE = "1,3,4";
    public static final String DEFAULT_FLOW = "1,2,4,5";
    public static final String SOUND_ID = "sound0000";
    public static final String REAL_LINK_TYPE_ALL = "0,1,2,4,5,6,7";
    public static final String FIRST_LINK = "-1";
    /** 可进入运营研判的告警来源枚举 */
    public static final String OPERATION_ALARM_SOURCE = "1,4,9";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String NINE = "9";
    public static final String TEN = "10";
    public static final String EIGHT = "8";
    /** 1-1级，需应用在告警列表展示，默认值为1；2-2级，需在工作台内静默展示；*/
    public static final String IMPORTANCE_LEVEL_1 = "1";

    /** 1-1级，需应用在告警列表展示，默认值为1；2-2级，需在工作台内静默展示；*/
    public static final String IMPORTANCE_LEVEL_2 = "2";

    public static final String VALUE = "value";
    public static final String YES_FLAG = "1";
    public static final String TWO_FLAG = "2";
    public static final String VALID_STATE = "0";
    public static final String ZERO_STRING = "0.0";
    //告警来源: 1-AI上报； 2-人工上报；3-一键告警
    public static final String WARNING_SOURCE_1 = "1";
    //告警来源: 1-AI上报； 2-人工上报；3-一键告警
    public static final String WARNING_SOURCE_2 = "2";
    //告警来源: 1-AI上报； 2-人工上报；3-一键告警
    public static final String WARNING_SOURCE_3 = "3";
    //告警来源: 4-摄像机算法告警
    public static final String WARNING_SOURCE_4 = "4";
    //告警来源：8-物联告警
    public static final String WARNING_SOURCE_8 = "8";
    //告警来源：8-卫星告警
    public static final String WARNING_SOURCE_9 = "9";
    //告警来源：10-雷达告警
    public static final String WARNING_SOURCE_10 = "10";
    //告警来源: 11-第三方告警
    public static final String WARNING_SOURCE_11 = "11";
    //告警来源: 12-无人机告警
    public static final String WARNING_SOURCE_12 = "12";
    //通知类型:1：告警消息 5：系统消息，6 ：超期提醒
    public static final String NOTIFY_TYPE_1 = "1";
    public static final String NOTIFY_TYPE_5 = "1";
    //消息通知位置0：全部，1：大屏，2：工作台
    public static final String SHOW_TYPE_ALL = "0";
    public static final String SHOW_TYPE_BIGSCREEN = "1";
    public static final String SHOW_TYPE_WORK = "2";
    //设备前端告警标识
    public static final String FORWARD_ALGORITHM = "18";

    //研判结果：误报
    public static final String JUDGE_RESULT_0 = "0";
    //研判结果：真实告警
    public static final String JUDGE_RESULT_1 = "1";
    //研判结果：重复告警
    public static final String JUDGE_RESULT_2 = "2";
    //研判结果：待研判
    public static final String JUDGE_RESULT_3 = "3";
    //研判结果：关闭
    public static final String JUDGE_RESULT_4 = "4";
    //研判结果：删除
    public static final String JUDGE_RESULT_5 = "5";

    //不会签
    public static final String SIGN_FLAG_0 = "0";
    //会签
    public static final String SIGN_FLAG_1 = "1";

    //0:审批失败 1:待审批 2:审批通过
    public static final String APPROVE_STATUS_0 = "0";
    public static final String APPROVE_STATUS_1 = "1";
    public static final String APPROVE_STATUS_2 = "2";


    public static final String DATE_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_YMDHMS_STR = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DATE_YM = "yyyy-MM-dd";
    public static final String SPLIT_SIGN = "_";

    //工单状态枚举值
    public static final int ORDER_STATUS_3 = 3;
    public static final String ORDER_STATUS_6 = "6";
    public static final String ANGLE_VALUE_N = "N";
    public static final String ANGLE_VALUE_S = "S";
    public static final String ANGLE_VALUE_W = "W";
    public static final String ANGLE_VALUE_E = "E";

    public static final String CODE_910 = "910";
    public static final int CODE_200 = 200;
    public static final String CENTER_CODE = "10000000001";
    public static final String ALARM_TYPE_VALUE = "1819000"; //卡口告警
    public static final String PARAM_TYPE_NAME = "type_name";
    public static final String PARAM_TYPE_VALUE = "type_value";
    public static final String PARAM_ENLARGE_FLAG = "enlarge_flag";
    public static final String PARAM_TOTAL = "total";
    public static final String PARAM_ALARM_PIC = "告警图片-";

    public static final String PARAM_AIR_PIC = "无人机图片-";
    public static final String PARAM_PRESITE_PIC = "预置位抓图-";
    public static final String DOWNLOAD_PIC_ERROR_MSG = "downLoad-读取图片URL异常:";
    public static final String DOWNLOAD_ERROR_MSG = "downLoad-异常:";
    public static final String PARAM_ENUM_CODE = "enumCode";
    public static final String PARAM_CREATE_TIME = "createTime";
    public static final String PARAM_ENUM_NAME = "enumName";
    public static final String PARAM_DEVICE_CODE = "deviceCode";
    public static final String PARAM_DEVICE_NAME = "deviceName";
    public static final String PARAM_IF_TIMEOUT = "ifTimeout";
    public static final String PARAM_VIDEO_URL = "videoUrl";
    public static final String PARAM_CONFIG_ID = "configId";
    public static final String PARAM_GROUP_ID = "groupId";
    public static final String PARAM_CONTINUE_VALUE = "continueValue";
    public static final String PARAM_SUDDEN_VALUE = "suddenValue";
    public static final String PARAM_VIDEO_TI_URL = "frontAlarmVedioUrl";
    public static final String PARAM_FILE_ALARM_PIC_URL = "frontAlarmPicUrl";
    public static final String PARAM_FILE_IMG_URL_ICON = "fileImgUrlIcon";
    public static final String PARAM_FILE_IMG_URL_SRC = "fileImgUrlSrc";
    public static final String PARAM_FILE_IMG_URL_TI = "frontAlarmPicUrl";
    public static final String PARAM_CONFIDENCE_LEVEL = "confidenceLevel";
    public static final String PARAM_CONFIDENCE_LEVEL_MIN = "confidence_level_min";
    public static final String PARAM_CONFIDENCE_LEVEL_MAX = "confidence_level_max";
    public static final String PARAM_COMP_CONFIDENCE_LEVEL = "compConfidenceLevel";
    public static final String SYS_VEHICLE_TYPES = "sys_vehicle_types";
    public static final String VEHICLE_COLOR = "vehicle_color"; //车辆颜色
    public static final String VEHICLE_NUM_COLOR = "vehicle_num_color"; //车牌颜色
    public static final String WATER_QUALITY = "extendedFieldsOfWaterQuality"; //水位
    public static final String PARAMLIST = "receive param: {}, collectionName: {}";
    public static final String USETIME = "耗时: {}";

    public static final String PARAM_CONFIG_VALUE = "configValue";
    public static final String RESULT_DATA = "data";
    public static final String PLATFORM_CODE = "platform_code";
    public static final String SEND_LOCAL_SATELLITE = "send_local_Satellite";
    public static final String VISIBLE_PTZ_CONFIG = "visible_ptz_config";
    public static final String PARAM_PRO_CODE = "province_code";
    public static final String PARAM_CITY_CODE = "city_code";
    public static final String PARAM = "param";
    public static final String PARAM_NODES = "nodes";
    public static final String PARAM_LABEL = "label";
    public static final String PARAM_ALARM_VIDEO = "alarmVideo";
    public static final String VM_FLOW_LINK_ATTR_DTO = "vmFlowLinkAttrDto";
    public static final String PARAM_FILE_VIDEO_URL = "fileVideoUrl";
    public static final String PARAM_ALARM_TAG_PIC = "alarmTagPic";
    public static final String PARAM_TAG_PICTURE_URL = "tagPictureUrl";
    public static final String PARAM_ALARM_ORIG_PIC = "alarmOrigPic";
    public static final String PARAM_WARNING_ORDER_ID = "warningOrderId";
    public static final String PARAM_WARNING_ORDER_IDS = "warningOrderIds";
    public static final String PARAM_DELETE_FLAG = "deleteFlag";
    public static final String PARAM_IS_AMPLIFICATION = "enlargeFlag";
    public static final String PARAM_TABLE_NAME = "tableName";
    public static final String PARAM_WARNING_TYPE_ID = "warningTypeId";
    public static final String PARAM_ICON = "icon";
    public static final String PARAM_WARNING_TYPE_NAME = "warningTypeName";
    public static final String PARAM_EMERGENCY_LEVEL = "emergencyLevel";
    public static final String PARAM_EMERGENCY_LEVEL_NAME = "emergencyLevelName";
    public static final String PARAM_ORDER_STATUS = "orderStatus";
    public static final String PARAM_ORDER_STATUS_DB = "order_status";
    public static final String PARAM_ORDER_STATUS_NAME = "orderStatusName";
    public static final String PARAM_ENABLE_ACCEPTANCE_MODE = "enableAcceptanceMode";
    public static final String PARAM_IMG_URL = "imgUrl";
    public static final String PARAM_FILE_URL = "fileUrl";
    public static final String PARAM_FILES = "files";
    public static final String PARAM_PICTURE_URL = "originalPictureUrl";
    public static final String PARAM_ORDER_ID = "orderId";
    public static final String PARAM_CHANNEL_CODE = "channelCode";
    public static final String PARAM_ALARM_CODE = "alarmCode";
    public static final String PARAM_WARNING_SOURCE = "warningSource";
    public static final String PARAM_WARNING_SOURCE_NAME = "warningSourceName";
    public static final String PARAM_ORDER = "order";
    public static final String PARAM_IMGS = "imgs";
    public static final String PARAM_VIDEO = "video";
    public static final String PARAM_FILE_IMAGE_URL = "fileImageUrl";
    public static final String PARAM_T_I_VIDEO="TIvideo";
    public static final String PARAM_MONITORS ="monitors";
    public static final String PARAM_DEVICEINFO ="deviceInfo";
    public static final String PARAM_POLYGON_OPERATE ="polygonOperate";
    public static final String PRESET_NAME ="presetName";
    public static final String PARAM_DEVICE_LABEL ="deviceLabel";
    public static final String PARAM_REAL_WARNING_SOURCE ="realAlarmSource";
    public static final String PARAM_IOT_DEV_INFO_LIST ="iotDevInfoList";
    public static final String PARAM_UAV_DEV_INFO_LIST ="uavDevInfoList";
    public static final String PARAM_SATELLITE_DEVICE_LIST ="satelliteDeviceList";
    public static final String PARAM_SPOT ="spot";
    public static final String PARAM_RADAR_DEVICE ="radarDevice";
    public static final String PARAM_DEVICE_SITE_LIST ="deviceSiteList";
    public static final String PARAM_MONITORS_LIST ="monitorsList";
    public static final String PARAM_RELDEVICE="reldevice";
    public static final String PARAM_FEATURES="features";
    public static final String PARAM_FEATURE_ID="featureId";
    public static final String PARAM_GEOMETRY="geometry";
    public static final String PARAM_GEOMETRY_TYPE="geomeryType";
    public static final String PARAM_DEVICE_ADDRESS = "deviceAddress";
    public static final String PARAM_CHANNEL_NAME = "channelName";
    public static final String PARAM_JUDGE_RESULT = "judgeResult";
    public static final String PARAM_TIME_LIMIT = "timeLimit";
    public static final String PARAM_TIME_LIMIT_DB = "time_limit";
    public static final String PARAM_TENANT_ID = "tenantId";
    public static final String PARAM_TYPE_VALUE_ = "typeValue";
    public static final String PARAM_NOTICE_MODE = "noticeMode";
    public static final String PARAM_MSG_ID = "msgId";
    public static final String PARAM_ADDRESS = "address";
    public static final String PARAM_ORGID = "orgId";
    public static final String PARAM_ORGIDLIST = "orgIdList";
    public static final String PARAM_ALARMTIME = "alarmTime";
    public static final String PARAM_TYPENAME = "typeName";
    public static final String PARAM_PUSH_CUSTOMER_FLAG = "pushCustomerFlag";
    public static final String PARAM_MSG = "msg";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_ID = "id";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_AREA = "area";
    public static final String PARAM_DAYS = "days";
    public static final String BSM = "bsm"; //草原地块id
    public static final String CCL = "ccl"; //产草量
    public static final String BSL = "bsl"; //补饲量
    public static final String TUBAN_NAME = "tuban_name"; //草原地块名称
    public static final String MIANJI = "mianji"; //草原地块面积
    public static final String PROPERTIES = "properties";
    public static final String FORAGEYIELD = "forageYield"; //产草量
    public static final String FEEDSUP = "feedSup"; //补饲量
    public static final String PARAM_GRAZESEASON = "grazeSeason";
    public static final String PARAM_NOTIFYMANAGER = "notifyManager";
    public static final String PARAM_NOTIFICATIONLINK = "notificationLink";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_IMPORTANCE_LEVEL = "importanceLevel";
    public static final String PARAM_IMPORTANCE_LEVEL_DB = "importance_level";
    public static final String PARAM_MODEL_ID_NUM = "modelId";
    public static final String PARAM_MODEL_FACTORY_NAME = "modelFactoryName";
    public static final String PARAM_MSG_TYPE_NUM = "msgType";
    public static final String PARAM_MSG_TYPE_NAME = "msgTypeName";
    public static final String PARAM_NOTICERESOURCE = "noticeResource";
    public static final String PARAM_NOTICERESOURCE_NAME = "noticeResourceName";
    public static final String PARAM_IMPORTANCE_LEVEL_NAME = "importanceLevelName";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_CLIENTTYPE = "clienttype";
    public static final String PARAM_NULL = "null";
    public static final String PARAM_LINK_ID = "linkId";
    public static final String PARAM_RES_LINK_ID = "link_id";
    public static final String PARAM_REAL_LINK_TYPE = "realLinkType";
    public static final String PARAM_LINK_NAME = "linkName";
    public static final String PARAM_NOTIFY_TYPE = "notifyType";
    public static final String PARAM_CREATOR = "creator";
    public static final String PARAM_NOTIFY_TITLE = "notifyTitle";
    public static final String PARAM_ALARM_BODY = "alarmBody";
    public static final String PARAM_ALARM_BODY_FIELD = "alarm_body";
    public static final String PARAM_ALGORITHM_CODE_FIELD = "algorithm_code";
    public static final String PARAM_ALARM_TIME_FIELD = "alarm_time";
    public static final String PARAM_WARNING_TYPE_ID_FIELD = "warning_type_id";
    public static final String PARAM_EMERGENCY_LEVEL_FIELD = "emergency_level";
    public static final String PARAM_NOTICE_RESOURCE_FIELD = "notice_resource";
    public static final String PARAM_DELETE_FLAG_FIELD = "delete_flag";
    public static final String PARAM_APP_VER_CODE_FIELD = "app_ver_code";
    public static final String PARAM_INDUSTRY_CODE_FIELD = "industry_code";
    public static final String PARAM_REGION_CODES = "regionCodes";
    public static final String PARAM_REGION_CODE = "region_code";
    public static final String PARAM_REGION_NAME = "region_name";
    public static final String PARAM_ORIGINAL_JSON = "originalJson";
    public static final String PARAM_ALARMS = "alarms";
    public static final String PARAM_ALARMCODELIST = "alarmCodeList";
    public static final String PARAM_CUSTOMER_CODE = "customerCode";
    public static final String PARAM_TYPE_KEY = "type";
    public static final String PARAM_CALL_BACK_URL = "callbackUrl";
    public static final String PARAM_REQUEST_JSON = "requestJson";
    public static final String PARAM_LOCATION_KEY = "location";
    public static final String PARAM_PROVINCE_ID = "province_id";
    public static final String PARAM_PROVINCE_NAME_FIELD = "province_name";
    public static final String PARAM_CITY_ID = "city_id";
    public static final String PARAM_CITY_NAME_FIELD = "city_name";
    public static final String PARAM_COUNTY_ID = "county_id";
    public static final String PARAM_COUNTY_NAME_FIELD = "county_name";
    public static final String PARAM_TOWN_NAME_FIELD = "town_name";
    public static final String PARAM_TODAY_NUM = "today_num";
    public static final String PARAM_COUNTY = "county";
    public static final String PARAM_PROVINCE_NAME = "provinceName";
    public static final String PARAM_CITY_NAME = "cityName";
    public static final String PARAM_COUNTY_NAME = "countyName";
    public static final String ALARM_TIME_KEY = "alarm_time_";
    public static final String ALARM_PT_KEY = "alarm_pt_";
    public static final String ALARM_THIRD_KEY = "alarm_third_";
    public static final String ALARM_POSITION_KEY = "alarm_position_";
    public static final String PARAM_INTERVAL_END_TIME = "intervalEndTime";
    public static final String PARAM_DATA = "data";
    public static final String PARAM_GRID_ID = "gridId";
    public static final String PARAM_GRID_NAME = "gridName";
    public static final String PARAM_SUCCESS = "SUCCESS";
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_INDUSTRY_CODE = "industryCode";
    public static final String PARAM_OPERATE_STATUS = "operateStatus";
    public static final String PARAM_USER_RECORD = "userRecord";
    public static final String PARAM_ORG_RECORD = "orgRecord";
    public static final String PARAM_FLOW_TEMPLATE = "flowTemplate";
    public static final String PARAM_SOURCE = "source";
    public static final String PARAM_OPERATE_TYPE = "operateType";
    public static final String PARAM_CREATE_USER = "createUser";
    public static final String PARAM_CREATE_USER_NAME = "createUserName";
    public static final String PARAM_PTZ_P = "ptzP";
    public static final String PARAM_PTZ_T = "ptzT";
    public static final String PARAM_PTZ_Z = "ptzZ";
    public static final String PARAM_PRESET_ID = "presetId";
    public static final String PARAM_FLOW_ID = "flowId";
    public static final String PARAM_WARNING_PROCESS_ID = "warningProcessId";
    public static final String PARAM_CALL_URL = "url";
    public static final String PARAM_MSG_TAG = "msgTag";
    public static final String PARAM_MSG_TAG_SUCCESS = "00000000";
    public static final String SUCCESS_CODE = "0000";

    public static final String PARAM_RECIVETIME = "reciveTime";
    public static final String PARAM_ALARM_TIME = "alarmTime";

    public static final String PARAM_AUDIT_TIME = "auditTime";

    public static final String PARAM_APPLY_TIME = "applyTime";

    public static final String ALRAM_LIMIT_TIME = "2021-07-01";
    public static final String REMARK = "checkRemark";

    public static final String REGION_CODE = "region_code";
    public static final String REGION_NAME = "regionName";

    public static final String PARAM_INDUSTRY_CODE_999999 = "999999";
    public static final String PARAM_APP_VER_CODE_1 = "1.0";

    public static final String ERROR_MSG_ORDER_NUM = "最大可选50个告警事件";
    public static final String ERROR_MSG_ORDER_MSG = "没有告警工单";

    public static final String RES_WARNING_ORDER_ID = "warning_order_id";
    public static final String RES_WARNING_SOURCE = "warning_source";
    public static final String RES_DEVICE_CODE = "device_code";
    public static final String PARAM_COUNTY_CODE = "county_code";
    public static final String PARAM_TOWN_CODE_FILD = "town_code";
    public static final String PARAM_FIND_NAME = "find_name";
    public static final String RES_TENANT_ID = "tenant_id";
    public static final String RES_MSG_TYPE = "msg_type";
    public static final String RES_JUDGE_RESULT = "judge_result";
    public static final String RES_PROCESS_NAME = "process_name";
    public static final String RES_PROCESS_USER_NAME = "process_user_name";
    public static final String RES_MODIFIER_DATE = "modifier_date";
    public static final String RES_PROCESS_ID = "processId";
    public static final String RES_ACCEPT_PROCESS_ID = "acceptProcessId";
    public static final String RES_ACCEPT_PROCESS_NAME = "acceptProcessName";
    public static final String RES_ACCEPT_PROCESS_PHONE = "acceptProcessPhone";
    public static final String RES_PROCESS_NAME_ = "processName";
    public static final String PROCESS_NAME_LIST = "processNameList";
    public static final String RES_GROUP_ID = "groupId";
    public static final String RES_MOBILE_PHONE = "mobilePhone";
    public static final String RES_JUDGED_PHONE = "judgedPhone";
    public static final String RES_JUDGED_NAME = "judgedName";
    public static final String RES_ORG_NAME = "orgName";
    public static final String RES_LEADER_ID = "leaderId";
    public static final String RES_LEADER_NAME = "leaderName";
    public static final String PARAM_LIST = "list";
    public static final String CITY_ID = "cityId";
    public static final String NUM = "num";
    public static final String HMS = " 23:59:59";
    public static final String AUTH_TOKEN = "authToken";
    public static final String STATES = "0";
    public static final String DELETE_FLAG = "0";
    public static final String DEFAULT_DATE = "2021-07-01";

    public static final String NO_USER_INFO = "无法获取当前登录用户信息，请重新登陆";
    public static final String NO_MANAGE_PERMISSION = "当前登录用户无操作权限！";
    public static final String WARNING_ORDER_ID_MSG = "warningOrderId不能为空！";
    public static final String ALARM_TIME_MSG = "alarmTime不能为空！";
    public static final String WARNING_ORDER_MSG = "未查询到告警数据！";
    public static final String WARNING_ORDER_COLLECTION = "未查询到收藏数据！";
    public static final String INTERFACE_613_ERROR = "调用6.13异常: ";
    public static final String PARAM_ERROR = "参数错误";
    public static final String ALARM_NO_DATA = "当前编码：{0},无法查询告警信息";
    public static final String ORDER_JUDGED = "当前告警已经研判完成，请核查";
    public static final String ORDER_JUDGED_TWO = "当前告警为串行告警，不可研判为重复告警，请核查";
    public static final String INTERCEPT_FAIL_MESSAGE = "操作失败";
    public static final String INTERCEPT_SUCCESS_MESSAGE = "操作成功";
    public static final String OPERATE_TEMP_ERROR = "保存运营研判真警临时表异常：";
    public static final String CHECK_TENANT_ID = "租户编码不能为空!";
    public static final String REAL_LINK_TYPE_MSG_1 = "待研判";
    public static final String REAL_LINK_TYPE_MSG_2 = "待调度";
    public static final String REAL_LINK_TYPE_MSG_4 = "待处置";
    public static final String REAL_LINK_TYPE_MSG_5 = "待核实";
    /**
     * 报错通用提示信息
     */
    public static final String DEFAULT_ERROR_MESSAGE = "系统繁忙，请稍后再试。";

    public static final String ERROR_TIME_MESSAGE = "告警时间为空，或不能早于2021年7月。";
    public static final String LOG_MESSAGEPUSH_PARAM = "告警消息推送入参：";
    public static final String LOG_MESSAGEPUSH_TOWER_PARAM = "运营研判消息推送入参：";
    public static final String LOG_NOTICE_MODE = "通知方式：";
    public static final String LOG_EVENT_INFO = "事件信息：";
    public static final String LOG_AI = "非人工上报 查询组织下部门的组织id 入参：";
    public static final String LOG_UNAI = "人工上报 查询组织下部门的组织id 入参：";
    public static final String ORG_RESULT = "查询组织下部门的组织id 结果：";
    public static final String MSG = "{0},在{1}发现{2}，请登录系统及时处理！";
    public static final String OPERATE_MSG = "截至{0}，今日{1}待研判告警{2}件，全部{1}待研判告警{2}件，请登录系统及时处理！";
    public static final String MESSAGE_PUSH_ERROR_MESSAGE = "告警消息推送异常：";
    public static final String MESS_3 = "告警工单状态异常或不存在!";
    public static final String ORDER_ERROR_MESSAGE = "生成告警工单失败";
    public static final String MESS_FLOW_RECORD = "告警工单环节异常!";
    public static final String LOG_FILE_INFO = "保存文件异常：";
    public static final String MESS_4 = "流转告警工单状态异常!";
    public static final String PROCESS_FLOW_FAIL = "行业调用流程流转失败:";
    public static final String UPLOAD_FAIL_MSG = "上传失败,告警编码[";
    public static final String NOT_EXIST_MSG = "]不存在!";
    public static final String FIELDOFVIEW_MSG = "水平视场角：{},俯仰视场角：{}";

    public static final String PEDDING_DISPOSAL = "peddingDisposal";
    public static final String DEV_LIST = "devList";
    public static final String CHANNEL_COUNT_OFFLINE = "channelCountOffline";
    public static final String CHANNEL_COUNT_ONLINE = "channelCountOnline";
    public static final String CHANNEL_COUNT_TOTAL = "channelCountTotal";
    public static final String DEV_COUNT_ONLINE = "devCountOnline";
    public static final String DEV_COUNT_OFFLINE = "devCountOffline";
    public static final String DEVICE_COUNT_TOTAL = "deviceCountTotal";
    public static final String ONLINE_PERCENTAGE_DEV = "onlinePercentageDev";
    public static final String ONLINE_PERCENTAGE = "onlinePercentage";
    public static final String DIS_ALGORITHM_TASK_NUM = "disAlgorithmTaskNum";
    public static final String UP_ALGORITHM_TASK_NUM = "upAlgorithmTaskNum";
    public static final String DEVICE_NUM = "deviceNum";
    public static final String ALGORITHM_NUM = "algorithmNum";
    public static final String FORE_ALGORITHM_TASK_NUM = "foreAlgorithmTaskNum";
    public static final String AFTER_ALGORITHM_TASK_NUM = "afterAlgorithmTaskNum";
    public static final String START_FOREALGORITHM_TASK_NUM = "startForeAlgorithmTaskNum";
    public static final String START_AFTERALGORITHM_TASK_NUM = "startAfterAlgorithmTaskNum";
    public static final String WARNING_ORDER_EXTRA_FIELD = "extraField";
    public static final String WARNING_ORDER_EXTRA_FIELD_POSE = "relativePathPose";
    public static final String WARNING_ORDER_EXTRA_FIELD_THEM = "relativePathThem";
    public static final String WARNING_ORDER_EXTRA_FIELD_MIR = "relativePathMir";
    public static final String WARNING_ORDER_EXTRA_FIELD_SNAP = "snapShotTime";
    public static final String WARNING_ORDER_EXTRA_FIELD_REAL_CHANNEL_CODE = "realChannelCode";
    public static final String WARNING_ORDER_EXTRA_FIELD_REAL_DEVICE = "realDeviceCode";
    public static final String TYPE_NAME = "typeName";
    public static final String TYPE_ID = "typeId";
    public static final String FIRE_AREA = "fireArea";
    public static final String UNDERLYING_SURFACE_TYPE = "underlyingSurfaceType";
    /**
     * 属地化管理 0正常，1停用
     */
    public static final String DEPENDENCE_MANAGE = "dependence_manage";

    public static final String NO_CURRENT_LINK = "下个环节id（currentLink）不能为空";
    public static final String PENDING_ACCEPTANCE= "待受理";
    public static final String ACCEPTANCE_COMPLETED= "已受理";
    public static final String REASSIGNMENT = "转派";
    public static final String FALLBACK = "回退";
    public static final String COMPLETE = "完成";
    public static final String NOCOMPLETE = "未完成";
    public static final String FALSEALARM= "误报";
    public static final String TRUEPOLICE= "真警";
    public static final String REPEATEDALARM= "重复告警";
    public static final String CLOSED= "已完结";
    public static final String HANDLEDBY= "办理人: ";
    public static final String HANDLINGINSTRUCTIONS= "办理说明: ";

    //消息通知类型    1:APP
    public static final String MESSAGE_PUSH_TYPE_APP = "1";
    //消息通知类型    2:短信
    public static final String MESSAGE_PUSH_TYPE_SMS = "2";
    //消息通知类型    4:PC系统告警
    public static final String MESSAGE_PUSH_TYPE_PC_SYSTEM = "3";
    //消息通知类型    3:PC声音告警
    public static final String MESSAGE_PUSH_TYPE_PC_VIOCE = "4";

    //需过滤用户的告警来源
    public static final String FILTER_WARNING_SOURCE = "1,3,4,8,10";

    public static final String MESSAGE_NO_PUSH_MANAGER = "0";

    //1用户
    public static final String USER_TYPE_1 = "1";
    //2角色
    public static final String USER_TYPE_2 = "2";
    //2角色
    public static final String USER_TYPE_3 = "3";
    //验证码
    public static final String MSG_TYPE_VERIFICATION = "1";
    //短信
    public static final String MSG_TYPE_SMS = "2";

    //正序
    public static final String SORT_TYPE_ASC = "ASC";

    //短信发送区间标志--是
    public static final String MSG_SEND_TIME_TYPE_Y = "Y";
    //短信发送区间标志--否
    public static final String MSG_SEND_TIME_TYPE_N = "N";

    public static final String KEY_ORDER = "order:";
    public static final int REDIS_EXPIRE = 86460;
    public static final String ERROR = "ERROR";
    public static final String RESULTSTAT = "resultStat";
    public static final String CITY_CODE = "cityCode";
    //域名前缀https
    public static final String HTTPS_CODE = "https";
    //替换的图片域名中的一块
    public static final String API_IMG_CODE = "apiImg";
    //星地通图片地址关键字
    public static final String EAGLE_CODE = "119.3.234.26:4605";
    //航天宏图图片地址关键字
    public static final String NJHT_CODE = "fireimgs.zzpeng.cn";
    //截取关键字
    public static final String FIRE_IMAGE_CODE = "/FireImage";
    public static final String SERVICE_CODE = "/service";
    public static final String DOMAIN_PRE_FIX = "domain_Pre_fix";

    public static final String MESS_1 = "经纬度不能为空！";
    public static final String MESS_2 = "城市ID不能为空！";
    public static final String PARAM_DICT_VALUE = "dictValue";
    public static final String PARAM_DICT_LABEL = "dictLabel";

    //地图类型01：高德  02：百度  03：天地图
    public static final String MAP_TYPE_AMAP = "01";
    public static final String MAP_TYPE_BAIDU = "02";
    public static final String MAP_TYPE_TIAN = "01";

    public static final String APPROVE_PERSON = "approvePerson";

    // 默认周边检索查询半径
    public static final String DEFAULT_RADIUS = "10000";
    public static final String RADIUS = "74";

    //0未结束
    public static final String LINK_USER_DEAL_STATUS_0 = "0";
    //1结束
    public static final String LINK_USER_DEAL_STATUS_1 = "1";

    public final static String APP_VER_CODE_TOPIC = "app_ver_code_topic"; //特殊处理字典值

    //卡口告警-告警类型字典值
    public final static String CHECKPOINT_ALARM_TYPES = "checkpoint_alarm_types";

    /**
     * 告警消息
     */
    public static final String SYS_MSG_TYPE_1 = "1";
    /**
     * 运营研判消息
     */
    public static final String SYS_MSG_TYPE_2 = "2";
    /**
     * 领导批示消息
     */
    public static final String SYS_MSG_TYPE_3 = "3";
    /**
     * 巡查任务消息
     */
    public static final String SYS_MSG_TYPE_4 = "4";
    /**
     * 系统消息
     */
    public static final String SYS_MSG_TYPE_5 = "5";

    /**
     * 待办
     */
    public static final String SEARCH_TYPE_1 = "1";
    /**
     * 已办
     */
    public static final String SEARCH_TYPE_2 = "2";
    /**
     * 置信度
     */
    // 摄像机厂家枚举值
    public static final String MODEL = "103";

    // AI 告警来源枚举值
    public static final String AI_NOTICE_RESOURCE = "102";

    public static final String PARAM_NOTICE_RESOURCE = "noticeResource";
    public static final String PARAM_WARNING_RESOURCE = "warningSource";
    public static final String PARAM_WARNING_RESOURCE_NAME = "warningSourceName";
    public static final String PARAM_HEIGHT = "height";
    public static final String PARAM_MODEL_NAME = "modelName";
    public static final String PARAM_VIDEO_LATITUDE = "videoLatitude";
    public static final String PARAM_VIDEO_LONGITUDE = "videoLongitude";

    public static final String PARAM_MODEL_ID = "modelId";

    public static final String PENDING_HANDLE_ID = "pendingHandleId";
    public static final String PENDING_HANDLE_NAME = "pendingHandleName";
    public static final String PARAM_HORI_AZIMUTH_ANGLE = "horiAzimuthAngle";  //水平方位角
    public static final int HORI_AZIMUTH_ANGLE_0 = 0;
    public static final int HORI_AZIMUTH_ANGLE_90 = 90;
    public static final int HORI_AZIMUTH_ANGLE_180 = 180;
    public static final int HORI_AZIMUTH_ANGLE_270 = 270;
    public static final int HORI_PAZIMUTH_ANGLE_360 = 360;

    /**
     * pc端不响报警声
     */
    public static final String PARAM_ALARM_FLAG_0 = "0";
    /**
     * pc端就响报警声音
     */
    public static final String PARAM_ALARM_FLAG_1 = "1";

    public static final String INTERCEPT_CONFIG = "intercept_config:";

    // 林业行业版本编码
    public static final String PRODUCT_CODE_FOREST = "b859d64d5210f4";

    //是否二次研判1-是；0-否
    public static final String IS_SECOND_JUDGE_Y = "1";
    //是否二次研判1-是；0-否
    public static final String IS_SECOND_JUDGE_N = "0";

    //二次研判结果1-真，0-假
    public static final String SECOND_JUDGE_RESULT_0 = "0";
    //二次研判结果1-真，0-假
    public static final String SECOND_JUDGE_RESULT_1 = "1";

    //是否调度标记位 1：是 0：不是
    public static final String BEFORE_DISPATCH_FLAG_1 = "1";
    public static final String BEFORE_DISPATCH_FLAG_0 = "0";

    public static final String NOTICE_RESOURCE_1 = "1";
    public static final String NOTICE_RESOURCE_2 = "2";

    public static final String RESULT_PARAM_YES = "是";
    public static final String RESULT_PARAM_NO = "否";
    public static final String RESULT_PARAM_TRUE = "真";
    public static final String RESULT_PARAM_FALSE = "假";

    public static final String MSG_TYPE_0 = "0";
    public static final String MSG_TYPE_1 = "1";
    public static final String MSG_TYPE_2 = "2";

    public static final String ORDER_FLOW_ID = "orderFlowId";
    public static final String LOGIN_INFO_ERROR = "消息推送获取用户信息异常！";

    //编辑权限
    public static final int ADMIN_EDIT = 1;

    //人员组织树根节点id
    public static final String TOWER_ROOT_AREA = "root9999";
    //人员组织树根节点名称
    public static final String TOWER_ROOT_AREA_NAME = "所有人员";

    public static final String FORMATTED_ADDRESS = "formattedAddress";


    public static final String DAY = "d";
    public static final String HOUR = "h";
    public static final String MINUTE = "m";
    public static final String SECOND = "s";

    //字典中告警压制原因dict_type
    public static final String DICT_TYPE_ALARM_PRESSING_REASON= "alarm_pressing_reason";

    //字典表中是否调用获取用户数据权限开关(默认：1调用，其他不调用)
    public static final String SELECT_USER_POWER_FLAG= "select_user_power_flag";

    public static final String SELECT_INTERCEPT_POWER_FLAG= "select_intercept_power_flag";

    public static final String SELECT_INTERCEPT_POWER_USER_FLAG= "select_intercept_power_user_flag";

    public static final String SELECT_OPERATE_POWER_FLAG= "select_operate_power_flag";

    public static final String SECOND_TASK_OUT_TIME= "second_task_out_time";

    public static final String THIRD_ALARM_TIME= "third_alarm_time";

    public static final String THIRD_ALARM_COUNT= "third_alarm_count";

    //周边半径搜索默认天数区间
    public static final String RADIUS_ALARM_DAY= "radius_alarm_day";

    public static final String TASK_SELECT_INTERCEPT_POWER_FLAG= "task_select_intercept_power_flag";

    public static final String TASK_SELECT_OPERATE_POWER_FLAG= "task_select_operate_power_flag";

    //字典表中是否调用获取用户数据权限行业开关(默认：1调用，其他不调用)
    public static final String SELECT_USER_POWER_FORTRADE_FLAG= "select_user_power_fortrade_flag";

    //字典表中是否调用获取雷达taskId配置开关(默认：1调用，其他不调用)
    public static final String SELECT_USER_POWER_TASK_ID_FLAG= "select_user_power_task_id_flag";
    /**
     * 告警流程配置生效状态
     */
    public static final String ALARM_FLOW_CONFIG_STATUS_VALID = "1";
    /**
     * 告警流程配置失效状态
     */
    public static final String ALARM_FLOW_CONFIG_STATUS_INVALID = "0";
    /**
     * 告警流程提交校验
     */
    public static final String ALARM_FLOW_CONFIG_EXSITS_SOURCEANDTYPE = "当前选中的告警来源下的告警类型已有流程配置，是否需要将原有流程覆盖？";
    public static final String ALARM_FLOW_CONFIG_NOTEXSITS_SOURCEANDTYPE = "未查到重复流程！";
    public static final String ALARM_FLOW_CONFIG_SAVE_SUCCESS = "告警流程配置信息保存成功！";
    public static final String ALARM_FLOW_CONFIG_MOD_SUCCESS = "该告警流程配置已保存成功，将即刻生效，在途告警将按照编辑后的告警流程配置进行处理。";
    public static final String ALARM_FLOW_CONFIG_SAVE_FAIL = "告警流程配置信息保存失败！";
    public static final String ALARM_FLOW_CONFIG_NAME_Y = "该告警流程配置名称已存在！";
    public static final String ALARM_TRANSFER_CONFIG_EXSITS_SOURCEANDTYPE = "当前选中的告警来源下的告警类型已有转移配置，是否需要将原有转移配置覆盖？";
    public static final String ALARM_TRANSFER_CONFIG_NOTEXSITS_SOURCEANDTYPE = "未查到重复转移配置！";
    public static final String ALARM_TRANSFER_CONFIG_NAME_Y = "该告警转移配置名称已存在！";
    public static final String LAYER_TEMPLATE_NAME_Y = "图层模板已存在";
    public static final String LAYER_TEMPLATE_CONFIG_SAVE_SUCCESS = "图层模版配置保存成功！";
    public static final String OUERY_LAYER_TEMPLATE_DETAIL_ERROR = "查询图层模版配置详情异常!";
    public static final String ALARM_TRANSFER_CONFIG_SAVE_SUCCESS = "告警转移配置信息保存成功！";
    public static final String ALARM_TRANSFER_CONFIG_SAVE_FAIL = "告警转移配置信息保存失败！";
    public static final String OUERY_ALARM_TRANSFER_CONFIG_ERROR = "查询告警转移配置异常!";
    public static final String ALARM_TRANSFER_CONFIG_MOD_SUCCESS = "该告警转移配置已修改保存成功！";
    public static final String OUERY_DEFAULT_FLOW_VIEW_ERROR = "查询默认流程视图模板异常!";
    public static final String CHECK_FLOW_TYPE = "流程类型不能为空！";
    public static final String CHECK_ALARM_TYPE = "告警类型不能为空！";
    public static final String CHECK_APPVER_CODE = "行业应用版本不能为空！";
    public static final String CHECK_INDUSTRY_CODE = "行业编码不能为空！";
    public static final String CHECK_PLATFORM_CODE = "节点编码不能为空！";
    public static final String ORDER_INDUSTRY_ALARM_FLOW_CONFIG = "订购_";
    public static final String ALARM_FLOW_CONFIG_QUERY_SUCCESS = "查询告警流程对应的告警工单异常";
    public static final String ALARM_FLOW_CONFIG_SOUND_SUCCESS = "查询告警流程对应的告警声音异常";

    public static final String ZERO = "0";
    public static final int ZERO_NUM = 0;
    public static final String TYPE_P = "P";
    public static final String TYPE_T = "T";

    public static final String TYPE_Z = "Z";
    public static final String MODELID_DAHUA = "dahua";
    public static final String MODELID_DHUA = "dhua";
    public static final String MODELID_HIKVISION = "hikvision";
    public static final String MODELID_HIKVISION_ANG = "360";
    public static final int RADIX = 16;

    public static final int T_JUST_90 = 90;
    public static final int T_LOST_90 = -90;

    public static final int ONE_NUM = 1;
    public static final int TWO_NUM = 2;

    public static final int THREE_NUM = 3;
    public static final int FOUR_NUM = 4;
    public static final int FIVE_NUM = 5;
    public static final int SIX_NUM = 6;
    public static final int SEVEN_NUM = 7;

    public static final String ZERO_ZERO_STRING = "0.00";

    public static final String PARAM_POLYGON = "POLYGON";

    public static final int TEN_NUM = 10;

    public static final int ONE_HUNDRED = TEN_NUM * TEN_NUM;

    public static final int ONE_THOUSAND = TEN_NUM * ONE_HUNDRED;

    public static final int ELEVEN_NUM = 11;
    public static final int THIRTEEN_NUM = 13;

    public static final String JUDGE_TYPE_COMMON = "0";
    public static final String JUDGE_TYPE_SERIAL = "1";
    public static final String JUDGE_TYPE_PARALLEL = "2";
    public static final String SYS_DEVICE_HORIZONTAL  = "sys_device_horizontal";

    public static final String YEAR_STRING = "year";
    public static final String MONTH_STRING = "month";
    public static final String DAY_STRING = "day";

    public static final String ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS = "查询告警流程配置异常";

    public static final String PROCESS_ID_CANNOT_BE_EMPTY = "流程ID不能为空";

    public static final String EVENT_SOURCE_CANNOT_BE_EMPTY = "事件来源不能为空";
    public static final String PRIMARY_KEY_CANNOT_BE_EMPTY = "主键不能为空";
    public static final String QUERY_WAS_SUCCESSFUL = "查询成功";

    public static final String SOUND_INFORMATION_DOES_NOT_EXIST = "该声音信息不存在";

    public static final String ALARM_FLOW_ID_CANNOT_BE_EMPTY = "告警流程ID不能为空";
    public static final String ALARM_FLOW_ID_DOES_NOT_EXIST = "该告警流程配置不存在";
    public static final String ALARM_FLOW_LINK_ATTR_NOT_EXIST = "没有查询到对应的流程环节";

    public static final String ERROR_CODE_LNGLAT1001 = "1001";
    public static final String ERROR_CODE_LNGLAT1002 = "1002";
    public static final String ERROR_CODE_LNGLAT1003 = "1003";

    public static final String IOT_ALARM_CONFIG_NAME_CANNOT_BE_EMPTY = "物联告警配置名称不能为空";
    public static final String IOT_ALARM_CONFIG_METHOD_CANNOT_BE_EMPTY = "物联告警配置方式不能为空";
    public static final String IOT_ALARM_FACTOR_CANNOT_BE_EMPTY = "物联因子不能为空";
    public static final String IOT_ALARM_DEVICE_CANNOT_BE_EMPTY = "物联设备不能为空";
    public static final String INSERT_IOT_ALARM_CONFIG_ERROR = "新增物联告警配置出错";
    public static final String INSERT_IOT_ALARM_FACTOR_ERROR = "新增物联告警因子出错";
    public static final String INSERT_IOT_ALARM_DEVICE_ERROR = "新增物联告警设备出错";

    /**
     * 配置方式 物联因子
     */
    public static final String IOT_ALARM_CONFIG_CONFIG_METHOD_FACTOR = "0";

    /**
     * 配置方式 物联因子设备
     */
    public static final String IOT_ALARM_CONFIG_CONFIG_METHOD_EQUIPMENT = "1";

    public static final String OPERATOR_LESS = "<";
    public static final String OPERATOR_LE = "≤";


    public static final String IOT_ALARM_CONFIG_ID_CANNOT_BE_EMPTY = "物联告警配置ID不能为空";
    public static final String IOT_ALARM_CONFIG_IS_EMPTY = "物联告警配置不存在";

    public static final String IOT_ALARM_BODY = "物联设备告警";

    public static final String VM_FLOW_LINK_ATTR = "vm_flow_link_attr";
    public static final String VM_FLOW_LINK_INDUSTRY_CONFIG = "vm_flow_link_industry_config";

    public static final String  PARAM_ERROR_KEY = "error";
    public static final String  PARAM_RESPONSEJSON = "responseJson";
    public static final String  PARAM_EXPENDTIME= "expendTime";
    public static final String  PARAM_FLOW= "flow";
    public static final String  PARAM_FLOW_NODE_TIME= "flowNodeTime";
    public static final String  PARAM_FLOW_NODE_TIME_DB= "flow_node_time";
    public static final String  PARAM_BEFORE_DISPATCH_FLAG= "beforeDispatchFlag";
    public static final String  PARAM_PROCESS_ID_LIST= "processIdList";
    public static final String  PARAM_PROCESS_NAME= "processName";
    public static final String  PARAM_ROLE_ID= "roleId";
    public static final String  PARAM_USER_TYPE= "userType";
    public static final String  PARAM_MSG_NOTIFY_TYPE= "msgNotifyType";
    public static final String  PARAM_NOTIFY_ADMIN_TYPE= "notifyAdminType";
    public static final String  PARAM_ALARM_TYPE= "alarmType";
    public static final String  PARAM_ALARM_TYPE_ID= "alarmTypeId";
    public static final String  PARAM_ALARM_SOURCE= "alarmSource";
    public static final String  PARAM_APP_VER_CODE= "appVerCode";
    public static final String  PARAM_VIDEO_LIST= "videoList";
    public static final String  PARAM_ATTR_LIST= "attrList";
    public static final String  PARAM_SRC= "src";
    public static final String  USER_NAME= "user_name";
    public static final String  USERNAME= "userName";
    public static final String  NICKNAME= "nickName";
    public static final String  PARAM_APPLY_USER_NAME= "applyUserName";
    public static final String  PARAM_APPLY_USER_PHONE= "applyUserPhone";
    public static final String  PARAM_APPLY_USER_ORG_NAME= "applyUserOrgName";
    public static final String  ORG_NAME= "org_name";
    public static final String  PARAM_APPLY_USER_ID= "applyUserId";
    public static final String  MOBILE_PHONE= "mobile_phone";
    public static final String  PARAM_LEADER_ID= "leaderId";
    public static final String  PARAM_LEADER_NAME= "leaderName";
    public static final String  PARAM_LEADER_PHONE= "leaderPhone";
    public static final String  PARAM_LEADER_ORG_NAME= "leaderOrgName";
    public static final String  PARAM_WARING_NAME= "warningName";
    public static final String  PARAM_EVENT_NUM= "eventNum";
    public static final String  PARAM_LEANDER_NUM= "leanderNum";
    public static final String  PARAM_PROCESS_ORDER_ID= "processOrderId";
    public static final String  PARAM_IMAGE= "image";
    public static final String  PARAM_APPROVE_ORDER_ID= "approveOrderId";
    public static final String  PARAM_STATUS= "status";
    public static final String  PARAM_IF_CURRENT_LINK= "ifCurrentLink";
    public static final String  PARAM_LEADERS= "leaders";
    public static final String  PARAM_ORDER_FLOW_ID= "order_flow_id";
    public static final String  PARAM_ORDER_FILE_LIST= "orderFileList";
    public static final String  PARAM_HANDLE_PROCESS_ID= "handleProcessId";
    public static final String  PARAM_HANDLE_PROCESS_NAME= "handleProcessName";
    public static final String  DISPATCH_PROCESS_NAME= "dispatchProcessName";
    public static final String  NEXT_PROCESS_NAME= "nextLinkProcessName";
    public static final String  FLOW_LINK_TYPE= "flowLinkType";
    public static final String  PARAM_ORDER_FLOW_ID_LIST= "orderFlowIdList";
    public static final String  PARAM_APPROVE_ID= "approveId";
    public static final String  PARAM_MAIN_ALARM_ID= "mainAlarmId";
    public static final String  PARAM_ALARM_FLAG= "alarmFlag";
    public static final String  PARAM_PROCESS_TABLE_NAME= "processTableName";
    public static final String  PARAM_PROVINCE_CODE= "provinceCode";
    public static final String  PARAM_DISTRICT_CODE= "adcode";
    public static final String  PARAM_TOWN_CODE= "towncode";
    public static final String  PARAM_PROVINCE= "province";
    public static final String  PARAM_NEXT_LINK_ID= "nextLinkId";
    public static final String  PARAM_CITY= "city";
    public static final String  PARAM_DISTRICT= "district";
    public static final String  PARAM_TOWN= "township";
    public static final String  PARAM_ALARM_TYPE_LIST= "alarmTypeList";
    public static final String  PARAM_WARNING_ORDER_ID_LIST= "warningOrderIdList";
    public static final String  PARAM_WARNING_ORDER_TABLE= "warningOrderTable";
    public static final String  PARAM_RESOURCE_URL= "resourceUrl";
    public static final String  PARAM_FILE_NAME= "fileName";
    public static final String  PARAM_REALLINK_TYPE= "realLinkType";
    public static final String  PARAM_FLOWEND_TIME= "flowEndTime";
    public static final String  PARAM_REMARK= "remark";
    public static final String  PARAM_NEXT_LINK_PROCESSNAME= "nextLinkProcessName";
    public static final String  PARAM_NEXT_LINK_NAME= "nextLinkName";
    public static final String  PARAM_POLYGON_NAME = "polygonName";
    public static final String  PARAM_LAYER_NAME = "layerName";
    public static final String  PARAM_PLAN_TASK_ID = "planTaskId";
    public static final String  KEY = "key";
    public static final String  HASHED = "hashed";

    public static final String  USER_TYPE1= "1";
    public static final String  USER_TYPE2= "2";


    public static final String TRUE_WARING = "真警";
    public static final String FALSE_WARING = "误报";
    public static final String REPEAT_WARING = "重复告警";
    public static final String SECOND_JUDGE_RESULT_3 = "算法未分析";

    public static final int ALARM_TAG_MAX_NUM = 20;

    public static final String RADAR_CODES = "radarCodes";

    public static final String PARAM_TOKEN = "access_token";
    public static final String PARAM_USER_ZFZH = "enforcement";
    public static final String PARAM_USER_ADOPT  = "adopt";
    public static final String PARAM_USER_LOGIN  = "login";
    public static final String ENFORCEMENT_ADDRESS  = "enforcement_address";
    public static final String ENFORCEMENT_CLIENT_ID = "enforcement_client_id";
    public static final String ENFORCEMENT_CLIENT_SECRET  = "enforcement_client_secret";

    public static final String WARNING_ORDER_REDIS_MAX  = "warning_order_redis_max";
    public static final String WARNING_ORDER_REDIS_NUM  = "warning_order_redis_num";
    public static final String WARNING_ORDER_NUMBER  = "warning_order_number";

    public static final double ZERO_FIVE_NUM = 0.5;
    public static final int NINE_NUM = 9;
    public static final int EIGHT_NUM = 8;

    public static final String APP_URL  = "appUrl";
    public static final String AUDIT_ID  = "auditId";

    public static final String ONE_BLANK = "[]";
    public static final String TWO_BLANK = "[{}]";

    public static final String ALARM_LABLE_LIST = "alarmLableRelDTOList";

    public static final String ALARM_SIGNATURE = "signature";
    public static final String ALARM_USER = "username";

    public static final String OPER_TYPE_MOD = "mod";
    public static final String OPER_TYPE_DEL = "del";
    public static final String OPER_TYPE_QRU = "query";

    public static final String ALARM_COUNT = "count";

    public static final String SOUND_OPERATION = "operation";
    public static final String ALARM_FLOW_USE = "流程正在流转中";
    public static final String PROCESS_CONFIGURATION= "用户已关联流程模板";
    public static final String CLOSE_ALARM= "关闭告警";

    public static final String ALARM_SOUND = "告警声音id不可为空";
    public static final String NOT_ASSOCIATED = "未关联流程";
    public static final String EIGHTEEN = "18";
    public static final int ALARM_FLOW_USE_STATUS = 1002;
    public static final int PROCESS_CONFIGURATION_STATUS = 1001;

    public static final String SETNAME_NOT_SPECIFIED = "未指定操作的集合名";

    //导出
    public static final String TYPE_VALUE = "typeId";
    public static final String ASSIGNED_DATE = "assignedDate";
    public static final String REAL_NUM = "realNum";
    public static final String FALSE_NUM = "falseNum";
    public static final String JUDGR_NUM = "judgeNum";
    public static final String DISPATCH_NUM = "dispatchNum";
    public static final String HANDLE_NUM = "handleNum";
    public static final String APPROVE_NUM = "approveNum";
    public static final String ORDERCOMPLETE_NUM = "orderCompleteNum";
    public static final String ALL_NUM = "allNum";
    public static final String AVG_NUM = "avgNum";
    public static final String AVG_TIME_DAY = "avgTimeDay";
    public static final String REPEATCOMPLETE_NUM = "repeatCompleteNum";
    public static final String PROCESS_NUM  = "processNum";
    public static final String ACCEPTANCE_NUM = "acceptanceNum";
    public static final String ACCURACY = "accuracy";
    public static final String COMPLETION_RATE = "completionRate";
    public static final String COMPLETION_RATES = "completionRates";
    public static final String SUPPRESS_NUM = "suppressNum";
    public static final String USER_TOTAL_NUM = "userTotalNum";
    public static final String START_USER_NUM = "startUserNum";
    public static final String STOP_USER_NUM = "stopUserNum";
    public static final String LOGIN_TOTAL_COUNT = "loginTotalCount";
    public static final String LOGIN_COUNT = "loginCount";
    public static final String LOGIN_DATE = "loginDate";
    public static final String LOGIN_TYPE_NAME = "loginTypeName";
    public static final String LOGIN_DURATION = "loginDuration";
    public static final String ONLINE_TIME = "onlineTime";
    public static final String TENANT_ID = "tenantId";
    public static final String TENANT_NAME = "tenantName";
    public static final String LOGIN_USER_NAME = "loginUserName";
    public static final String LOGIN_NAME = "loginName";
    public static final String LOGIN_TIME_SLOT = "loginTimeSlot";
    public static final String PC_OS_NAME = "pcOsName";
    public static final String BROWSER_NAME = "browserName";
    public static final String APP_OS_NAME = "appOsName";
    public static final String VERSION_NAME = "versionName";
    public static final String PHONE_MODEL_NAME = "phoneModelName";
    public static final String LOGIN_STATUS = "loginStatus";
    public static final String SUCCESS_RATE = "successRate";
    public static final String LOGIN_CAUSE = "loginCause";
    public static final String FAIL_RATIO = "failRatio";
    public static final String FAIL_CAUSE_NUM = "failCauseNum";
    public static final String REPEAT_NAME = "重复告警数量";
    public static final String PROCESS_NAME = "处理中数量";
    public static final String DISPATCH_NAME = "待调度数量";
    public static final String ACCEPTANCE_NAME = "待受理数量";
    public static final String JUDGE_NAME = "待研判数量";
    public static final String APPROVE_NAME = "待核实数量";
    public static final String HANDLE_NAME = "待处置数量";
    public static final String COMPLETE_NAME = "已完结数量";
    public static final String COMPLETE_AVG_TIME_NAME = "完结事件平均处理时长";
    public static final String SUPPRESS_NAME = "告警压制数量";
    public static final String DEVICE_CODE_NAME = "摄像机编号";
    public static final String DEVICE_NAME = "摄像机名称";
    public static final String GRID_NAME_Z = "网格名称";
    public static final String INSPECT_TASK_NAME = "巡查任务数量";
    public static final String COMPLETED_NUM_NAME = "已完成数量";
    public static final String INCOMPLETE_NUM_NAME = "待完成数量";
    public static final String CHANNEL_ONLINE_RATE_NAME = "通道在线率";
    public static final String CHANNEL_ONLINE_NUM_NAME = "通道在线数量";
    public static final String CHANNEL_OFFLINE_NUM_NAME = "通道离线数量";
    public static final String[] EVENT_SHEET1 = {"告警数量", "真警数量", REPEAT_NAME, "误报数量", "准确率", PROCESS_NAME,
            ACCEPTANCE_NAME, JUDGE_NAME, DISPATCH_NAME, HANDLE_NAME, APPROVE_NAME, COMPLETE_NAME, "完结率", SUPPRESS_NAME};
    public static final String[] EVENT_SHEET2 = {"日期", "告警事件数量", SUPPRESS_NAME};
    public static final String[] EVENT_SHEET3 = {"告警类型名称", "告警数量", "占比"};
    /** 下游任务排行表头 */
    public static final String[] EVENT_SHEET_SUB_SCENE = {"下游任务名称", "告警数量", "占比"};
    public static final String[] EVENT_SHEET4 = {"告警来源名称", "告警数量", "占比"};
    public static final String[] EVENT_SHEET5 = {"告警区域名称", "告警数量", "占比"};
    public static final String[] EVENT_SHEET6 = {"告警时间段", "告警事件数量", SUPPRESS_NAME};
    public static final String[] EVENT_SHEET7 = {DEVICE_CODE_NAME,DEVICE_NAME,"告警数量","日均告警数量", "真警数量", REPEAT_NAME, "误报数量", "准确率","完结率", PROCESS_NAME,
            ACCEPTANCE_NAME, JUDGE_NAME, DISPATCH_NAME, HANDLE_NAME, APPROVE_NAME, COMPLETE_NAME, SUPPRESS_NAME};
    public static final String[] EVENT_SHEET8 = {"真警数量","真警数量占比","误报数量","误报数量占比", REPEAT_NAME, "重复告警数量占比", "准确率"};
    public static final String[] EVENT_SHEET9 = {"告警区域名称","准确率"};
    public static final String[] EVENT_SHEET10 ={"告警来源名称","准确率"};
    public static final String[] EVENT_SHEET11 ={"告警类型名称","准确率"};
    /** 下游任务准确率排行表头 */
    public static final String[] EVENT_SHEET_SUB_SCENE_ACCURACY = {"下游任务名称", "准确率"};
    public static final String[] EVENT_SHEET12 ={"告警时间段","准确率"};
    public static final String[] EVENT_SHEET13 = {"行政区域","告警数量","完结率",PROCESS_NAME,ACCEPTANCE_NAME, JUDGE_NAME, DISPATCH_NAME, HANDLE_NAME, APPROVE_NAME, COMPLETE_NAME, COMPLETE_AVG_TIME_NAME};
    public static final String[] EVENT_SHEET14 = {"组织名称","告警数量","完结率",PROCESS_NAME,ACCEPTANCE_NAME, JUDGE_NAME, DISPATCH_NAME, HANDLE_NAME, APPROVE_NAME, COMPLETE_NAME, COMPLETE_AVG_TIME_NAME};
    public static final String[] EVENT_SHEET15 = {"物联设备编号","物联设备名称","告警数量","日均告警数量", "真警数量", REPEAT_NAME, "误报数量", "准确率","完结率", PROCESS_NAME,
            ACCEPTANCE_NAME, JUDGE_NAME, DISPATCH_NAME, HANDLE_NAME, APPROVE_NAME, COMPLETE_NAME};
    public static final String[] EVENT_SHEET16 = {GRID_NAME_Z,"告警数量","完结率" ,PROCESS_NAME,
            ACCEPTANCE_NAME, JUDGE_NAME, DISPATCH_NAME, HANDLE_NAME, APPROVE_NAME,COMPLETE_NAME ,COMPLETE_AVG_TIME_NAME};
    public static final String[] DEVICE_SHEET1 = {"摄像机数量", "摄像机在线数量", "摄像机离线数量", "摄像机在线率", "通道数量", CHANNEL_ONLINE_NUM_NAME, CHANNEL_OFFLINE_NUM_NAME, CHANNEL_ONLINE_RATE_NAME, "通道配置算法任务数量", "通道启用算法任务数量"};
    public static final String[] DEVICE_SHEET2 = {"区域名称", "摄像机数量"};
    public static final String[] DEVICE_SHEET3 = {DEVICE_CODE_NAME, DEVICE_NAME, "摄像机订购的算法数量"};
    public static final String[] DEVICE_SHEET4 = {DEVICE_CODE_NAME, DEVICE_NAME, "通道编号", "通道名称", "通道配置的前端算法任务数量", "通道配置的后端算法任务数量", "通道启用的前端算法任务数量", "通道启用的后端算法任务数量"};
    public static final String[] DEVICE_SHEET5 = {"物联设备数量", "物联设备在线数量", "物联设备离线数量", "物联设备在线率"};
    public static final String[] DEVICE_SHEET6 = {"区域名称", "物联设备数量"};
    public static final String[] DEVICE_SHEET7 = {"无人机数量", "无人机在线数量","无人机离线数量","无人机在线率","机场数量","机场在线数量","机场离线数量","机场在线率"};
    public static final String[] DEVICE_SHEET8 = {"区域名称", "无人机数量"};
    public static final String[] DEVICE_SHEET9 = {"雷达数量", "雷达在线数量","雷达离线数量","雷达在线率"};
    public static final String[] DEVICE_SHEET10 = {"区域名称", "雷达数量"};
    public static final String[] DEVICE_SHEET11 = {"喇叭数量", "喇叭工作中数量","喇叭离线数量","喇叭空闲数量","喇叭故障数量","喇叭从未连接数量","喇叭在线率"};
    public static final String[] DEVICE_SHEET12 = {"区域名称", "喇叭数量"};
    public static final String[] DEVICE_SHEET13 = {"执法记录仪数量", "执法记录仪在线数量", "执法记录仪离线数量","执法记录仪从未连接数量", "执法记录仪在线率", "通道数量", CHANNEL_ONLINE_NUM_NAME, CHANNEL_OFFLINE_NUM_NAME, CHANNEL_ONLINE_RATE_NAME};
    public static final String[] DEVICE_SHEET14 = {"区域名称", "执法记录仪数量"};
    public static final String[] USER_SHEET1 = {"用户总数", "启用用户数","停用用户数","组织数量"};
    public static final String[] USER_SHEET2 = {"日期", "登录次数总数","PC端登录次数","APP登录次数","小程序/公众号登录次数"};
    public static final String[] USER_SHEET3 = {"用户账号", "用户姓名","登录次数"};
    public static final String[] USER_SHEET4 = {"时间段", "登录次数"};
    public static final String[] USER_SHEET5 = {"PC操作系统名称", "登录次数"};
    public static final String[] USER_SHEET6 = {"浏览器名称", "登录次数"};
    public static final String[] USER_SHEET7 = {"APP操作系统名称", "登录次数"};
    public static final String[] USER_SHEET8 = {"手机型号", "登录次数"};
    public static final String[] USER_SHEET9 = {"APP版本", "登录次数"};
    public static final String[] USER_SHEET10 = {"登录成功数量", "登录失败数量","成功率"};
    public static final String[] USER_SHEET11 = {"用户登录失败原因", "数量","占比"};
    public static final String[] USER_SHEET12 = {"组织名称", "用户数量"};
    public static final String[] USER_SHEET13 = {"用户账号", "用户姓名","登录时长"};
    public static final String[] INSPECT_SHEET1 = {"网格数量", "已标绘数量", "未标绘数量", "网格员数量", "网格长数量", "普通网格员数量",
            INSPECT_TASK_NAME, COMPLETED_NUM_NAME, INCOMPLETE_NUM_NAME, "出勤率", "正常人数", "异常人数"};
    public static final String[] INSPECT_SHEET2 = {"网格层级", "数量"};
    public static final String[] INSPECT_SHEET3 = {"性别", "数量","占比"};
    public static final String[] INSPECT_SHEET4 = {"年龄段", "数量","占比"};
    public static final String[] INSPECT_SHEET5 = {"网格层级", GRID_NAME_Z,"标绘状态","网格长姓名","网格员数量"};
    public static final String[] INSPECT_SHEET6 = {GRID_NAME_Z, "出勤率"};
    public static final String[] INSPECT_SHEET7 = {"网格长姓名", "出勤率"};
    public static final String[] INSPECT_SHEET8 = {"普通网格员姓名", "出勤率"};
    public static final String[] INSPECT_SHEET9 = {"网格层级", GRID_NAME_Z, "出勤率", "正常签到次数", "迟到次数", "未签到次数", "正常签退次数", "早退次数", "未签退次数"};
    public static final String[] INSPECT_SHEET10 = {"网格层级", GRID_NAME_Z, "网格员姓名","账号","出勤率", "正常签到次数", "迟到次数", "未签到次数", "正常签退次数", "早退次数", "未签退次数"};
    public static final String[] INSPECT_SHEET11 = {GRID_NAME_Z, INSPECT_TASK_NAME,COMPLETED_NUM_NAME,INCOMPLETE_NUM_NAME};
    public static final String[] INSPECT_SHEET12 = {"反馈端", "数量","占比"};
    public static final String[] INSPECT_SHEET13 = {"时间段", "巡查反馈数量"};
    public static final String[] INSPECT_SHEET14 = {"日期", INSPECT_TASK_NAME, COMPLETED_NUM_NAME, INCOMPLETE_NUM_NAME};
    public static final String[] INSPECT_SHEET15 = {"巡查人姓名", INSPECT_TASK_NAME, COMPLETED_NUM_NAME, INCOMPLETE_NUM_NAME,"完成率"};
    public static final String[] INSPECT_SHEET16 = {GRID_NAME_Z, "网格任务完成率"};
    public static final String SOURCE_NAME = "sourceName";
    public static final String PARAM_TENANT_NAME = "tenant_name";
    public static final String DEPT_NAME = "deptName";
    public static final String YY_MM_DD = "yyyyMMdd";
    public static final String YY_MM = "yyyyMM";
    public static final String $_MATCH= "$match";
    public static final String $_REGEX= "$regex";
    public static final String $_GROUP= "$group";
    public static final String $_PROVINCE_CODE= "$province_code";
    public static final String $_CITY_CODE= "$city_code";
    public static final String $_TENANT_ID= "$tenant_id";
    public static final String $_COUNTY_CODE= "$county_code";
    public static final String $_TOWN_CODE= "$town_code";
    public static final String $_DEVICE_CODE= "$device_code";
    public static final String $_ALGORITHM_CODE= "$algorithm_code";
    public static final String $_INDUSTRY_CODE= "$industry_code";
    public static final String $_WARNING_SOURCE= "$warning_source";
    public static final String $_SUBSTR= "$substr";
    public static final String $_COND= "$cond";
    public static final String $_OPERATE_TYPE= "$operate_type";
    public static final String $_ALARM_TIME= "$alarm_time";
    public static final String $_PROJECT= "$project";
    public static final String $_ID_TENANT_ID= "$_id.tenant_id";
    public static final String $_ID_PROVINCE_CODE= "$_id.province_code";
    public static final String $_ID_CITY_CODE= "$_id.city_code";
    public static final String $_ID_COUNTY_CODE= "$_id.county_code";
    public static final String $_ID_TOWN_CODE= "$_id.town_code";
    public static final String $_ID_DEVICE_CODE= "$_id.device_code";
    public static final String $_ID_INDUSTRY_CODE= "$_id.industry_code";
    public static final String $_ID_WARNING_SOURCE= "$_id.warning_source";
    public static final String $_ID_HOUR_STR= "$_id.hour_str";
    public static final String $_ID_YEAR_STR= "$_id.year_str";
    public static final String $_ID_MONTH_STR= "$_id.month_str";
    public static final String $_ID_DAY_STR= "$_id.day_str";
    public static final String $_EVENT_NUM= "$event_num";
    public static final String $_WARNING_TYPE_ID= "$warning_type_id";
    public static final String $_PROVINCE_ID= "$province_id";
    public static final String $_TENANT_NAME= "$tenant_name";
    public static final String $_JUDGE_RESULT= "$judge_result";
    public static final String $_CITY_ID= "$city_id";
    public static final String $_COUNTY_ID= "$county_id";
    public static final String HANDLE_DATE_TIME= "handleDateTime";
    public static final String SERIAL_ALARM_NUM= "serial_alarm_num";
    public static final String PARALLEL_ALARM_NUM= "parallel_alarm_num";
    public static final String SERIAL_JUDGE_NUM= "serial_judge_num";
    public static final String PARALLEL_JUDGE_NUM= "parallel_judge_num";
    public static final String SERIAL_REAL_ALARM_NUM= "serial_real_alarm_num";
    public static final String PARALLEL_REAL_ALARM_NUM= "parallel_real_alarm_num";
    public static final String SERIAL_FALSE_ALARM_NUM= "serial_false_alarm_num";
    public static final String PARALLEL_FALSE_ALARM_NUM= "parallel_false_alarm_num";
    public static final String YEARSTR= "yearStr";
    public static final String DAYSTR= "dayStr";
    public static final String MONTHSTR= "monthStr";
    public static final String JUDGE_TIME= "judge_time";
    public static final String DATE_TIME= "dateTime";
    public static final String CREATE_DATE = "createDate";
    public static final String HOUR_STR = "hour_str";
    public static final String YEAR_STR = "year_str";
    public static final String MONTH_STR = "month_str";
    public static final String DAY_STR = "day_str";
    public static final String EVENT_NUM = "event_num";
    public static final String PROVINCE_ID = "provinceId";
    public static final String COUNTY_ID = "countyId";
    public static final String AI_NUM = "aiNum";
    public static final String LESS_ONE_HOUR = "lessOneHour";
    public static final String ONE_FIVE_HOUR = "oneFiveHour";
    public static final String FIVE_THE_HOUR = "fiveTenHour";
    public static final String OVER_THE_HOUR = "overTenHour";
    public static final String AVG_TIME = "avgTime";
    public static final String ONE_STRING = "1";
    public static final String EVENT_DATE = "eventDate";
    public static final String AAA_SIX= "aaa666";
    public static final String OTHER= "其他";
    public static final String TAG_ID = "tagId";
    public static final String TAG_NAME = "tagName";
    public static final String CHECK_EXPORT_FAIL = "导出数据异常";
    public static final String COLON = ":";
    public static final String SEP = "-";
    public static final String PER_CENT = "%";
    public static final String INSPECTION_TASK_NUM = "inspectionTaskNum";
    public static final String TASK_COMPLETED_NUM = "taskCompletedNum";
    public static final String TASK_INCOMPLETE_NUM = "taskIncompleteNum";
    public static final String ATTENDANCE_RATE = "attendanceRate";
    public static final String TASK_COMPLETED_RATE = "taskCompletedRate";
    public static final String GRID_LEVEL_NAME = "gridLevelName";
    public static final String GRID_NAME = "gridName";
    public static final String GRID_NUM = "gridNum";
    public static final String GRID_KEEPER_NUM = "gridKeeperNum";
    public static final String UNKNOWN_NUM = "unknownNum";
    public static final String UNKNOWN_RATE = "unknownRate";
    public static final String GRID_LEVEL = "gridLevel";
    public static final String IN_NUM = "inNum";
    public static final String ATTENDANCE_RATES = "attendanceRates";
    public static final String OUT_NUM = "outNum";
    public static final String INSPECTION_TASK_BACK_NUM = "inspectionTaskBackNum";
    public static final String COMPLETED_RATE = "completedRate";
    public static final String GENDER = "gender";
    public static final String KEEPER_NAME = "keeperName";
    public static final String DEVICE_LABEL = "deviceLabel";
    public static final String USER_TABLE1 = "表1 用户登录按时间统计";
    public static final String USER_TABLE2 = "表2 用户登录主要数据";
    public static final String USER_TABLE3 = "表3 用户登录时长排行（PC）";
    public static final String USER_TABLE4 = "表4 用户登录时长排行（APP）";
    public static final String USER_TABLE5 = "表5 用户登录时长排行（小程序）";
    public static final String USER_TABLE6 = "表6 用户登录24小时分布";
    public static final String USER_TABLE7 = "表7 PC操作系统排行";
    public static final String USER_TABLE8 = "表8 PC浏览器排行";
    public static final String USER_TABLE9 = "表9 APP操作系统排行";
    public static final String USER_TABLE10 = "表10 APP手机型号排行";
    public static final String USER_TABLE11 = "表11 APP版本排行";
    public static final String USER_TABLE12 = "表12 用户数量按组织排行";

    public static final String DEPT_ID = "deptId";
    public static final String PARENT_ID = "parentId";

    //运营研判表索引列
    public static final int INDEXASC_ONE = 1;
    public static final String INDEXFIELD_WARNING_ORDER_ID = "warning_order_id";
    public static final String INDEXFIELD_DUPLICATE_ORDER_ID = "duplicate_order_id";
    public static final String INDEXFIELD_TENANT_ID = "tenant_id";
    public static final String INDEXFIELD_ALARM_TIME = "alarm_time";
    public static final String INDEXFIELD_WARNING_TYPE_ID = "warning_type_id";
    public static final String INDEXFIELD_PROVINCE_ID = "province_id";
    public static final String INDEXFIELD_CITY_ID = "city_id";
    public static final String INDEXFIELD_COUNTY_ID = "county_id";
    public static final String INDEXFIELD_CREATE_USER = "create_user";
    public static final String INDEXFIELD_EMERGENCY_LEVEL = "emergency_level";
    public static final String INDEXFIELD_CREATE_TIME = "create_time";
    public static final String INDEX_TIME_RESULT = "alarm_time,judge_result";
    public static final String INDEX_TIME_RESULT_SOURCE = "alarm_time,is_second_judge,warning_source";
    public static final String INDEX_TIME_DEVICE_RESULT = "alarm_time,device_code,judge_result";
    public static final String INDEX_TIME_TENANTID_RESULT = "alarm_time,tenant_id,operate_type,judge_result";
    public static final String INDEX_TIME_PROVINCEID_CITYID_COUNTYID_RESULT = "alarm_time,province_id,city_id,county_id,judge_result";
    public static final String INDEX_DEL_ALARMTIME_TYPE = "alarm_time,operate_type,judge_result";
    public static final String INDEX_DEL_TIME_RESULT = "create_time,alarm_time,operate_type,judge_result";
    //告警拦截表索引列
    public static final String INDEXFIELD_TASK_ID = "task_id";
    public static final String INDEXFIELD_CITY_CODE = "city_code";
    public static final String INDEXFIELD_COUNTY_CODE = "county_code";
    public static final String INDEXFIELD_ALGORITHM_CODE = "algorithm_code";
    public static final String INDEXFIELD_PROVINCE_CODE = "province_code";
    public static final String INDEXFIELD_DEVICE_CODE = "device_code";
    public static final String INDEXFIELD_ALARM_BODY = "alarm_body";
    public static final String INDEXFIELD_PROVINCE_CITY = "province_code,city_code";
    public static final String INDEXFIELD_LONGITUDE_LATITUDE = "longitude,latitude";
    public static final String INDEX_TIME_TYPE_RESULT = "alarm_time,warning_type_id,judge_result";
    public static final String INDEX_TENANTID_TIME_FLAG_RESULT = "tenant_id,alarm_time,delete_flag,judge_result,industry_code";
    public static final String INDEX_TIME_FLAG_RESULT = "alarm_time,delete_flag,judge_result,importance_level";
    public static final String INDEX_TIME_PROVINCE_CITY_COUNTY_RESULT = "alarm_time,province_code,city_code,county_code,judge_result";
    public static final String INDEX_CONFIDENCE_LEVEL_MAX = "confidence_level_max";
    public static final String INDEX_COMP_CONFIDENCE_LEVEL = "comp_confidence_level";
    public static final String INDEX_INTERCEPT = "intercept";
    public static final String INDEX_ENLARGE_FLAG = "enlarge_flag";
    public static final String IOT_ALARM_CONFIG_CACHE = "iot_alarm_config_cache";
    public static final String IOT_CONFIG_FACTORY_SUM_VALUE = "iot_config_factory_sum_value";
    public static final String IOT_ANTI_SHAKE_SUM_VALUE = "iot_anti_shake_sum_value";
    public static final String SECOND_RECOG_TASK_INFO = "second_recog_task_info";
    public static final String AIR_TASK_AL_SYNC_LOG = "uav_al_task_log";
    public static final String UAV_AIR_POINT_TASK = "uav_air_point_task";
    public static final String UAV_FLIGHT_FENCE_INFO = "uav_flight_fence_info";

    public static final String UAV_SYNC_UFNC = "uav_ufnc_info";
    public static final String UFCP_GAS_INFO = "uav_gas_info";
    public static final String UAV_SYNC_STATUS = "uav_sync_status";

    public static final String UAV_SYNC_UFNC_TASK = "uav_ufnc_info_task";
    public static final String  ANTI_SHAKE_REDIS_KEY= "anti_shake";
    public static final String  IOT_DEVICE_GROUPS_REDIS_KEY= "iot_device_groups";
    public static final String  IOT_ALARM_SUPPRESS_REDIS_KEY= "iot_alarm_suppress";
    public static final String MERGE_TIME = "mergeTime";
    public static final String SECOND_RECOG_SEND_DICT = "second_recog_send_url";
    public static final String CAPTURE_VIDEOS_SWITCH = "capture_videos_switch";
    public static final String VIDEO_DOWN_URL = "video_down_url";
    public static final String STOP_VIDEO_DOWN_URL = "stop_video_down_url";
    public static final String FILE_ERROR = "文件服务错误";

    public static final String OPERATION_DEPT = "operation_dept";
    public static final String OPERATION_NODE = "operation_node";
    public static final String PARAM_OPERATION_DEPT = "operationDept";
    public static final String PARAM_OPERATION_NODE = "operationNode";
    public static final String OPERATE_TYPE = "operate_type";
    public static final String ALARM_INFORMATION_LOG = "告警信息：";
    public static final String SECOND_SELECT_CONFIG_LOG = "二次识别查询二次识别配置：";
    public static final String SECOND_TASK_FALSE_LOG = "二次识别任务下发失败:";
    public static final String SECOND_JUDEG_RESULT = "二次识别结果";

    public static final String CRLF = "\r\n";
    public static final String LICENSE_PLATES = "licensePlates";
    public static final String BLACK_WHITE_JSON = "blackWhiteJson";
    //1-AI、4-摄像机告警：事件描述
    public static final String CAR_TYPE = "carType";
    public static final String CAR_NUMBER = "carNumber";
    public static final String PLATE_COLOR = "plateColor";
    public static final String OBJECT_ID = "objectId";
    public static final String OBJECT_COUNT = "objectCount";
    public static final String OBJECT_TIME = "objectTime";
    public static final String ALARM_CAUSE = "alarmCause";
    public static final String VEHICLE_NUMBER = "vehicleNumber";
    public static final String VEHICLE_TYPE = "vehicleType";
    public static final String VEHICLENUM_COLOR = "vehicleNumColor";
    public static final String VEHICLECOLOR = "vehicleColor";
    public static final String CAR_TYPE_COLON = "车辆分类：";
    public static final String VISUAL_PARAMETERS = "视觉参数";
    public static final String CAR_NUMBER_COLON = "车牌号码：";
    public static final String PLATE_COLOR_COLON = "车牌颜色：";
    public static final String OBJECT_ID_COLON = "监测目标ID：";
    public static final String OBJECT_COUNT_COLON = "监测目标数量：";
    public static final String OBJECT_TIME_COLON = "目标停留时长（秒）：";
    public static final String ALARM_CAUSE_COLON = "告警原因：";
    public static final String VEHICLE_NUMBER_COLON = "车牌号：";
    public static final String VEHICLE_TYPE_COLON = "车辆类型：";
    public static final String VEHICLE_NUM_COLOR_COLON = "车牌颜色：";
    public static final String VEHICLE_COLOR_COLON = "车辆颜色：";
    //10-雷达告警：事件描述
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_TYPE = "targetType";
    public static final String TARGET_MMSI = "MMSI";
    public static final String TARGET_NAME = "targetName";
    public static final String SPEED = "speed";
    public static final String COURSE = "course";
    public static final String TARGET_LENGTH = "targetLength";
    public static final String TARGET_ID_COLON = "目标ID：";
    public static final String TARGET_TYPE_COLON = "目标类型：";
    public static final String TARGET_MMSI_COLON = "目标MMSI：";
    public static final String TARGET_NAME_COLON = "目标名称：";
    public static final String SPEED_COLON = "目标速度：";
    public static final String COURSE_COLON = "目标航向：";
    public static final String TARGET_LENGTH_COLON = "目标长度：";

    public static final String WARNING_TYPE_ID_GDWP = "4110101";
    public static final String WARNING_TYPE_ID_KCWP = "4110102";
    public static final String WARNING_TYPE_ID_FNHWP = "4110103";
    public static final String WARNING_TYPE_ID_FLHWP = "4110104";
    public static final String[] REGION_ALARM_LIST = {WARNING_TYPE_ID_GDWP,WARNING_TYPE_ID_KCWP,WARNING_TYPE_ID_FNHWP,WARNING_TYPE_ID_FLHWP};
    public static final String PARAM_CHANGE = "变";
    public static final String SATELLITE = "卫星";

    /**
     * 告警类型，从查询摄像头信息来
     */
    public static final String ALARM_SOURCE_TYPE_CAMERA = "camera";
    public static final String PARAM_ATTR_TYPE = "attrType";
    public static final String PARAM_ATTR_VALUE = "attrValue";

    //导出相关字段信息
    public static final String EXP_TENANDID = "{{tenantId}}";
    public static final String EXP_TENANDNAME = "{{tenantName}}";
    public static final String EXP_ACCEPTNODE = "{{acceptNode}}";
    public static final String EXP_ACCEPTNODENAME = "{{acceptNodeName}}";
    public static final String EXP_INDUSTRYNAME = "{{industryName}}";
    public static final String EXP_APPVERCODE = "{{appVerCode}}";
    public static final String EXP_INTERCEPTNAME = "{{interceptName}}";
    public static final String EXP_ALARMTITLE = "{{alarmTitle}}";
    public static final String EXP_ALARMID = "{{alarmId}}";
    public static final String EXP_ALARMLEVEL = "{{alarmLevel}}";
    public static final String EXP_ALARMTIME = "{{alarmTime}}";
    public static final String EXP_ALARMSOURCE = "{{alarmSource}}";
    public static final String EXP_ALARMTYPE = "{{alarmType}}";
    public static final String EXP_ALARMREASON = "{{alarmReason}}";
    public static final String EXP_ALARMADDRESS = "{{alarmAddress}}";
    public static final String EXP_ALARMLOTANDLAT = "{{alarmLotAndLat}}";
    public static final String EXP_ALARMDISTANCE = "{{alarmDistance}}";
    public static final String EXP_ALARMPITCHANGLE = "{{alarmPitchAngle}}";
    public static final String EXP_ALARMHORANGLE = "{{alarmHorAngle}}";
    public static final String EXP_ALARMGRID = "{{alarmGrid}}";
    public static final String EXP_ALARMGRIDKEPPER = "{{alarmGridKepper}}";
    public static final String EXP_ALARMDESCRIBE = "{{alarmDescribe}}";
    public static final String EXP_ALARMREMARK = "{{alarmRemark}}";
    public static final String EXP_DEVICECODE = "{{deviceCode}}";
    public static final String EXP_DEVICENAME = "{{deviceName}}";
    public static final String EXP_DEVICELABEL = "{{deviceLabel}}";
    public static final String EXP_CHANNELNAME = "{{channelName}}";
    public static final String EXP_DEVICEFACTORY = "{{deviceFactory}}";
    public static final String EXP_DEVICELOTANDLAT = "{{deviceLotAndLat}}";
    public static final String EXP_DEVICEHIGH = "{{deviceHigh}}";
    public static final String EXP_DEVICEADDRESS = "{{deviceAddress}}";
    public static final String EXP_CONFIDENCELEVEL = "{{confidenceLevel}}";
    public static final String EXP_HORIZONTALAZIMUTH = "{{horizontalAzimuth}}";
    public static final String EXP_ISSECONDIDENTIFY = "{{isSecondIdentify}}";
    public static final String EXP_SECONDIDENTIFYRESULT = "{{secondIdentifyResult}}";
    public static final String EXP_LINKAGEALGORITHM = "{{linkageAlgorithm}}";

    public static final String EXP_RELDEVICECODE1 = "{{relDeviceCode1}}";
    public static final String EXP_RELDEVICENAME1 = "{{relDeviceName1}}";
    public static final String EXP_RELDEVICELABEL1 = "{{relDeviceLabel1}}";
    public static final String EXP_RELDEVICEFACTORY1 = "{{relDeviceFactory1}}";
    public static final String EXP_RELDEVICEHIGH1 = "{{relDeviceHigh1}}";
    public static final String EXP_RELDEVICELOTANDLAT1 = "{{relDeviceLotAndLat1}}";
    public static final String EXP_SNAPSHOTTIME1 = "{{snapShotTime1}}";
    public static final String EXP_RELDEVICEADDRESS1 = "{{relDeviceAddress1}}";

    public static final String EXP_RELDEVICECODE2 = "{{relDeviceCode2}}";
    public static final String EXP_RELDEVICENAME2 = "{{relDeviceName2}}";
    public static final String EXP_RELDEVICELABEL2 = "{{relDeviceLabel2}}";
    public static final String EXP_RELDEVICEFACTORY2 = "{{relDeviceFactory2}}";
    public static final String EXP_RELDEVICEHIGH2 = "{{relDeviceHigh2}}";
    public static final String EXP_RELDEVICELOTANDLAT2 = "{{relDeviceLotAndLat2}}";
    public static final String EXP_SNAPSHOTTIME2 = "{{snapShotTime2}}";
    public static final String EXP_RELDEVICEADDRESS2 = "{{relDeviceAddress2}}";

    public static final String EXP_RELDEVICECODE3 = "{{relDeviceCode3}}";
    public static final String EXP_RELDEVICENAME3 = "{{relDeviceName3}}";
    public static final String EXP_RELDEVICELABEL3 = "{{relDeviceLabel3}}";
    public static final String EXP_RELDEVICEFACTORY3 = "{{relDeviceFactory3}}";
    public static final String EXP_RELDEVICEHIGH3 = "{{relDeviceHigh3}}";
    public static final String EXP_RELDEVICELOTANDLAT3 = "{{relDeviceLotAndLat3}}";
    public static final String EXP_SNAPSHOTTIME3 = "{{snapShotTime3}}";
    public static final String EXP_RELDEVICEADDRESS3 = "{{relDeviceAddress3}}";

    public static final String EXP_RELDEVICECODE4 = "{{relDeviceCode4}}";
    public static final String EXP_RELDEVICENAME4 = "{{relDeviceName4}}";
    public static final String EXP_RELDEVICELABEL4 = "{{relDeviceLabel4}}";
    public static final String EXP_RELDEVICEFACTORY4 = "{{relDeviceFactory4}}";
    public static final String EXP_RELDEVICEHIGH4 = "{{relDeviceHigh4}}";
    public static final String EXP_RELDEVICELOTANDLAT4 = "{{relDeviceLotAndLat4}}";
    public static final String EXP_SNAPSHOTTIME4 = "{{snapShotTime4}}";
    public static final String EXP_RELDEVICEADDRESS4 = "{{relDeviceAddress4}}";

    public static final String EXP_RELDEVICECODE5 = "{{relDeviceCode5}}";
    public static final String EXP_RELDEVICENAME5 = "{{relDeviceName5}}";
    public static final String EXP_RELDEVICELABEL5 = "{{relDeviceLabel5}}";
    public static final String EXP_RELDEVICEFACTORY5 = "{{relDeviceFactory5}}";
    public static final String EXP_RELDEVICEHIGH5 = "{{relDeviceHigh5}}";
    public static final String EXP_RELDEVICELOTANDLAT5 = "{{relDeviceLotAndLat5}}";
    public static final String EXP_SNAPSHOTTIME5 = "{{snapShotTime5}}";
    public static final String EXP_RELDEVICEADDRESS5 = "{{relDeviceAddress5}}";

    public static final String EXP_RELDEVICECODE6 = "{{relDeviceCode6}}";
    public static final String EXP_RELDEVICENAME6 = "{{relDeviceName6}}";
    public static final String EXP_RELDEVICELABEL6 = "{{relDeviceLabel6}}";
    public static final String EXP_RELDEVICEFACTORY6 = "{{relDeviceFactory6}}";
    public static final String EXP_RELDEVICEHIGH6 = "{{relDeviceHigh6}}";
    public static final String EXP_RELDEVICELOTANDLAT6 = "{{relDeviceLotAndLat6}}";
    public static final String EXP_SNAPSHOTTIME6 = "{{snapShotTime6}}";
    public static final String EXP_RELDEVICEADDRESS6 = "{{relDeviceAddress6}}";

    public static final String EXP_RELDEVICECODE7 = "{{relDeviceCode7}}";
    public static final String EXP_RELDEVICENAME7 = "{{relDeviceName7}}";
    public static final String EXP_RELDEVICELABEL7 = "{{relDeviceLabel7}}";
    public static final String EXP_RELDEVICEFACTORY7 = "{{relDeviceFactory7}}";
    public static final String EXP_RELDEVICEHIGH7 = "{{relDeviceHigh7}}";
    public static final String EXP_RELDEVICELOTANDLAT7 = "{{relDeviceLotAndLat7}}";
    public static final String EXP_SNAPSHOTTIME7 = "{{snapShotTime7}}";
    public static final String EXP_RELDEVICEADDRESS7 = "{{relDeviceAddress7}}";

    public static final String EXP_RELDEVICECODE8 = "{{relDeviceCode8}}";
    public static final String EXP_RELDEVICENAME8 = "{{relDeviceName8}}";
    public static final String EXP_RELDEVICELABEL8 = "{{relDeviceLabel8}}";
    public static final String EXP_RELDEVICEFACTORY8 = "{{relDeviceFactory8}}";
    public static final String EXP_RELDEVICEHIGH8 = "{{relDeviceHigh8}}";
    public static final String EXP_RELDEVICELOTANDLAT8 = "{{relDeviceLotAndLat8}}";
    public static final String EXP_SNAPSHOTTIME8 = "{{snapShotTime8}}";
    public static final String EXP_RELDEVICEADDRESS8 = "{{relDeviceAddress8}}";

    public static final String EXP_RELDEVICECODE9 = "{{relDeviceCode9}}";
    public static final String EXP_RELDEVICENAME9 = "{{relDeviceName9}}";
    public static final String EXP_RELDEVICELABEL9 = "{{relDeviceLabel9}}";
    public static final String EXP_RELDEVICEFACTORY9 = "{{relDeviceFactory9}}";
    public static final String EXP_RELDEVICEHIGH9 = "{{relDeviceHigh9}}";
    public static final String EXP_RELDEVICELOTANDLAT9 = "{{relDeviceLotAndLat9}}";
    public static final String EXP_SNAPSHOTTIME9 = "{{snapShotTime9}}";
    public static final String EXP_RELDEVICEADDRESS9 = "{{relDeviceAddress9}}";

    public static final String EXP_RELDEVICECODE10 = "{{relDeviceCode10}}";
    public static final String EXP_RELDEVICENAME10 = "{{relDeviceName10}}";
    public static final String EXP_RELDEVICELABEL10 = "{{relDeviceLabel10}}";
    public static final String EXP_RELDEVICEFACTORY10 = "{{relDeviceFactory10}}";
    public static final String EXP_RELDEVICEHIGH10 = "{{relDeviceHigh10}}";
    public static final String EXP_RELDEVICELOTANDLAT10 = "{{relDeviceLotAndLat10}}";
    public static final String EXP_SNAPSHOTTIME10 = "{{snapShotTime10}}";
    public static final String EXP_RELDEVICEADDRESS10 = "{{relDeviceAddress10}}";

    public static final String EXP_SPOTNO = "{{spotNo}}";
    public static final String EXP_CITYNAME = "{{cityName}}";
    public static final String EXP_COUNTYNAME = "{{countyName}}";
    public static final String EXP_TOWNNAME = "{{townName}}";
    public static final String EXP_VILLAGENAME = "{{villageName}}";
    public static final String EXP_REGIONSOURCE = "{{regionSource}}";
    public static final String EXP_SPOTAREA = "{{spotArea}}";
    public static final String EXP_SPOTPERIMETER = "{{spotPerimeter}}";
    public static final String EXP_CHANGETYPE = "{{changeType}}";
    public static final String EXP_AREA = "{{area}}";
    public static final String EXP_LANDUSETYPE = "{{landUseType}}";
    public static final String EXP_IMAGETIME = "{{imageTime}}";
    public static final String EXP_DETECTIONFLAG = "{{detectionFlag}}";

    /**
     * 算法参数名称与参数阈值  description
     */
    /**
     * 查询参数 设备编码、通道编码、任务ID、算法编码
     */
    //设备编码
    public static final String DESC_DEVICE_CODE_PARAM = "deviceCode";
    //通道编码
    public static final String DESC_CHANNEL_CODE_PARAM = "channelCode";
    //任务ID
    public static final String DESC_TASK_ID_PARAM = "taskId";
    //算法编码
    public static final String DESC_ALGORITHM_CODE_PARAM = "algorithmCode";
    //租户
    public static final String DESC_TENANT_ID_PARAM = "tenantId";
    //行业
    public static final String DESC_INDUSTRY_CODE_PARAM = "industryCode";
    //告警类型
    public static final String DESC_ALARM_TYPE_PARAM = "alarmType";

    //分析类型
    public static final String ANALY_TYPE = "analyType";
    //分隔符
    public static final String SPLIT_AND = "&";

    /**
     * 返回参数 算法分析时段、识别频率、识别频率单位、配置参数
     */
    //算法分析时段
    public static final String DESC_ANALYSIS_PERIOD_RESP = "analysisPeriod";
    //识别频率
    public static final String DESC_TIMEINTERVAL_RESP = "timeInterval";
    //兼容大模型识别频率
    public static final String DESC_TIMEINTERVAL_MODEL_RESP = "timeInterval_model";
    //识别频率单位 1:小时 2:分钟 3:秒
    public static final String DESC_TIMEINTERVAL_UNIT_RESP = "timeIntervalUnit";
    //配置参数
    public static final String DESC_MODEL_CONFIG_RESP = "modelConfig";
    public static final String EMPTY_STRING = "[\"\"]";

    public static final String WATER_LEVEL_ALARM_TYPE_VALUE = "1818127";

    public static final String WATER_LEVEL_EXTENDED_FIELDS = "water_level_extended_fields";

    public static final String PARAM_TASK_BATCH_ID = "taskBatchId";

    public static final String TIME_PERIOD_SUPPRESSION_STRATEGY = "TimePeriodSuppressionStrategy";

    //获取任务
    public static final String ALARM_OPERATE_TASK_MODIFY_ERROR = "告警研判任务更新失败！！";
    public static final String ALARM_OPERATE_TASK_LIST = "告警任务列表查询";
    public static final String ALARM_OPERATE_TASK_MODIFY = "告警任务修改";
    public static final String REQUEST_LOG = "请求报文:";
    public static final String RESPONSE_LOG = "返回报文:";
    // AI研判通知模板配置
    public static final String OVERDUE_ONE_JUDGE_RESULT_NOTICE = "OVERDUE_ONE_JUDGE_RESULT_NOTICE";
    public static final String OVERDUE_JUDGE_RESULT_NOTICE = "OVERDUE_JUDGE_RESULT_NOTICE";
    public static final String ONE_JUDGE_RESULT_NOTICE = "ONE_JUDGE_RESULT_NOTICE";
    public static final String AUTO_JUDGE_RESULT_NOTICE = "AUTO_JUDGE_RESULT_NOTICE";
    public static final String ONE_JUDGE_RESULT_NOTICE_SUCCESS = "ONE_JUDGE_RESULT_NOTICE_SUCCESS";
    public static final String ONE_JUDGE_RESULT_NOTICE_ERROR = "ONE_JUDGE_RESULT_NOTICE_ERROR";
    public static final String AUTO_JUDGE_RESULT_CONVERSATION = "AUTO_JUDGE_RESULT_CONVERSATION";
    public static final String ONE_JUDGE_RESULT_CONVERSATION = "ONE_JUDGE_RESULT_CONVERSATION";
    public static final int AI_JUDGE_TASK_STATUS_OPEN = 0;
    public static final int AI_JUDGE_TASK_STATUS_CLOSE = 1;
    public static final String ONE_JUDGE_TASK_EXECUTION_TYPE = "1";
    public static final String AUTO_JUDGE_TASK_EXECUTION_TYPE = "2";

    public static final String TASK_STATUS_PROCESS_KEY = "taskStatusProcess_";

    public static final String LOCK = "lock";

    // 查询类型 NEST - 机巣 UAV - 无人机
    public static final String NEST = "NEST";
    public static final String UAV = "UAV";

    //抽帧任务key
    public static final String EXTRACTION_TASK_ID_ = "extractionTaskId_";
    public static final int EXTRACTION_TASK_CACHE_TIME = 5;

    /**
     * 单期遥感解析接收结果 kafka topic
     */
    public static final String EPOCHAL_GEO_ANALYTICS_RESULT = "EPOCHAL_GEO_ANALYTICS_RESULT";

    //指点飞行挂载开关
    public static final String GUIDE_FLIGHT_MOUNT_SWITCH = "guide_flight_mount_switch";
}

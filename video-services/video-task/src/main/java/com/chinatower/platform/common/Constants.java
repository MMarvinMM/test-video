package com.chinatower.platform.common;

import java.text.SimpleDateFormat;

public class Constants {
    /**
     * 最小表时间
     */
    public static final String MIN_TABLE_DATE = "202201";

    /**
     * 工单告警表
     * vm_warning_order_t_
     */
    public final static String WARN_ORDER_TEBLE = "vm_warning_order_t_";
    public final static String SYS_DICT_DATA = "sys_week_day";
    public final static String PARAM_TENANT_NAME = "tenantName";
    public final static String PARAM_TENANT_ID = "tenant_id";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String EXCEPTION_INFO = "*********************系统异常信息*****************：";

    public static final SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
    public static final String STERT_DATE_HMS = " 00:00:00";
    public static final String END_DATE_HMS = " 23:59:59";

    public static final SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
    public static final String STR_01 = "01";
    public static final String LIST = "list";
    public static final String HANDLEYEAR = "handleYear";
    public static final String HANDLEDATETIME = "handleDateTime";
    public static final String PARAMLIST = "receive param: {}, collectionName: {}";
    public static final String USETIME = "耗时: {}";
    /**
     * 延迟消息推送（mongo）
     * vm_warning_order_t_
     */
    public final static String SYS_ORDER_DELAY_TEBLE = "order_delay_message_t";

    public final static String PARAM_COLLECTION_NAME = "collectionName";

    public final static String PARAM_ENTITY_CLASS = "entityClass";

    /**
     * 告警研判表
     * vm_alarm_operate_t
     */
    public final static String WARN_ALARM_OPERATE = "vm_alarm_operate_t";
    /**
     * 告警压制表
     */
    public final static String WARN_WARNING_INTERCEPT = "vm_warning_intercept_t";

    // 短信
    public static final String MSG_TYPE_SMS = "2";

    public static final String PARAM_NULL = "null";
    // 短信模板 {0},在{1}发现{2}，请登录系统及时处理！
    public static final String MSG = "【铁塔视联】截至{0}，今日【{1}】待研判告警【{2}】件，请登录系统及时处理！";

    public static final int INT_ZERO = 0;
    public static final int INT_ONE = 1;
    public static final String PARAM_ZERO = "0";
    public static final String ONE = "1";
    public static final String COLON = ":";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String TASK_JOB = "task_job";
    public static final String TASK_HISTORY_JOB = "task_history_job";
    public static final String FILTER_STRING = "-|\\s|:";
    public static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PENDING_PROCESSED = "待处理";
    public static final String NO_KEY = "task_job暂时没有key值";
    public static final String OTHER_RUN = "其他线程获取到列表，请稍等";
    public static final String COMMA = ",";
    public static final String SUCCESS = "成功";
    public static final String ALARM_DAILY_STATISTICS_MSG = "告警每日维度统计(租户)沉淀数据定时";
    public static final String ALARM_DAILY_STATISTICS_MANUAL_MSG = "告警每日维度统计(租户)沉淀数据定时-人工上报";
    public static final String ALARM_YEAR_STATISTICS_TOWER_MSG = "告警年度维度统计-铁塔视角";
    public static final String BAYONET_DEVICE_HOUR_MSG = "卡口设备告警统计-小时";
    public static final String BAYONET_DEVICE_DAY_MSG = "卡口设备告警统计-当天";
    public static final String BAYONET_DEVICE_MONTH_MSG = "卡口设备告警统计-当月";
    public static final String BAYONET_DEVICE_YEAR_MSG = "卡口设备告警统计-当年";
    public static final String YESTERDAY_DAY_TOWER = "昨日告警每日维度统计-铁塔视角";
    public static final String EVENT_INTERCEPT_HOUR_TENANT = "按照小时沉淀压制数据-租户视角";
    public static final String EVENT_INTERCEPT_HOUR_TOWER = "按照小时沉淀压制数据-铁塔视角";
    public static final String EVENT_DAY_TENANT = "按照小时沉淀数据-租户视角";
    public static final String EVENT_DAY_TOWER = "按照小时沉淀数据-铁塔视角";
    public static final String EVENT_INTERCEPT_DAY_TENANT = "告警压制事件统计表-租户视角";
    public static final String TENANT_ID = "tenant_id";
    public static final String TYPE_VALUE = "type_value";
    public static final String INDUSTRY_CODE = "industry_code";
    public static final String APP_VER_CODE = "app_ver_code";
    public static final String ROLE_USERS = "role_users";
    public static final String UAV_FLIGHT_TASKS = "uav_flight_tasks";
    public static final String PLAN_EXECUTE_TIME = "planExecuteTime";
    public static final String UAV_FLIGHT_TASKS_STATISTICS_STATUS = "uav_flight_tasks_statistics_status";

    //权限标识 和告警来源类型关系
    public static final String DEVICE_PERMISSION = "1,3,4,8,10";
    public static final String REGION_PERMISSION = "9";
    public static final String OTHER_PERMISSION = "2";
    public static final String PARAM_SOURCE = "source";
    public static final String PARAM_ORDER_ID = "orderId";
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_OPERATE_STATUS = "operateStatus";
    public static final String PARAM_MESSAGE_JSON = "messageJson";
    public static final String PARAM_INDUSTRY_CODE = "industryCode";
    public static final String PARAM_FLOW_ID = "flowId";
    public static final String PARAM_LINK_ID = "linkId";
    public static final String PARAM_WARNING_SOURCE = "warningSource";
    public static final String PARAM_WARNING_TYPE_ID = "warningTypeId";
    public static final String PARAM_ALARM_TIME = "alarmTime";
    public static final String PARAM_CREATE_TIME = "createTime";
    public static final String PARAM_UPDATE_TIME = "updateTime";
    public static final String PARAM_TASK_ID = "taskId";
    public static final String PARAM_LAYER_ID = "layerId";
    public static final String PARAM_FEATURE_ID = "featureId";
    public static final String PARAM_TABLE_NAME = "tableName";
    public static final String PARAM_ALARM_TIME_START = "alarmTimeStart";
    public static final String PARAM_ALARM_TIME_END = "alarmTimeEnd";
    public static final String PARAM_PROVINCE_CODE = "provinceCode";
    public static final String PARAM_CITY_CODE = "cityCode";
    public static final String PARAM_COUNTY_CODE = "countyCode";
    public static final String PARAM_DEVICE_CODE = "deviceCode";
    public static final String PARAM_SUM = "sum";
    public static final String GRID_LEVEL = "gridLevel";
    public static final String GRID_NAME = "gridName";
    public static final String GRID_ID = "gridId";
    public static final String TAG_ID = "tagId";
    public static final String TAG_NAME = "tagName";
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

    public static final String WARNING_SOURCE_COMMON = "warning_source_common";

    /** 用户统计 */
    //日期格式
    public static final int TWO_THOUSAND = 2000;
    public static final String FORMAT_DATE = "%Y-%m-%d";
    //表名称
    public static final String LOG_LOGININFO = "log_logininfo";
    //筛选条件 登录时间
    public static final String LOGIN_TIME = "loginTime";
    //统计 登录次数
    public static final String LOGIN_COUNT = "loginCount";
    //分组 统计 登录端
    public static final String CLIENT_TYPE = "clientType";
    //分组 统计 登录名称
    public static final String USER_NAME = "userName";
    //分组 统计 浏览器
    public static final String BROWSER_NAME = "browserName";
    //id
    public static final String _ID = "_id";
    // app操作系统
    public static final String APP_MANAGE = "app_manage";
    public static final String APP_SYSTEM_NAME = "appSystemName";
    // pc操作系统
    public static final String PC_SYSTEM_NAME = "pcSystemName";
    // app版本
    public static final String APP_VERSION_NAME = "versionName";
    // 手机型号
    public static final String PHONE_MODEL_NAME = "phoneModelName";
    // 原因
    public static final String LOGIN_CAUSE = "loginCause";
    public static final String PLATFORM_CODE = "platform_code";
    public static final String NEWLAND_BOOT_INNER = "newlandBootInner";
    public static final String ALGORITHM_TASK_UPGRADE_COLONY = "algorithm_task_upgrade_colony";
    //日期
    public static final String DATE = "date";
    public static final String HOUR_STR = "hour_str";
    public static final String YEAR_STR = "year_str";
    public static final String MONTH_STR = "month_str";
    public static final String DAY_STR = "day_str";
    public static final String STATUS = "status";
    public static final String PARAM_TENANTID = "tenantId";
    public static final String GRID_KEEPER_INSPECTION_TASK_HANDLER = "网格员巡查任务定时";
    public static final String UNDERLINE = "_";
    public static final String LINE_CENTER = "-";
    public static final String EMPTY_STR = "";

    public static final String WAYLINEWPML = "wayline.wpml";
    public static final String KML = "kml";
    public static final String DOCUMENT = "Document";
    public static final String FOLDER = "Folder";
    public static final String PLACEMARK = "Placemark";
    public static final String POINT = "Point";
    public static final String COORDINATES = "coordinates";
    public static final String WPMLHEIGHT = "wpml:height";
    public static final String LOCATION_2DSPHERE_IDX = "location_2dsphere";
    public static final String LOCATION = "location";
    public static final String TWO_DSPHERE = "2dsphere";

    public static final String ALARM_PUSH_TASK_KEY = "alarm_push_task_key";

}

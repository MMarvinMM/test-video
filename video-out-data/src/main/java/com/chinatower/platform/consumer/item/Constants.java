package com.chinatower.platform.consumer.item;

public final class Constants {
    public static final String MINUTE = "minute";
    public static final String HOUR = "hour";
    public static final String DAY = "day";
    public static final String TABLE_NAME_ = "sensor_attribute_report_";
    public static final String TABLE_NAME = "sensor_attribute_report";
    public static final String TABLE_NAME_ALL = "sensor_attribute_report_all";
    public static final String TABLE_NAME_ALL_YYYYMM = "sensor_attribute_report_all_";
    public static final String TABLE_NAME_MINUTE_ALL = "sensor_attribute_report_minute_all";
    public static final String TABLE_NAME_HOUR_ALL = "sensor_attribute_report_hour_all";
    public static final String TABLE_NAME_DAY_ALL = "sensor_attribute_report_day_all";
    public static final String TIMEFLAG = "timeFlag";
    public static final String DEVICECODE = "deviceCode";
    public static final String DISTRICTCODE = "districtCode";
    public static final String PROVINCECODE = "provinceCode";
    public static final String CITYCODE = "cityCode";
    public static final String ADCODE = "adcode";
    public static final String TOWNCODE = "towncode";
    public static final String AUTHPATHLIS = "authPathLis";
    public static final String KAFKALISTENER_TOPIC_MSG = "KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}";
    public static final String PARAMLIST = "receive param: {}, collectionName: {}";
    public static final String USETIME = "耗时: {}";
    public static final String TABLE_NAME_STATUS = "vm_iot_device_t_";
    public static final String TABLE_NAME_RADAR_WARNING_TARGET = "radar_warning_target";
    public static final String TABLE_NAME_SENSOR_STATUS = "vm_sensor_monitor_status_";
    public static final String TABLE_NAME_RADAR_REPORT = "radar_report_";

    public static final String TABLE_NAME_SENSOR_FILE = "vm_sensor_file_info";

    public static final String TABLE_NAME_SENSOR_EVENT = "vm_sensor_event_info";
    public static final String WARN_ORDER_ID = "warning_order_id";
    public static final String ORDER_FLOW_ID = "order_flow_id";
    public static final String PARAM_TENANT_ID = "tenantId";
    public static final String PARAM_CONFIG_METHOD = "configMethod";
    public static final String PARAM_INDUSTRY_CODE = "industry_code";
    public static final String RES_INDUSTRY_CODE = "industryCode";
    public static final String PARAM_ORDER_STATUS = "order_status";
    public final static String WARN_ORDER_TEBLE = "vm_warning_order_t_"; //工单告警表
    public final static String WARN_ORDER_PROCESS_TEBLE = "vm_order_flow_record_t_"; //流程记录表
    public final static String WARN_ALARM_OPERATE = "vm_alarm_operate_t_"; //告警研判表
    public final static String WARN_ALARM_INTERCEPT = "vm_warning_intercept_t_"; //告警拦截表
    public final static String PARAM_WARNORDER_TABLE_NAME = "warnOrderTableName";
    public final static String PARAM_FLOWRECORD_TABLE_NAME = "flowRecordTableName";
    public static final String DELETE = "DELETE";
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String CREATE = "CREATE";
    public static final String DELETEFLAG_0 = "0"; //有效
    public static final String DELETEFLAG_1 = "1"; //删除
    public static final String DELETEFLAG_2 = "2"; //删除
    public static final String APMODY = "1";
    public static final String COMMA = ",";
    public static final int INDEXASC_ONE = 1;
    public static final int INDEXASC_MINUS_ONE = -1;
    public static final String INDEXNAME_PREFIX = "index_";
    public static final String PARAM_NOT_EXISTENT = "查询参数为空";
    public static final String DEVICE_STATUS_FAIL = "查询设备状态失败";
    public static final String SETNAME_NOT_SPECIFIED = "未指定操作的集合名";
    public static final String PARAM_COLLECTIONNAME = "collectionName";
    public static final String PARAM_ENTITYCLASS = "entityClass";
    public static final String TOPIC = "topic";
    public static final String MESSAGE = "message";
    public static final String SIGN = "[";
    public static final String DATE_YM = "dateYM";
    public static final String  KEY = "key";
    public static final String  HASHED = "hashed";
    public static final String  ORDER = "order";
    public static final String  RECORD = "record";
    public static final String PLATFORM_CODE = "platform_code";

    //工单表索引列
    public static final String INDEXFIELD_ALARM_TIME = "alarm_time";
    public static final String INDEXFIELD_PROVINCE_CODE = "province_code";
    public static final String INDEXFIELD_TASK_ID = "task_id";
    public static final String INDEXFIELD_CITY_CODE = "city_code";
    public static final String INDEXFIELD_COUNTY_CODE = "county_code";
    public static final String INDEXFIELD_WARNING_TYPE_ID = "warning_type_id";
    public static final String INDEXFIELD_PROVINCE_CITY = "province_code,city_code";
    public static final String INDEXFIELD_LONGITUDE_LATITUDE = "longitude,latitude";
    public static final String INDEXFIELD_DEVICE_CODE = "device_code";
    public static final String INDEXFIELD_PROCESS_ID = "process_id";
    public static final String INDEXFIELD_GROUP_ID = "group_id";
    public static final String INDEXFIELD_ALARM_BODY = "alarm_body";
    public static final String INDEXFIELD_ALGORITHM_CODE = "algorithm_code";
    public static final String INDEXFIELD_TENANT_ID = "tenant_id";
    public static final String INDEXFIELD_APP_VER_CODE = "app_ver_code";
    public static final String INDEXFIELD_DUPLICATE_ORDER_ID = "duplicate_order_id";
    public static final String INDEXFIELD_JUDGE_TIME = "judge_time";
    public static final String INDEXFIELD_WARNING_ORDER_ID = "warning_order_id";
    public static final String INDEXFIELD_CHANNEL_CODE = "channel_code";
    public static final String INDEXFIELD_TOWN_CODE = "town_code";

    //工单环节表索引列
    public static final String INDEXFIELD_ORDER_ID = "order_id";
    public static final String INDEXFIELD_FLOW_NODE_TIME = "flow_node_time";
    public static final String INDEXFIELD_FLOW_END_TIME = "flow_end_time";
    public static final String INDEXFIELD_ORDER_FLOW_ID = "order_flow_id";

    //传感器属性索引列
    public static final String INDEXFIELD_DEVICECODE = "deviceCode";
    public static final String INDEXFIELD_MONITORTIME = "monitorTime";
    public static final String INDEXFIELD_TEMPERATURE = "temperature";
    public static final String INDEXFIELD_UPDATETIME = "updateTime";

    //运营研判表索引列
    public static final String INDEXFIELD_PROVINCE_ID = "province_id";
    public static final String INDEXFIELD_CITY_ID = "city_id";
    public static final String INDEXFIELD_COUNTY_ID = "county_id";
    public static final String INDEXFIELD_CREATE_USER = "create_user";
    public static final String INDEXFIELD_EMERGENCY_LEVEL = "emergency_level";
    public static final String INDEXFIELD_CREATE_TIME = "create_time";
    public static final String INDEX_DEL_ALARMTIME_TYPE = "alarm_time,operate_type,judge_result";
    public static final String INDEX_DEL_TIME_RESULT = "create_time,alarm_time,operate_type,judge_result";
    //告警拦截表索引列
    public static final String INDEX_TENANTID_TIME_FLAG_RESULT = "tenant_id,alarm_time,delete_flag,judge_result,industry_code";
    public static final String INDEX_TIME_FLAG_RESULT = "alarm_time,delete_flag,judge_result,importance_level";
    public static final String INDEXFIELD_NAME = "index_time_del_result";

    //设备未完结告警数量统计表索引列
    public static final String INDEX_DEVICE_TENANTID_CODE = "device_code,tenant_id,industry_code";

    public static final String DEPENDENCE_MANAGE = "dependence_manage";
    public static final String HIGHLIGHT_DURATION = "highlight_duration";
    public static final String SEP = "-";
    public static final String ZERO = "0";
    public static final int ZERO_NUM = 0;
    public static final int ONE_NUM = 1;
    public static final String SIX = "6";
    public static final String SEVEN = "7";

    public static final String TWO = "2";
    public static final String CENTER_CODE = "10000000001";

    public static final String VM_DEVICE_INFO_T = "vm_device_info_t";

    public static final String VM_DEVICE_MODEL_T = "vm_device_model_t";

    public static final String MODELID = "modelId";

    public static final String DEVICEID = "deviceId";

    public static final String FILTER = "filter";
    public static final String QUERY = "query";
    public static final String PROJECTION = "projection";
    public static final String SORT = "sort";
    public static final String LIMIT = "limit";
    public static final String SKIP = "skip";
    public static final String PIPELINE = "pipeline";
    public static final String PREFIX_LIMIT = "$limit";
    public static final String PREFIX_SKIP = "$skip";
    public static final String FIND = "find";
    public static final String AGGREGATE = "aggregate";

}

package com.chinatower.platform.common;

public class Constants {

    public final static String WARN_ORDER_TEBLE_ = "vm_warning_order_t_"; //工单告警表
    public final static String WARN_ORDER_PROCESS_TEBLE_ = "vm_order_flow_record_t_"; //流程记录表
    public final static String WARN_ALARM_OPERATE_ = "vm_alarm_operate_t_"; //告警研判表
    public final static String WARN_ALARM_INTERCEPT_ = "vm_warning_intercept_t_"; //告警拦截表
    public final static String WARN_ORDER_TEBLE = "vm_warning_order_t";
    public final static String WARN_ALARM_OPERATE = "vm_alarm_operate_t";
    public final static String WARN_ALARM_INTERCEPT = "vm_warning_intercept_t";

    public final static String VM_FLOW_HANDLE_RECORD_T = "vm_flow_handle_record_t";

    public static final String COMMA = ",";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final int FIVE_THOUSAND = 5000;
    public static final String WARNING_SOURCE_11 = "11";
    public static final String MODELID_DAHUA = "dahua";
    public static final String MODELID_DHUA = "dhua";
    public static final String PENDING_ACCEPTANCE= "待受理";
    public static final String ACCEPTANCE_COMPLETED= "已受理";
    public static final String REASSIGNMENT = "转派";
    public static final String FALLBACK = "回退";
    public static final String COMPLETE = "完成";
    public static final String NOCOMPLETE = "未完成";
    public static final String FALSEALARM= "误报";
    public static final String TRUEPOLICE= "真警";
    public static final String REPEATEDALARM= "重复告警";
    public static final String CLOSE_ALARM= "关闭告警";
    public static final String CLOSED= "已完结";
    public static final String HANDLEDBY= "办理人: ";
    public static final String HANDLINGINSTRUCTIONS= "办理说明: ";
    //卫星告警
    public static final String ALARM_SOURCE_9 = "9";
    // 告警状态枚举
    public static final int ORDER_STATUS = 3;
    // 事件等级枚举类型
    public static final int EMERGENCY_LEVEL = 41;
    // 告警重要等级枚举
    public static final int IMPORTANCE_LEVEL = 90;
    // 告警等级枚举
    public static final String ALARM_LEVEL = "alarm_level";
    // 摄像机厂家枚举
    public static final int MODEL_ID = 103;
    // 消息类型枚举
    public static final int MSG_TYPE = 104;
    // 通知类型枚举
    public static final int NOTICE_RESOURCE = 102;
    // 物联设备厂家枚举
    public static final String IOT_MODEL_ID = "iot_device_mfgr_code";
    // 告警来源枚举
    public static final String WARNING_SOURCE_COMMON = "warning_source_common";
    // 告警压制原因枚举
    public static final String DICT_TYPE_ALARM_PRESSING_REASON= "alarm_pressing_reason";

    public static final String WARN_ORDER_ID = "warning_order_id";
    public static final String PARAM_WARN_ORDER_ID = "warningOrderId";
    public static final String PARAM_TASK_ID = "taskId";
    public static final String PARAM_TENANT_ID = "tenantId";
    public static final String PARAM_WARNING_SOURCE_NAME = "warningSourceName";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_DEVICE_CODE = "deviceCode";
    public static final String PARAM_MONITORS ="monitors";
    public static final String PARAM_DEVICEINFO ="deviceInfo";
    public static final String PARAM_RELDEVICE="reldevice";
    public static final String SPLIT_SIGN = "_";
    public static final String GRID_ID = "gridId";
    public static final String GRID_CODE = "gridCode";
    public static final String GRID_NAME = "gridName";
    public static final String GRID_CENTER = "gridCenter";
    public static final String GRID_RANGE = "gridRange";
    public static final String PARAM_TYPE_VALUE = "typeValue";
    public static final String PARAM_TYPE_NAME = "typeName";
    public static final String PARAM_PRESET_ID = "presetId";
    public static final String PARAM_PRESET_NAME = "presetName";
    public static final String PARAM_LAYER_ID = "layerId";
    public static final String PARAM_POLYGON_NAME = "polygonName";
    public static final String PARAM_PLATFORM_CODE = "platformCode";
    public static final String PARAM_PLATFORM_NAME = "platformName";
    public static final String PARAM_FLOW_ID = "flowId";
    public static final String PARAM_ALARM_CONFIG_NAME = "alarmFlowName";
    public static final String XLSX_SUFFIX = ".xlsx";
    public static final String RESULT_DATA = "data";
    public static final String RADAR_CODES = "radarCodes";
    public static final String PARAM_NULL = "null";

    public static final String PLATFORM_CODE = "platform_code";
    public static final String PARAM_CONFIG_VALUE = "configValue";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_NICK_NAME = "nickName";
    public static final String PARAM_INDUSTRY_CODE = "industryCode";
    public static final String PARAM_INDUSTRY_NAME = "industryName";
    public static final String KEEPER_ID = "keeperId";
    public static final String KEEPER_NAME = "keeperName";
    public static final String KEEPER_PHONE_NUMBER = "keeperPhoneNumber";
    public static final String COLOR = "color";
    public static final String BORDER_COLOR = "borderColor";
    public static final String BORDER_COLOR_TRANSPARENCY = "borderColorTransparency";
    public static final String BORDER_COLOR_LINE_THICKNESS = "borderColorLineThickness";
    public static final String COLOR_TRANSPARENCY = "colorTransparency";
    public static final String ERR_MSG_RADAR = "获取雷达详情信息失败-参数:";
    public static final String ERR_MSG_JSON = "WarningOrderEntity.getExtraField()转换JSON异常-参数:";
    public static final String ERR_MSG_PTZESCAPE = "告警工单ptz转义异常，工单号：{}，异常：{}";
    public static final String TMP_DIR = "java.io.tmpdir";
    public static final String DELETE_TMP_FILE_ERROR = "删除临时文件失败";

    /** 属地告警同步状态 Mongo 集合名 */
    public static final String ALARM_SYNC_STATUS = "alarm_sync_status";
    /** 拉取状态：成功 */
    public static final int SYNC_STATUS_SUCCESS = 1;
    /** 拉取状态：失败 */
    public static final int SYNC_STATUS_FAIL = 2;

    /** 同步类型：工单 */
    public static final int SYNC_TYPE_WARNING_ORDER = 1;
    /** 同步类型：运营研判 */
    public static final int SYNC_TYPE_OPERATE = 2;
    /** 同步类型：回收站 */
    public static final int SYNC_TYPE_INTERCEPT = 3;

    /** 属地告警同步异常提醒字典类型，dict_value 为邮箱，remark 标注收件人/抄送人 */
    public static final String ALARM_SYNC_FAIL_RECEIVE_EMAIL = "alarm_sync_fail_receive_email";

    /** 字典备注：收件人 */
    public static final String ALARM_SYNC_MAIL_RECEIVER = "收件人";

    /** 字典备注：抄送人 */
    public static final String ALARM_SYNC_MAIL_CC = "抄送人";

    /** EasyExcel 读取时文件无 sheet，视为无数据 */
    public static final String EXCEL_NO_SHEET_MSG = "can not find any sheet";

}

package com.chinatower.device.constants;

/**
 * @author yy
 */
public class DeviceSyncConstants {

    public static final String VIDEO_DEVICE_CHANNEL_CODE_LIST_KEY = "VIDEO:DEVICE:SYNC:CHANNEL:CODE:LIST";

    public static final String  VIDEO_DEVICE_ERR_LIST = "video_device_sync_err";

    public static final String BATCH_NUM_LIMIT_50 = "批量操作数量限制每次最多选中50条";
    public static final String PARAM_DATA_ERROR = "入参数据错误!";
    public static final String ALGO_CONFIG_LIST_PARAM = "算法配置列表入参:";
    public static final String LINE_ID = "_id";

    public static final String DEVICE_TREE_REQUEST_LOG = "查询摄像头树---请求报文------{}";
    public static final int INT_50 = 50;

    public static final String HORIZON_LINE = "-";

    public static final String ZERO_STR = "0";
    public static final String ONE_STR = "1";
    public static final String TWO_STR = "2";
    public static final String THREE_STR = "3";
    public static final String FOUR_STR = "4";

    public static final String ADD_ALGO_ERROR = "新增后端算法异常";
    public static final String ADD_EXIST_ALGO = "新增数据内部已存在该算法的配置，请不要重复配置";
    public static final String EXIST_PRESET_ALGO_CANNOT_ENABLE = "已经启用通道算法，如需启用预置位算法，请先将已启用的算法任务停用后再试。";
    public static final String EXIST_CHAN_ALGO_CANNOT_ENABLE = "已经启用预置位算法，如需启用通道算法，请先将已启用的算法任务停用后再试。";
    public static final String ADD_DEVICE_OPT_AUTH_ERROR = "新增摄像机操控权限接口异常";
    public static final String BATCH_ADD_DEVICE_OPT_AUTH_ERROR = "批量新增摄像机操控权限接口异常";
    public static final String USERCODE_NOT_NULL = "用户编码不允许为空！";
    public static final String QUOTATION = "\"";

    public static final String MP4_SUFFIX = ".mp4";
    public static final String CENTER_LOCATION_CODE = "10000000001";

    public static final String LESS_THAN_EQUEALS = "<=";

    public static final String EMPTY_ARR = "[]";

    public static final String LOGIN_USER = "loginUser";
    public static final String IMPORT_DATA_NOT_NULL = "导入数据不能为空。";
    public static final int EXCEL_IMPORT_MAX_NUM = 1000;
    public static final String EXCEL_IMPORT_MAX_NUM_ERROR = "导入设备数量不支持超过1000条！";
}
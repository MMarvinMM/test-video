package com.chinatower.video.api.entity;

/**
 * 常量类
 */
public final class VideoConstants {
    public static final String ERROR_PRE = "";
    public static final String VALID_STATE = "0";
    public static final String COMMA = ",";
    public static final String NOT_EMPTY_STORAGE_TYPE ="存储类型不能为空";
    public static final String NOT_EMPTY_RECORD_TYPE ="录像类型不能为空";
    public static final String NOT_EMPTY_START_TIME ="开始时间不能为空";
    public static final String NOT_EMPTY_END_TIME ="结束时间不能为空";
    public static final String NOT_EMPTY_START_END_TIME ="开始时间和结束时间不能为空";
    public static final String NOT_EMPTY_PROTOCOL_TYPE ="协议类型不能为空";
    public static final String NOT_EMPTY_STREAM_UUID ="流UUID不能为空";
    public static final String NOT_EMPTY_STREAM_TYPE ="码流类型不能为空";
    public static final String NOT_EMPTY_NET_TYPE ="网域类型不能为空";
    public static final String NOT_EMPTY_WINDOW_LENGTH ="播放窗口长度像素值不能为空";
    public static final String NOT_EMPTY_WINDOW_WIDTH ="播放窗口宽度像素值不能为空";
    public static final String NOT_EMPTY_MID_POINTX ="拉框中心的横轴坐标像素值不能为空";
    public static final String NOT_EMPTY_MID_POINTY ="拉框中心的纵轴坐标像素值不能为空";
    public static final String NOT_EMPTY_LENGTHX ="拉框长度像素值不能为空";
    public static final String NOT_EMPTY_LENGTHY ="拉框宽度像素值不能为空";
    public static final String NOT_EMPTY_ZOOM_FLAG ="缩放标识不能为空";
    public static final String NOT_EMPTY_PLUG_ID = "插件ID不能为空";
    public static final String NOT_EMPTY_SWITCH_TYPE = "开关类型不能为空";
    public static final String NOT_EMPTY_IMAGE_UP_URL = "图片回传服务地址不能为空";
    public static final String NOT_EMPTY_IMAGE_NAME = "图片名称不能为空";
    public static final String NOT_EMPTY_STREAM_CONTROL_ID = "倍速回放流控制标识不能为空";
    public static final String NOT_EMPTY_CONTROL_TYPE = "控制类型不能为空";
    public static final String NOT_EMPTY_DEV_CODE = "设备编码不能为空";
    public static final String NOT_EMPTY_CHANNEL_CODE = "通道编码不能为空";
    public static final String NOT_EMPTY_PLUGIN_ID = "协议类型为铁塔私协时，插件ID不能为空";
    public static final String DOLOCKORRELEASEPTZ_OP = "获取云台控制权doLockOrReleasePtz ";
    public static final String DOLOCKORRELEASEPTZ_ERROR = "获取云台控制权失败 ";
    public static final String VIDEO_OPERATOR_NUM = "VIDEO_OPERATOR_NUM";
    public static final String SELECT_ROLE_BY_ID_OP = "获取租户权限selectRoleById";
    public static final String SELECT_ROLE_BY_ID_ERRO = "获取租户权限";

    public static final String SELECT_ENUM_INFO_OP = "获取视频格式字典表getEnumInfo";
    public static final String SELECT_ENUM_INFO_ERRO = "获取视频格式字典表";


    public static final String QUERY_VIDEO_LIST_OP = "查询通道录像列表queryVideoList";
    public static final String QUERY_VIDEO_LIST_ERRO = "查询通道录像列表失败";
    public static final String STORAGE_TYPE_ERRO = "查询通道录像列表方法存储类型范围错误";
    public static final String RECORD_TYPE_ERRO = "查询通道录像列表方法录像类型范围错误";
    //4.11.获取通道的录像回放流URL地址 getVideoPlaybackUrl
    public static final String GET_VIDEO_PLAY_BACK_URL_OP = "获取通道的录像回放流URL地址getVideoPlaybackUrl";
    public static final String GET_VIDEO_PLAY_BACK_URL_ERRO = "获取通道的录像回放流URL地址失败";

    public static final String GET_VIDEO_REAL_TIME_URL_OP = "获取通道实时流URL地址getVideoRealtimeUrl";
    public static final String GET_VIDEO_REAL_TIME_URL_ERRO = "获取通道实时流URL地址失败";

    public static final String THIRD_LOCATION_OP = "3D定位thirdLocation";

    public static final String DOWNLOAD_VIDEO_OP = "设备录像下载downloadVideo";

    public static final String DOWNLOAD_PROCESS_OP = "设备录像下载进度downloadProcess";

    public static final String STOP_DOWNLOAD_VIDEO_OP = "设备录像停止下载downloadVideo";
    public static final String THIRD_LOCATION_ERRO = "3D定位失败";

    public static final String GET_AUDIO_CHANNEL_OP = "获取语音对讲通道getAudioChannel";
    public static final String GET_AUDIO_CHANNEL_ERRO = "获取语音对讲通道失败";

    public static final String DOSETCHANNELPARAM = "doSetChannelParam";
    public static final String DO_SET_CHANNEL_PARAM_OP = "设置通道参数doSetChannelParam";
    public static final String DO_SET_CHANNEL_PARAM_ERRO = "设置通道参数失败";

    public static final String DO_SWITCH_LIGHT_OP = "开启关闭灯失败doSwitchLight";
    public static final String DO_SWITCH_LIGHT_ERRO = "开启关闭灯失败";

    public static final String SNAP_SHOT_OP = "抓图snapshot";
    public static final String SNAP_SHOT_ERRO = "抓图失败";

    public static final String PLAY_BACK_CONTROL_OP = "倍速回放playBackControl";
    public static final String PLAY_BACK_CONTROL_ERRO = "倍速回放失败";

    public static final String QUERY_RECORD_PLAN_STORAGE_DAY_OP = "获取视频存储时长queryRecordPlanStorageDay";
    public static final String QUERY_RECORD_PLAN_STORAGE_DAY_ERRO = "获取视频存储时长失败";

    public static final String QUERYPATROL = "queryPatrol";
    public static final String QUERY_PATROL_OP = "查询巡航路径queryPatrol";
    public static final String QUERY_PATROL_ERRO = "查询巡航路径失败";
    public static final String SELECT_LOG = "查询结果:";
    public static final String XPLUGID = "X-PlugId";
    public static final String XFRONTSTREAMUUID = "X-FrontStreamUuid";

    public static final String QUERY_VIDEO_LIST_TIME_ERRO = "查询通道录像列表时间转换失败";
    public static final String QUERYVIDEOLIST = "queryVideoList";
    //4.11.获取通道的录像回放流URL地址 getVideoPlaybackUrl
    public static final String GETVIDEOPLAYBACKURL = "getVideoPlaybackUrl";

    public static final String GETVIDEOREALTIMEURL = "getVideoRealtimeUrl";
    public static final String GET_VIDEO_REAL_TIME_URL_ERRO_TWO = "获取通道实时流URL地址失败";
    public static final String GETVIDEOREALTIMEURLS_INTERFACE = "批量获取通道实时流URL地址接口";
    public static final String GETVIDEOREALTIMEURLS = "getVideoRealtimeUrls";
    public static final String GETVIDEOREALTIMEURLS_ERRO = "批量获取通道实时流URL地址失败";

    public static final String THIRDLOCATION = "thirdLocation";
    public static final String GETAUDIOCHANNEL = "getAudioChannel";

    public static final String DOSWITCHLIGHT = "doSwitchLight";

    public static final String SNAPSHOT = "snapshot";

    public static final String PLAYBACKCONTROL = "playBackControl";

    public static final String QUERYPRESETLIST = "queryPresetList";
    public static final String QUERY_PRESET_LIST_OP = "查询预置点列表queryPresetList";
    public static final String QUERY_PRESET_LIST_ERRO = "查询预置点列表失败";

    public static final String QUERYGUARDLIST = "queryGuardList";
    public static final String QUERY_GUARD_LIST_OP = "查询看守位queryGuardList";
    public static final String QUERY_GUARD_LIST_ERRO = "查询看守位失败";

    public static final String QUERYPTZPOSITION = "queryPtzPosition";
    public static final String QUERY_PTZ_POSITION_OP = "查询PTZ位置queryPtzPosition";
    public static final String QUERY_PTZ_POSITION_ERRO = "查询PTZ位置失败";

    public static final String QUERYRECORDPLAN = "queryRecordPlan";
    public static final String QUERY_RECORD_PLAN_OP = "查询通道录像计划queryRecordPlan";
    public static final String QUERY_RECORD_PLAN_ERRO = "查询通道录像计划失败";

//    public static final String QUERYRECORDPLANFORPAGE = "queryRecordPlanForPage";
    public static final String QUERY_RECORD_PLAN_FOR_PAGE_OP = "查询全部录像计划queryRecordPlanForPage";
//    public static final String QUERY_RECORD_PLAN_FOR_PAGE_ERRO = "查询全部录像计划失败";

    public static final String REDIS_URL_ERROR ="接口调用地址redis异常";

    public static final String TOWERCODETEST = null;

    public static final String REQUEST_LOG = "请求报文:";
    public static final String RESPONSE_INTERFACE_LOG = "接口返回报文:";
    public static final String RESPONSE_LOG = "返回报文:";
    public static final String NO_AUTH = "当前用户未租用此设备";
    public static final String SUCCESS_MSG = "成功";
    public static final String PRESET_SUCCESS_MSG = "转动预置位成功";

    // 获取/释放云台控制权 对应方法 doLockOrReleasePtz
    public static final String LOCKPTZ = "doLockOrReleasePtz";
    public static final String LOCK_ERROR = "申请控制权限失败";
    public static final String LOCK_WAITING = "申请控制权限排队中";
    public static final String LOCKPTZ_OP = "获取/释放云台控制权 doLockOrReleasePtz ";

    // 云台转动信息 对应方法 doPtzCmds
    public static final String PTZCMDS = "doPtzCmds";
    public static final String PTZCMDS_OP = "云台转动 doPtzCmds ";
    public static final String PTZCMDS_ERROR = "云台转动操作失败";

    // 聚焦缩放 对应方法 doPtzCmds
    public static final String FOCUSORZOOM = "doCameraFocusOrZoom";
    public static final String FOCUSORZOOM_OP = "聚焦缩放 doCameraFocusOrZoom ";
    public static final String FOCUSORZOOM_ERROR = "聚焦缩放操作失败";

    // 停用/启用巡航路径 对应方法doPatrol
    public static final String DOPATROL = "doPatrol";
    public static final String DOPATROL_OP = "启用/停用巡航路径doPatrol ";
    public static final String DOPATROL_ERROR = "启用/停用巡航路径失败 ";

    // 批量停用/启用巡航路径 对应方法batchStartOrEndPatrol
    public static final String BATCHSTARTORENDPATROL = "batchStartOrEndPatrol";
    public static final String BATCH_START_OR_END_PATROL_OP = "批量停用/启用巡航路径batchStartOrEndPatrol ";
    public static final String BATCH_START_OR_END_PATROL_ERROR = "批量停用/启用巡航路径失败 ";

    // 根据通道查询设备信息 对应方法queryYgDeviceAndChannel
    public static final String QUERYYGDEVICEANDCHANNEL = "queryYgDeviceAndChannel";
    public static final String QUERY_YG_DEVICE_AND_CHANNEL_OP = "根据通道查询设备信息queryYgDeviceAndChannel ";
    public static final String QUERY_YG_DEVICE_AND_CHANNEL_ERROR = "根据通道查询设备信息失败 ";

    // 转到指定PTZ位置 对应方法 doPtzLocationCmd
    public static final String PTZLOCATIONCMD = "doPtzLocationCmd";
    public static final String PTZLOCATIONCMD_OP = "转到指定位置 doPtzLocationCmd ";
    public static final String APIREQ= "APIcontroller入参";
    public static final String CONTROLLERREQ= "前台controller入参";
    public static final String SDKREQ= "SDKcontroller入参";

    // 设置看守卫 对应方法doSetGuard
    public static final String DOSETGUARD = "doSetGuard";
    public static final String DO_SET_GUARD_OP = "设置看守卫doSetGuard ";
    public static final String DO_SET_GUARD_ERROR = "设置看守卫失败 ";

    // 查询算法状态 对应方法synchAlgorithmConfigStatus
    public static final String SYNCHALGORITHMCONFIGSTATUS = "synchAlgorithmConfigStatus";
    public static final String SYNCH_ALGORITHM_CONFIG_STATUS_OP = "查询算法状态synchAlgorithmConfigStatus ";
    public static final String SYNCH_ALGORITHM_CONFIG_STATUS_ERROR = "查询算法状态失败 ";

    // 设备/通道/预置点/监控区域/算法关系推送 对应方法synchDeviceAlgorithms
    public static final String SYNCHDEVICEALGORITHMS = "synchDeviceAlgorithms";
    public static final String SYNCH_DEVICE_ALGORITHMS_OP = "设备/通道/预置点/监控区域/算法关系推送synchDeviceAlgorithms ";
    public static final String DO_SELECT_FROM_TO_OP = "根据设备编码查询from-to信息getCasCadeByDeviceCode ";
    public static final String DO_SELECT_FROM_TO_BY_TASK_OP = "根据任务id查询from-to信息getCasCadeByTaskId ";
    public static final String DO_SELECT_FROM_TO_BY_CHANNEL_BATCH_OP = "根据任务id查询from-to信息getCasCadeByChannelCodeBatch ";
    public static final String DO_SELECT_FROM_TO_BATCH_OP = "根据设备编码批量查询from-to信息getCasCadeByDeviceCodeBatch ";
    public static final String SYNCH_DEVICE_ALGORITHMS_ERROR = "设备/通道/预置点/监控区域/算法关系推送失败 ";

    // 查询事件告警详情 对应方法getAlarmInfo
    public static final String GETALARMINFO = "getAlarmInfo";
    public static final String GET_ALARM_INFO_OP = "查询事件告警详情getAlarmInfo ";
    public static final String GET_ALARM_INFO_ERROR = "查询事件告警详情失败 ";

    // 批量删除巡航路径 对应方法batchDelPatrol
    public static final String BATCHDELPATROL = "batchDelPatrol";
    public static final String BATCH_DEL_PATROL_OP = "批量删除巡航路径batchDelPatrol ";
    public static final String BATCH_DEL_PATROL_ERROR = "批量删除巡航路径失败 ";

    // 删除巡航路径 对应方法delPatrol
    public static final String DELPATROL = "delPatrol";
    public static final String DEL_PATROL_OP = "删除巡航路径delPatrol ";
    public static final String DEL_PATROL_ERROR = "删除巡航路径失败 ";

    // 修改巡航路径 对应方法updateCruisePatrol
    public static final String UPDATECRUISEPATROL = "updateCruisePatrol";
    public static final String UPDATE_CRUISE_PATROL_OP = "修改巡航路径updateCruisePatrol ";
    public static final String UPDATE_CRUISE_PATROL_ERROR = "修改巡航路径失败 ";

    // 新增巡航路径 对应方法addCruisePatrol
    public static final String ADDCRUISEPATROL = "addCruisePatrol";
    public static final String ADD_CRUISE_PATROL_OP = "新增巡航路径addCruisePatrol ";
    public static final String ADD_CRUISE_PATROL_ERROR = "新增巡航路径失败 ";

    // 新增预置点 对应方法addCameraPreset
    public static final String ADDCAMERAPRESET = "addCameraPreset";
    public static final String ADD_CAMERA_PRESET_OP = "新增预置点addCameraPreset ";
    public static final String ADD_CAMERA_PRESET_ERROR = "新增预置点失败 ";

    // 修改预置点 对应方法updatePresetInfo
    public static final String UPDATEPRESETINFO = "updatePresetInfo";
    public static final String UPDATE_PRESET_INFO_OP = "修改预置点updatePresetInfo ";
    public static final String UPDATE_PRESET_INFO_ERROR = "修改预置点失败 ";

    // 删除预置点 对应方法delCameraPreset
    public static final String DELCAMERAPRESET = "delCameraPreset";
    public static final String DEL_CAMERA_PRESET_OP = "删除预置点delCameraPreset ";
    public static final String DEL_CAMERA_PRESET_ERROR = "删除预置点失败 ";

    // 批量删除预置点 对应方法batchDelCameraPreset
    public static final String BATCHDELCAMERAPRESET = "batchDelCameraPreset";
    public static final String BATCH_DEL_CAMERA_PRESET_OP = "批量删除预置点batchDelCameraPreset ";
    public static final String BATCH_DEL_CAMERA_PRESET_ERROR = "批量删除预置点失败 ";

    // 删除通道录像计划 对应方法deleteRecordPlan
    public static final String DELETE_RECORD_PLAN = "deleteRecordPlan";
    public static final String DELETE_RECORD_PLAN_OP = "删除通道录像计划deleteRecordPlan ";
    public static final String DELETE_RECORD_PLAN_ERROR = "删除通道录像计划失败 ";

    // 修改通道录像计划 对应方法updateRecordPlan
    public static final String UPDATE_RECORD_PLAN = "updateRecordPlan";
    public static final String UPDATE_RECORD_PLAN_OP = "修改通道录像计划updateRecordPlan ";
    public static final String UPDATE_RECORD_PLAN_ERROR = "修改通道录像计划失败 ";

    // 新增通道录像计划 对应方法addRecordPlan
    public static final String ADD_RECORD_PLAN = "addRecordPlan";
    public static final String ADD_RECORD_PLAN_OP = "新增通道录像计划addRecordPlan ";
    public static final String ADD_RECORD_PLAN_ERROR = "新增通道录像计划失败 ";

    // 计算可视域 对应方法calDeviceVisible
    public static final String CALDEVICEVISIBLE = "calDeviceVisible";
    public static final String CAL_DEVICE_VISIBLE_OP = "计算可视域calDeviceVisible ";
    public static final String CAL_DEVICE_VISIBLE_ERROR = "计算可视域失败 ";

    public static final String PTZLOCATIONCMD_ERROR = "转到指定位置操作失败";

    // 转到预置点 对应方法 doControlPreset
    public static final String CONTROLPRESET = "doControlPreset";
    public static final String CONTROLPRESET_OP = "转到预置点 doControlPreset ";

    public static final String CONTROLPRESET_NOREQUEST = "转到预置点 doControlPresetNoRequest ";
    public static final String CONTROLPRESET_ERROR = "转到预置点操作失败";

    // 设置通道参数 对应方法doSetChannelParam
    public static final String SETCHANNELPARAM = "doSetChannelParam";
    public static final String SETCHANNELPARAM_OP = "设置通道参数doSetChannelParam ";
    public static final String SETCHANNELPARAM_ERROR = "设置通道参数失败 ";
    public static final String XTOKEN = "X-Token";
    public static final String DEVICECODE = "deviceCode";
    public static final String CUSTOMERCODE = "X-CustomerCode";
    public static final String X_StreamUuid = "X-StreamUuid";
    public static final String XAPPCODE = "X-AppCode";
    public static final String XUSERCODE = "X-UserCode";
    public static final String XTOWERCODE = "X-TowerCode";
    public static final String XORDERCODE = "X-OrderCode";
    public static final String CHANNELCODE = "channelCode";
    public static final String ORDERCODE = "order_code";

    public static final String SERVICE_NAME_PREFIX = "apiInfo:";

    public static final String OPERATOR_TYPE = "1";

    public static final String PLAY_TYPE = "2";

    public static final String TWO_STR = "2";

    public static final String SUCCESS_CODE = "0000";

    public static final String SUCCESS_MESSAGE = "请求成功";

    public static final String FAIL_CODE = "8888";

    public static final String FAIL_MESSAGE = "请求失败";

    public static final String RESULT_CODE = "resultCode";

    public static final String RESULT_MSG = "resultMsg";

    public static final int ZERO = 0;

    public static final int ONE = 1;

    public static final int TWO = 2;

    public static final int FIVE = 5;

    public static final int TWO_HUNDRED = 200;
    public static final int TWO_HUNDRED_FOUR = 204;

    public static final int ERROR_4001 = 4001;

    public static final String DEL_PATROL_ERR = "该巡航计划正在启动中，无法删除，请巡航结束后再试。";

    public static final String PATROL_ID_NULL_ERR = "巡航路径id不能为空。";

    public static final String DEL_PATROL_STATUS_ERR = "有正在启动中的巡航计划，无法删除，重新选择。";

    public static final String OPERATORCOUNT = "调用记录操作接口：";

    public static final String FILE_URL = "fileUrl";

    public static final String ORIGIN_URL = "originUrl";

    private VideoConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String SNAP_SHOT_SIMPLE = "批量查询抓图getSnapShotSimple";

    public static final String SNAP_SHOT_SIMPLE_COLLECTION = "snapShotSimpleRes";

    public static final String SNAP_SHOT_SIMPLE_ERROR1 = "========上传简约模式抓图失败";
    public static final String SNAP_SHOT_SIMPLE_ERROR2 = "上传失败";

    public static final String TENANTID = "tenantId";

    public static final String LASTREQDATE = "lastReqDate";

    public static final String LASTURLDATE = "lastUrlDate";

    public static final String STATUS = "status";

    public static final String UUID = "uuid";

    public static final String URL = "url";

    public static final String START_TIME = "startTime";

    public static final String END_TIME = "endTime";

    public static final String QUERY_PLATFORM_LOCK_INFO = "queryPlatFormLockInfo接口入参";
    public static final String KEY_FOTMAT_CHANNEL_QUEUE = "channelQueue:%s:%s";
    public static final String DO_LOCK_PTZ = "doLockPtz";
    public static final String DURATION = "duration";
    public static final String PLATFORM_LOCK_TIME = "platformLockTime";
    public static final String PLATFORM_LOCK_TIME_WEICHUANG = "platformLockTimeWeichuang";
    public static final String PLATFORM_LOCK_TIME_996 = "996";
    public static final String PAUSE_DURATION_TIME = "cruise_pause_time";
    public static final String USERCODE = "userCode";
    public static final String CONTORL_TYPE = "controlType";
    public static final String X_LOCK_TOKEN = "X-LockToken";
    public static final String DO_LOCK_PTZ_NEW = "doLockPtzNew";
    public static final String DO_RELEASE_PTZ = "doReleasePtz";
    public static final String TOKEN = "token";
    public static final String CURRENT_PLATFORM_SURPLUS = "正在操控云台，剩余";

    public static final String DESENSITIZE_NAME = "***";
    public static final String DEAL_LOCK_REMINDER = "摄像机正在使用，请稍后操作。";
    public static final String SECOND_STOP = "s。";
    public static final String OPERATE_FAIL = "操作失败";
    public static final String X_DURATION = "X-Duration";
    public static final String GATEWAY_LOCKTOKENDATA = "com.chinatower.gateway.common.dto.LockTokenData";
    public static final String CONTROL_LOCKTOKENDATA = "com.chinatower.video.modules.videocontrol.dto.LockTokenData";
    public static final String REDIS_KEY = "------缓存中key为------";
    public static final String REDIS_VALUE = "------缓存中值为------";
    public static final String X_USERCODE_HEADER = "X-UserCode";
//    批量获取实时视频流个数
    public static final String enableChannelSize = "enableChannelSize";
//    批量获取实时视频流客户列表
    public static final String bath_VideoRealtimeUrl = "bath_VideoRealtimeUrl";

    public static final String FROM_WEICHUANG = "weichuang";

    public static final int INT_50 = 50;
    public static final String BATCH_NUM_LIMIT = "最多支持选择";
    public static final String BATCH_NUM_LIMIT_MAX = "条录像计划";
    public static final String PARAM_DATA_ERROR = "入参数据错误!";
    public static final String BATCH_DELETE_RECORD_PLAN = "batchDeleteRecordPlan";
    public static final String BATCH_DELETE_MAX_NUM = "maximum-number-of-batch-operations";
    public static final String TOWER_ACCOUT_CANNOT_DELETE = "铁塔账号无法进行录像计划删除操作";
    public static final String STOP_PLAN_DELETE = "请停用录像计划后再进行删除。";
    public static final String BATCH_DELETE_SUCCESS = "批量删除通道录像计划成功!";
    public static final String RECORD_PLAN_ID = "record_plan_id";
    public static final String INDUSTRY_CODE = "industry_code";
    public static final String INDUSTRYCODE = "industryCode";
    public static final String TOWER_ACCOUT_CANNOT_START_END = "铁塔账号无法进行录像计划停用启用操作";
    public static final String BATCH_START_END_RECORD_PLAN = "batchStartOrEndRecordPlan";
    public static final String BATCH_START_SUCCESS = "批量启用通道录像计划成功!";
    public static final String BATCH_END_SUCCESS = "批量停用通道录像计划成功!";
    public static final String BATCH_START_END_SUCCESS = "批量停用/启用通道录像计划成功!";
    public static final String BATCH_START_END_MAX_NUM = "startEndBatchMaxNum";
    public static final String batchStartEndRecordPlan_REQ = "batchStartEndRecordPlan--批量停用/启用通道录像计划(订单调用)请求报文------";
    public static final String PLAN_NOT_NULL = "通道录像计划列表不能为空!";
    public static final String BATCH_START_END_PLAN = "batchStartEndRecordPlan";
    public static final String ADMIN = "admin";
    public static final String EXCEPTION = "Exception:";
    public static final String BATCH_START_END_RECORD_PLAN_MAIN_START = "batchStartOrEndRecordPlan主方法------START------:";
    public static final String BATCH_START_END_RECORD_PLAN_MAIN_END = "batchStartOrEndRecordPlan主方法------END------:";
    public static final String RECORD_PLAN_COMMON_MAIN_START = "recordPlanCommon主方法2------START------:";
    public static final String RECORD_PLAN_COMMON_MAIN_END = "recordPlanCommon主方法2------END------:";
    public static final String MERGE_RECORD_PLAN_SYNC_START = "录像计划整合mergeRecordPlan异步方法------START------:";
    public static final String MERGE_RECORD_PLAN_SYNC_END = "录像计划整合mergeRecordPlan异步方法------END------:";


    public static final String PAUSE_RESUME_PATROL = "pauseResumePatrol";
    public static final String QUERY_PAUSE_RESUME_PATROL_INFO = "queryPauseResumePatrolInfo";
    public static final String PAUSERESUME_A_ = "PAUSERESUME_A_";
    public static final String REDIS_EXPIRE_KEY_LOG = "REDIS---过期KEY---：{}";
    public static final String UNDERLINE_STR = "_";
    public static final String OPERATE_ERROR_NOT_EXIST_PATROL = "操作失败，不存在该巡航数据!";
    public static final String ZERO_STR = "0";
    public static final String ONE_STR = "1";
    public static final String THREE_STR = "3";
    public static final String FOUR_STR = "4";
    public static final String FIVE_STR = "5";
    public static final String SIX_STR = "6";
    public static final String NINE_STR = "9";


    public static final String OPERATE_SUCCESS = "操作成功";
    public static final Long FOUR_LONG = 4L;
    public static final Integer INT_60 = 60;
    public static final String PAUSE_ERROR = "摄像机暂无巡航，无需暂停。";
    public static final String DEVICE_NOT_RESUME_STATUS_PATROL = "该设备下无暂停状态巡航数据，无需恢复！";

    public static final String DOUBLE_BRACKET = "{},{}";
    public static final String EQUAL_INFO = "===:{}";

    public static final String UPDATE_PRESET_SNAPSHOT_INFO = "updatePresetSnapshotInfo";
    public static final String UPDATE_PRESET_SNAPSHOT_INFO_SUCC = "预置位抓图设置成功";
    public static final String UPDATE_PRESET_NAME = "updatePresetName";
    public static final String UPDATE_PRESET_NAME_SUCC = "编辑预置位名称成功";
    public static final String PRESET_NAME_REPEAT = "预置位名称不可重复";
    public static final String PRESET_NAME_EXIST = "该预置位名称已存在，请重新输入。";
    public static final String DEL_CAMERA_PRESET_CHECK = "delCameraPresetCheck";
    public static final String GET_PRESET_DETAIL = "getPresetDetail";
    public static final String PTZ_P = "P";
    public static final String PTZ_T = "T";
    public static final String PTZ_Z = "Z";
    public static final String PRESET_SNAPSHOT_JOB_TIME = "预置位定时抓图执行时间：{}  {}  {}  {}";
    public static final String PRESET_SNAPSHOT_ALL_SIZE = "预置位定时抓图预置位总数量：";
    public static final String PARALLEL_PROCESS_SIZE = "并行处理的列表数量：";
    public static final String THREAD_PROCESS_SIZE = "线程处理数据量：";
    public static final String PRESET_PROCESS_INFO = "当前处理的预置位信息：";
    public static final String DO_CONTROL_PRESET_SNAPSHOT_FAIL = "doControlPresetSnapshot转到预置位并抓图失败";
    public static final String DO_CONTROL_PRESET_FAIL = "doControlPresetSnapshot转到预置位失败，预置位ID：{}，预置位名称：{}，失败信息：{}";
    public static final String STRING_200 = "200";
    public static final String STRING_400 = "400";
    public static final String DO_CONTROL_PRESET_WAIT = "doControlPresetWait";
    public static final String SNAPSHOT_TIME_JOB = "&snapshotTimeJob=";
    public static final String SNAPSHOT_TIME_JOB_PARAM = "snapshotTimeJob";
    public static final String PRESET_ID_FIELD = "presetId";
    public static final String DELETE_FLAG_FIELD = "deleteFlag";
    public static final String SNAPSHOT_TIME_FIELD = "snapshotTime";
    public static final String QUERY_PRESET_IMG_JOB = "queryPresetImgJob";
    public static final String QUERY_FAIL = "查询失败！";
    public static final String ZHUAN_PRESET_TIME = "doControlPresetSnapshot转到预置位，预置位ID：{}，预置位名称：{}，时间：{}";
    public static final String PRESET_SNAPSHOT_TIME = "doControlPresetSnapshot抓图，预置位ID：{}，预置位名称：{}，时间：{}";
    public static final String DATE_DD = "dd";
    public static final String VM_VIDEO_PRESET_SNAPSHOT = "vm_video_preset_snapshot";

    public static final String GET_VIDEO_ENCRYPT_REAL_TIME_URL_OP = "获取通道加密视频流URL地址getVideoRealtimeUrl";
    public static final String GET_VIDEO_ENCRYPT_REAL_PROTOCOL_TYPE = "获取通道加密视频流接口协议类型必须为铁塔私协！";
    public static final String GET_VIDEO_ENCRYPT_REAL_PLUGID = "获取通道加密视频流接口请求头参数必须传插件ID！";
    public static final String GET_VIDEO_ENCRYPT_REAL_ERROR = "获取通道加密视频流失败";
    public static final int SIX_NUM = 6;
    public static final String ALGO_TASK = "算法任务";
    public static final String PATROL_INFO = "、巡航计划";
    public static final String PATROL_MSG = "巡航计划";
    public static final String PATROL_WAY_MSG = "巡航路径";
    public static final String AR_TAG = "、AR标签";
    public static final String POLY_PATCH = "、图斑与电子围栏的绑定关系";
    public static final String CAESURA_SIGN = "、";
    public static final String THE_PRESET_SET = "该预置位配置了";
    public static final String NOT_DEL = "，目前无法删除。如要删除该预置位，请将预置位上配置的";
    public static final String DEL_TRY = "删除后再试。";
    public static final String PRESET_LIST_PTZ_PARSE_FAIL = "查询预置位列表PTZ转义失败：";

    public static final String NO_CONTROL_PERMISSION_TEMPORARILY = "暂无操控权限";
    public static final String INDUSTRU_CODE = "000000";
    public static final String TIME_PARSE_ERROR = "时间转换错误：";
    public static final String BLANK_SPACE = " ";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String GET_LOCK_TIME_PARSE_ERROR = "获取云台锁过期时长类型转换报错！";
    public static final int ONE_HUNDRED_TWENTY = 120;
    public static final String PRIORITY_CONPARE_ERROR = "优先级判断异常：";
    public static final String DEVICE_OPERATE_AUTH = "device_operate_auth";
    public static final String DEVICE_OPERATE_AUTH_TOWER = "device_operate_auth_tower";
    public static final String IF_CURRENT_OPERATE_PERIOD_COMMON = "ifCurrentInOperatePeriod查询返回参数：";
    public static final String IF_PRIORITY_QUERY_QESPONSE = "ifPriority查询返回参数：";
    public static final String IF_PRIORITY_QUERY_QESPONSE_AND = "======：";
    public static final String LOCK_REDIS_UPDATE = "云台锁REDIS更新信息为：";

    public static final String POINTS_VALID_ERROR = "该摄像机暂未标定，请联系铁塔管理员。";
    public static final String DEVICE_MODEL_ERROR = "获取摄像机型号信息失败";
    public static final String GETPTZBYMASSPOINTBATCH = "getPtzByMassPointBatch";
    public static final String POINTS_VALID = "pointsValid";
    public static final String POINTS_VALID_SUCC = "该摄像机已标定。";

    public static final String POINTS_VALID_BATCH = "pointsValidBatch";

    public static final String PAGE_SIZE_1000 = "pageSize限制最大是1000";
    public static final String QUERY_PRESET_LIST_IMG_JOB = "queryPresetListImgJob";
    public static final String IMG_URL_FIELD = "imgUrl";
    public static final String NETTYPE_PARAM_NOT_CONFIG = "网域类型参数未配置！";
    public static final String NETTYPE_PARAM_ERROR = "网域类型参数错误！";
    public static final String NETTYPE_PARAM = "realtimeUrl.netType";
    public static final String MERGE_PATROL_NAME = "整合巡航计划-";
    public static final String ID = "VideoPatrolPlanInfoService---巡航计划ID为：";
    public static final String DEVICE = "巡航计划设备为：";
    public static final String ID2 = "VideoRecordPlanInfoBatchService---巡航计划ID为：";
    public static final String PLATFORM_CODE = "platform_code";
    public static final String TO_APP_VIDEO = "base";

    public static final String TO_APP_YG = "newlandBootInner";
    public static final String TO_APP_AIOT = "aiot";
    public static final String TO_APP_RADAR = "radar";
    public static final String ADD_PATROL_ERROR = "新增巡航路径失败";
    public static final String DELETE_PATROL_ERROR = "删除巡航路径失败";
    public static final String UPT_PATROL_ERROR = "修改巡航路径失败";
    public static final String DO_PATROL_ERROR = "启用/停用巡航路径失败";
    public static final String TURN_TO_MSG = "请求参数不正确";

    public static final String DOWNLOAD_VIDEO = "批量下载录像downloadVideo";

    public static final String DOWNLOAD_VIDEO_PROCESS = "批量下载进度百分比查询downloadProcess";

    public static final String SNAP_AND_DOWNLOAD_VIDEO_START = "抓图snapshot/批量下载录像downloadVideo";

    public static final String SNAP_AND_DOWNLOAD_VIDEO_PROCESS = "抓图snapshot/批量下载录像downloadProcess";

    public static final String STOP_DOWNLOAD_VIDEO = "批量停止下载录像stopDownloadVideo";
    public static final String DOWNLOAD_ERROR = "下载错误";
    public static final String STOP_DOWNLOAD_ERROR = "停止下载错误";

    public static final String DOWNLOAD_PROCESS_ERROR = "下载进度查询错误";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String ITEM = "item";
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String PATROL_TYPE_START = "启用";
    public static final String PATROL_TYPE_STOP = "停用";
    public static final String PATROL_WAY_STOP_SUCCESS_MSG = "停用巡航路径成功!";
    public static final String SUCCESS_STATUS_MSG = "成功";
    public static final String FAIL_STATUS_MSG = "失败";
    public static final String START_SUCCESS_MSG = "启动成功";
    public static final String NOT_IN_TIME_MSG = "当前不在";

    public static final String ADD_BASIC_ERR_MSG = "添加基础报错信息记录异常";

    public static final String AGAIN_TRY_MSG = "失败重试！";

    public static final String POINT_X = "pointX";

    public static final String POINT_Y = "pointY";

    public static final String TAG_CLASS_101 = "101";
    public static final String TAG_CLASS_102 = "102";
    public static final String TAG_CLASS_103 = "103";
    public static final String TAG_CLASS_104 = "104";
    public static final String TAG_CLASS_105 = "105";
    public static final String TAG_CLASS_106 = "106";
    public static final String TAG_CLASS_107 = "107";
    public static final String TAG_CLASS_108 = "108";
    public static final String TJH_LOGIN_TOKEN = "tjh_login_token";

    public static final String DEVICE_INFO_DETAIL = "device_info_detail";

    public static final String ALGORITHM_CODE = "algorithmCode";

    public static final String TASK_ID = "taskId";

    public static final String WARNING_ORDER_ID = "warningOrderId";

}

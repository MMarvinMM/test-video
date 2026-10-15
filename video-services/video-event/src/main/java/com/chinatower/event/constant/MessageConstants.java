package com.chinatower.event.constant;

/**
 * 消息类
 */
public final class MessageConstants {
    public static final String NO_RULE_MESSAGE = "当前用户没有数据权限";
    public static final String NO_ORDER_MESSAGE = "无法查询工单信息";
    public static final String ORDER_ERROR_MESSAGE = "生成告警工单失败";
    public static final String ORDER_JUDGED = "当前告警已经研判完成，请核查";
    public static final String THIRD_ERROR_MESSAGE = "===同步第三方平台 error";
    public static final String JUDGED_FAIL_MESSAGE = "运营研判失败！";
    public static final String JUDGED_SUCCESS_MESSAGE = "研判成功！";
    public static final String ALARM_NO_DATA = "当前编码：{0},无法查询告警信息";
    public static final String INTERCEPT_FAIL_MESSAGE = "操作失败";
    public static final String INTERFACE_613_ERROR = "调用6.13异常: ";
    public static final String OPERATE_TEMP_ERROR = "保存运营研判真警临时表异常：";
    public static final String ALL_INTERCEPT_MESSAGE = "告警推送全部拦截";
    public static final String MESSAGE_PUSH_ERROR_MESSAGE = "告警消息推送异常：";
    public static final String JUDGED_MESSAGE_PUSH_ERROR_MESSAGE = "运营研判消息推送异常：";
    public static final String NO_CUSTOMER_MESSAGE = "无法获取租户信息";
    public static final String AMAP_ERROR_MESSAGE = "高德API获取地址失败: ";
    public static final String AMP_COORDINATE_CONVERT_MESSAGE = "通过经纬度调用高德API得到火星坐标系——GCJ-02失败：";
    public static final String GRID_MESSAGE = "通过经纬度获取网格，返回结果： {0}";
    public static final String GRID_REQ_MESSAGE = "通过经纬度获取网格，请求入参： {0}";
    public static final String GRID_ERROR_MESSAGE = "通过经纬度获取网格接口异常: ";
    public static final String ALARM_TYPE_PARAM_ERROR_MESSAGE = "参数错误：alarmType为空";
    public static final String PUSH_THIRD_MESSAGE = "===同步第三方平台 {0} ===";
    public static final String START = "start";
    public static final String END = "end";
    public static final String ERROR = "error";
    public static final String RECEIVE_ALARM_ERROR_MESSAGE = "接收告警异常: ";
    public static final String LOG_NOTICE_MODE = "通知方式：";
    public static final String LOG_EVENT_INFO = "事件信息：";
    public static final String LOG_AI = "非人工上报 查询组织下部门的组织id 入参：";
    public static final String LOG_UNAI = "人工上报 查询组织下部门的组织id 入参：";
    public static final String ORG_RESULT = "查询组织下部门的组织id 结果：";
    public static final String MSG = "{0},在{1}发现{2}，请登录系统及时处理！";
    public static final String LOG_MESSAGEPUSH_PARAM = "告警消息推送入参：";

    public static final String NO_USER_INFO = "无法获取当前登录用户信息，请重新登陆";
    public static final String WARNING_ORDER_ID_MSG = "warningOrderId不能为空！";
    public static final String ALARM_TIME_MSG = "alarmTime不能为空！";
    public static final String WARNING_ORDER_MSG = "未查询到告警数据！";
    public static final String WARNING_ORDER_COLLECTION = "未查询到收藏数据！";
    public static final String NATIVE_FILE = "加载native文件 :";
    public static final String NATIVE_FILE_DELETE = "native文件删除失败 :";
    public static final String NATIVE_ERROR = "失败!!请确认操作系统是X86还是X64!!!";
    public static final String WARNING_ORDER_ERROR = "告警工单状态异常或不存在!";
    public static final String WARNING_ORDER_FLOW_ERROR = "告警工单环节异常!";
    public static final String PARK_NAME_ERROR = "公园名称重复!";
    public static final String PARK_PEOPLE_ERROR = "通过人流量监控告警获取当前人数异常";

    public static final String ALARM_TAG_NAME_ERROR = "告警类型标签名称重复!";
    public static final String ALARM_TAG_ERROR = "告警类型标签不存在!";
    public static final String ALARM_TAG_REL_ERROR = "当前标签已关联告警类型!";
    public static final String ALARM_TAG_NUM_ERROR = "标签不能超过20个!";
    public static final String ALARM_TAG_REL_SAVE_ERROR = "保存失败,告警类型标签全部无效!";


    public static final String ALARM_LABLE_NAME_ERROR = "标签名称重复，无法添加！";
    public static final String ALARM_LABLE_ERROR = "告警标签不存在!";
    public static final String ALARM_LABLE_NUM_ERROR = "标签不能超过20个!";
    public static final String ALARM_LABLE_SUCCESS =  "告警标签新增成功!";
    public static final String ALARM_LABLE_MOD_SUCCESS =  "告警标签修改成功!";
    public static final String ALARM_LABLE_DEL_SUCCESS =  "告警标签删除成功!";
    public static final String ALARM_LABLE_JOIN_SUCCESS =  "告警关联标签成功!";
    public static final String ALARM_LABLE_DEL_MSG =  "其他告警事件已选择此标签，暂时无法删除";

    public static final String ALARM_NO_PARAMS = "传入参数为空";
    public static final String ALARM_NO_TENANT = "未找到租户订购信息";

    public static final String WEATHER_ERROR_MESSAGE = "和风天气API获取天气信息失败: ";
    public static final String PTZ_ERROR_MESSAGE = "PTZ转换异常: ";
    public static final String FLOW_LINK_EXCEPTION = "查询流程开始环节异常";
    public static final String FLOW_LINK_ATTR_EXCEPTION = "查询流程环节属性异常";
    public static final String FLOW_VIEW_EXCEPTION = "查询流程视图模板异常";
    public static final String EVENT_PROCESS_COMMONWORDS_MSG = "常用语修改成功！";



}

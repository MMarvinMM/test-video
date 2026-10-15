package com.chinatower.platform.constants;

/**
 * Created by chenmf on 2022/1/18.
 */
public class OrderConstants {

    /** 正常状态 */
    public static final int DELETE_FLAG_NORMAL = 0;

    /** 删除状态 */
    public static final int DELETE_FLAG_EXCEPTION = 1;

    /** 正常状态 */
    public static final String DELETE_FLAG_NORMAL_CHAR = "0";

    /** 删除状态 */
    public static final String DELETE_FLAG_EXCEPTION_CHAR = "1";


    /** 是否租户管理员(0否,1是) */
    public static final String IS_NOT_TENANT_ADMIN = "0";
    public static final String IS_TENANT_ADMIN = "1";

    /** 4-摄像头,10-雷达,8-传感器 11-无人机 12-喇叭*/
    public static final int CATEGORY_DEVICE_CODE = 4;
    public static final int CATEGORY_RADAR_CODE = 10;
    public static final int CATEGORY_SENSOR_CODE = 8;

    public static final int CATEGORY_UAV_CODE = 11;

    public static final int CATEGORY_HORN_CODE = 12;

    public static final int CATEGORY_RECORDER_CODE = 13;

    public static final int CATEGORY_UAV_NEST_CODE = 15;

    //告警来源: 1-AI上报； 2-人工上报；3-一键告警
    public static final String WARNING_SOURCE_2 = "2";
    //告警来源: 1-AI上报； 2-人工上报；3-一键告警
    public static final String WARNING_SOURCE_3 = "3";

    public static final String CATEGORY_SATELLITE_SERVICE_CODE = "9";

    /**
     * 9-卫星
     */
    public static final int CATEGORY_SATELLITE_CODE = 9;

    /** 订购推送物联设备类型 1-雷达,2传感器 3-云广播*/
    public static final int ORDER_RADAR_CODE = 1;
    public static final int ORDER_SENSOR_CODE = 2;

    public static final int ORDER_HORN_CODE = 3;

    /**
     *订购关系变更状态：
     * 0：变更
     * 1：新增订购
     * 2：订单续订、启用（订购失效或停用后此状态才可生效）
     * 3：退订（退订后不可恢复）
     * 4：停用（欠费停用） 、失效
     */
    public static final int STATUS_FOR_UPDATE = 0;
    public static final int STATUS_FOR_ADD = 1;
    public static final int STATUS_FOR_ENABLE = 2;
    public static final int STATUS_FOR_DELETE = 3;
    public static final int STATUS_FOR_DEACTIVATE = 4;

    /** 订购关系类型：1-非精准转非精准 2-非精准转精准 3-精准转非精准 4-精准转精准 */
    public static final int ORDER_TYPE_NON_PRECISE_TO_NON_PRECISE = 1;
    public static final int ORDER_TYPE_NON_PRECISE_TO_PRECISE = 2;
    public static final int ORDER_TYPE_PRECISE_TO_NON_PRECISE = 3;
    public static final int ORDER_TYPE_PRECISE_TO_PRECISE = 4;

    /** 算法配置类型：1-摄像机算法配置 2-无人机算法配置 */
    public static final String ALGORITHM_CONFIG_TYPE_CAMERA = "1";

    public static final String DATE_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String SYS_GIVE_ALGORITHM = "sys_give_algorithm";

    public static final String SYS_LOCALIZATION_STORAGE="sys_localization_storage";

    /**
     * Redis前缀
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    public static final String _DEVLIST = "_devList";
    public static final String _CHNLIST = "_chnList";
    //api配置开启
    public static final String API_OPEN = "1";
    //api配置关闭
    public static final String API_CLOSE = "0";
    //客户状态失效
    public static final String CUSTOMER_STATUS_0 = "0";
    //客户状态有效
    public static final String CUSTOMER_STATUS_1 = "1";
    //订购失效时间
    public static final String _INVALIDTIME = "_invalidTime";
    //api配置开启
    public static final String ISOPEN = "isOpen";
    //客户编码
    public static final String CUSTOMERCODE = "customerCode";
    //客户状态
    public static final String CUSTOMERSTATUS = "customerStatus";

    /** 数据库字段常量 */
    public static final String FIELD_ORDER_CODE = "order_code";
    public static final String FIELD_PACKAGE_CODE = "package_code";
    public static final String FIELD_DELETE_FLAG = "delete_flag";




}

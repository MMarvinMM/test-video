package com.chinatower.common.core.enums;

/**
 * @Auther: guyuefei
 * @Date: 2022/05/24 09:10
 * @Description:
 *      告警来源枚举
 */
public enum WarningSourceEnum {

    /** *********** 获取流程配置使用的告警来源类型转换 ********** **/
    /** *********** 1：AI上报默认流程 ；2 ：人工上报默认流程 ；3：一键告警默认流程 ********** **/

    /**
     * 告警来源 算法AI告警 AI上报
     */
    AI_ALARM("1","算法AI告警","1"),


    /**
     * 告警来源 人工上报 人工上报流程
     */
    ARTIFICIAL("2", "人工上报", "2"),

    /**
     * 告警来源 无人机告警 一键告警默认流程
     */
    UAV_ALARM("3","无人机告警","3"),

    /**
     * 告警来源 摄像机算法告警 一键告警默认流程
     */
    CAMERA_ALARM("4","摄像机算法告警","3"),

    /**
     * 告警来源 车辆卡口摄像机告警 不走流程
     */
    VEHICLE_ALARM("5","车辆卡口摄像机告警","-1"),

    /**
     * 告警来源 人脸卡口摄像机告警 一键告警默认流程
     */
    MAN_FACE_ALARM("6","人脸卡口摄像机告警","3"),

    /**
     * 告警来源 行业告警
     */
    INDUSTRY_ALARM("7","行业告警","1"),

    /**
     * 告警来源 物联网告警 一键告警默认流程
     */
    IOT_ALARM("8","物联网告警","3"),

    /**
     * 告警来源 卫星告警
     */
    SATELLITE_ALARM("9","卫星告警","1"),

    /**
     * 告警来源 雷达告警 AI上报
     */
    RADAR_ALARM("10","雷达告警","1"),

    /**
     * 告警来源 无人机告警
     */
    UAV_DEVICE_ALARM("12","无人机设备告警","1"),

    /**
     * 告警来源 摄像机算法告警 客户管理-运营研判 使用
     */
    CAMERA_ALARM_KHGL("4","摄像机告警-告警类型","3"),

    /**
     * 告警来源 算法AI告警 客户管理-运营研判 使用
     */
    AI_ALARM_KHGL("1","AI告警-告警类型","1"),

    THIRD_ALARM("11","第三方告警","1"),

    /**
     * 告警来源 卫星告警 客户管理-运营研判 使用
     */
    SATELLITE_ALARM_KHGL("9","卫星告警-告警类型","1");



    /**
     * 告警来源 值
     */
    private String typeValue;

    /**
     * 告警来源 名称
     */
    private String typeName;

    /**
     * 告警来源 查询流程配置使用的来源类型
     */
    private String flowType;

    WarningSourceEnum(String typeValue, String typeName, String flowType) {
        this.typeValue = typeValue;
        this.typeName = typeName;
        this.flowType = flowType;
    }

    /**
     * 查询告警来源匹配的流程配置
     */
    public static String getFlowType (String typeValue){
        for (WarningSourceEnum warningSourceEnum: WarningSourceEnum.values()) {
            if(warningSourceEnum.getTypeValue().equals(typeValue)){
                return String.valueOf(warningSourceEnum.getFlowType());
            }
        }
        return String.valueOf(-1);
    }

    public static String getTypeName(String typeValue) {
        for (WarningSourceEnum warningSourceEnum: WarningSourceEnum.values()) {
            if(warningSourceEnum.getTypeValue().equals(typeValue)){
                return warningSourceEnum.getTypeName();
            }
        }
        return AI_ALARM.getTypeName();
    }

    public String getTypeValue() {
        return typeValue;
    }

    private void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeName() {
        return typeName;
    }

    private void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFlowType() {
        return flowType;
    }

    private void setFlowType(String flowType) {
        this.flowType = flowType;
    }
}

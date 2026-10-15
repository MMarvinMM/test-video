package com.chinatower.receive.core.util;


/**
 * @Description 告警类型对应处理策略枚举
 * @Author lyy
 * @Date 2024/8/6 9:49
 * @Version release_v1.6.90
 **/
public enum AlarmStrategyEnum {


    /**
     * 告警来源 AI告警
     */
    AI_ALARM("1","aiAlarmStrategy"),


    /**
     * 告警来源 人工上报
     */
    ARTIFICIAL("2", "humanAlarmStrategy"),

    /**
     * 告警来源 无人机告警
     */
    UAV_ALARM("3","uavAlarmStrategy"),

    /**
     * 告警来源 摄像机算法告警
     */
    CAMERA_ALARM("4","cameraAlgorithmAlarmStrategy"),

    /**
     * 告警来源 车辆卡口摄像机告警
     */
    VEHICLE_ALARM("5","checkpointAlarmStrategy"),

    /**
     * 告警来源 人脸卡口摄像机告警
     */
    MAN_FACE_ALARM("6","faceAlarmStrategy"),

    /**
     * 告警来源 行业告警
     */
    INDUSTRY_ALARM("7","industryTradeAlarmStrategy"),

    /**
     * 告警来源 物联网告警
     */
    IOT_ALARM("8","iotAlarmStrategy"),

    /**
     * 告警来源 卫星告警
     */
    SATELLITE_ALARM("9","satelliteAlarmStrategy"),

    /**
     * 告警来源 雷达告警
     */
    RADAR_ALARM("10","radarAlarmStrategy"),

    /**
     * 告警来源 无人机告警
     */
    UAV_DEVICE_ALARM("12", "uavAlarmStrategy");

    /**
     * 告警来源 值
     */
    private String typeValue;

    /**
     * 处理告警策略类
     */
    private String alarmStrategyBean;

    AlarmStrategyEnum(String typeValue, String alarmStrategyBean) {
        this.typeValue = typeValue;
        this.alarmStrategyBean = alarmStrategyBean;
    }
    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getAlarmStrategyBean() {
        return alarmStrategyBean;
    }

    public void setAlarmStrategyBean(String alarmStrategyBean) {
        this.alarmStrategyBean = alarmStrategyBean;
    }

    /**
     * 根据传入告警类型返回处理bean
     * @param typeValue
     * @return
     */
    public static String jdugeAlarmTypeAndGetStrategyBean(String typeValue){
        for (AlarmStrategyEnum alarmStrategyEnum : AlarmStrategyEnum.values()){
            if(alarmStrategyEnum.getTypeValue().equals(typeValue)){
                return alarmStrategyEnum.getAlarmStrategyBean();
            }
        }
        return AI_ALARM.getAlarmStrategyBean();
    }

}

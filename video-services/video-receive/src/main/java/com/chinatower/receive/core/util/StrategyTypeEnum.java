package com.chinatower.receive.core.util;


/**
 * @Description 告警压制策略方式枚举类
 * @Author lyy
 * @Date 2024/08/30 10:39
 * @Version release_v1.6.90
 **/
public enum StrategyTypeEnum {

    TIME_PERIOD_STRATEGY("时间段压制", "TimePeriodSuppressionStrategy", "timePeriodStrategy"),
    CONFIDENCE_STRATEGY("置信度压制", "ConfidenceSuppressionStrategy", "confidenceStrategy"),
    REPEATED_ALARM_STRATEGY("重复告警压制", "RepeatedAlarmSuppressionStrategy" ,"repeatedAlarmStrategy"),
    AREA_SHIELDING_STRATEGY("区域屏蔽压制", "AreaShieldingSuppressionStrategy", "areaShieldingStrategy"),
    ALGORITHM_THRESHOLD_STRATEGY("算法阈值压制", "AlgorithmThresholdSuppressionStrategy", "algorithmThresholdStrategy"),
    /*RAINY_MODE_STRATEGY("雨天模式压制", "rainyModeStrategy"),
    JUDGMENT_STRATEGY("研判压制", "judgmentStrategy"),*/
    UAV_AREA_SHIELDING_STRATEGY("无人机告警区域压制", "UavAreaShieldingSuppressionStrategy", "uavAreaShieldingStrategy"),
    UAV_FLY_TIME_STRATEGY("无人机起飞识别压制", "UavFlyTimeSuppressionStrategy", "uavFlyTimeStrategy"),

    WX_TIME_PERIOD_STRATEGY("卫星时间段压制", "TimePeriodSuppressionStrategy", "timePeriodStrategy"),
    WX_CONFIDENCE_STRATEGY("卫星置信度压制", "ConfidenceStrategyBySatellite", "wxConfidenceStrategy"),
    WX_REPEATED_ALARM_STRATEGY("卫星重复告警压制", "RepeatedAlarmStrategyBySatellite", "wxRepeatedAlarmStrategy"),
    ;

    private String displayName;

    private String instanceName;

    private String strategyTypeBean;

    StrategyTypeEnum(String displayName, String instanceName, String strategyTypeBean) {
        this.displayName = displayName;
        this.strategyTypeBean = strategyTypeBean;
        this.instanceName = instanceName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getStrategyTypeBean() {
        return strategyTypeBean;
    }

    public static StrategyTypeEnum fromDisplayName(String displayName) {
        for (StrategyTypeEnum type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }

}

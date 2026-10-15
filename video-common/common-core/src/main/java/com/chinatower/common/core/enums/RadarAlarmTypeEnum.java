package com.chinatower.common.core.enums;



/**
 * 雷达告警类型枚举类
 */
public enum RadarAlarmTypeEnum {


    /**
     * AIS变更告警
     */
    AIS_CHANGE_ALARM("LDGJ-AISBGGJ",22),

    /**
     * AIS关闭告警
     */
    AIS_CLOSE_ALARM("LDGJ-AISGBGJ",23),

    /**
     * 闯出告警
     */
    CC_ALARM("LDGJ-CCGJ",12),

    /**
     * 闯入告警
     */
    CR_ALARM("LDGJ-CRGJ",11),

    /**
     * 低速告警
     */
    DS_ALARM("LDGJ-DSGJ",13),

    /**
     * 单拖告警
     */
    DT_ALARM("LDGJ-DTGJ",5),

    /**
     * 高速告警
     */
    GS_ALARM("LDGJ-GSGJ",17),

    /**
     * 过线告警
     */
    GX_ALARM("LDGJ-GXGJ",16),

    /**
     * 黑名单告警
     */
    HMD_ALARM("LDGJ-HMDGJ",21),

    /**
     * 碰撞预警
     */
    PZ_ALARM("LDGJ-PZYJ",32),

    /**
     * 双拖告警
     */
    ST_ALARM("LDGJ-STGJ",4),

    /**
     * 套牌船告警
     */
    TPC_ALARM("LDGJ-TPCGJ",33),

    /**
     * AIS开启告警
     */
    AIS_ADD_ALARM("LDGJ-AISKQGJ",2),

    /**
     * 滞留告警
     */
    ZL_ALARM("LDGJ-ZLGJ",18);



    /**
     * 告警类型 值
     */
    private String alarmTypeValue;

    /**
     * 告警类型 名称
     */
    private int alarmCode;

    RadarAlarmTypeEnum(String alarmTypeValue, int alarmCode){
        this.alarmCode =alarmCode;
        this.alarmTypeValue = alarmTypeValue;
    }

    public String getAlarmTypeValue() {
        return alarmTypeValue;
    }

    public void setAlarmTypeValue(String alarmTypeValue) {
        this.alarmTypeValue = alarmTypeValue;
    }

    public int getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
    }
}

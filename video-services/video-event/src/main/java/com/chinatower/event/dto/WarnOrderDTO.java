package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WarnOrderDTO
 * @Deacription 告警对象
 * @Author chenmf
 * @Date 2022/3/29 15:02
 * @Version 1.0
 **/
@Data
public class WarnOrderDTO implements Serializable {
    
    //告警类型值
    private int alarmType;
    //告警时间
    private String alarmTime;
    //告警内容
    private String alarmBody;
    //设备编码
    private String deviceCode;
    //设备名称
    private String deviceName;
    //事件源经度
    private String longitude;
    //事件源纬度
    private String latitude;
    //告警编码
    private String alarmCode;
    //报警参数
    private String alarmParam;
    //报警参数值
    private String alarmParamValue;
    //告警来源
    private int alarmSource;
    /**
     * 行业信息id
     */
    private String tradeId;
    
    /**
     * 行业版本信息
     */
    private String tradeVerCode;
    
}

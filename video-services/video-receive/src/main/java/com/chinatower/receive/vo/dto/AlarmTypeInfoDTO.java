package com.chinatower.receive.vo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AlarmTypeInfoDTO
 * @Deacription 告警信息
 * @Author chenmf
 * @Date 2022/6/8 14:53
 * @Version 1.0
 **/
@Data
public class AlarmTypeInfoDTO implements Serializable {
    
    // 告警类型ID
    private Long alarmTypeId;
    
    //类型名称
    private String typeName;
    
    //类型值
    private String typeValue;
    
    //是否赠送 0:否  1:是
    private String ifFree;
    
    //图标
    private String icon;
}

package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 事件告警类型对象
 *
 * @return 
 * @author chenmf
 * @date 2022/3/28
 * @version
 */
@Data
public class AlarmTypeData implements Serializable {
    
    /**
     * 事件告警编码
     */
    private String alarmTypeCode;
    
    /**
     * 事件告警名称
     */
    private String alarmTypeName;
    
    /**
     * 1-新增；2-修改； 3-删除
     */
    private int optType;

}

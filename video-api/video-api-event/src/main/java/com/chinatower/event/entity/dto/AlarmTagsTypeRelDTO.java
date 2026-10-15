package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AlarmTagsTypeRelDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/4/26 17:20
 * @Version 1.0
 **/
@Data
public class AlarmTagsTypeRelDTO implements Serializable {
    
    private String alarmTypeId;
    private String id;
    private String isRel = "0";
    private Long tagId;
    private String tagName;
    private String typeValue;
}

package com.chinatower.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalQueryVO implements Serializable {

    private String eventName;

    private List<String> eventGradeDictList;
    private List<String> smallCategoryList;
    private String creator;
    private String createTimeStart;
    private String createTimeEnd;
    private String modifier;
    private String updateTimeStart;
    private String updateTimeEnd;
    private String tenantId;
    private String industryCode;
    private Integer pageNum;
    private Integer pageSize;
    /**
     * 是否是铁塔视角  1-是 0-不是
     */
    private Integer isTower;
    /**
     * desc  asc
     */
    private String sort;
    /**
     * //排序字段
     */
    private String sortName;

    private String digitalId;

    private String stageId;

    private String acceptNode;

}

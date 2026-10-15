package com.chinatower.industry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DevicesStatisticsResVO implements Serializable {
    //摄像机总数
    private String totalCnt;

    //在线摄像机数
    private String onlineCnt;

    //组织编码list
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DevicesDeptVO> deptList;

}

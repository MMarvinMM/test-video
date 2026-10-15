package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OilGasPipelinesVO {

    //油气管线唯一标识
    private String oilGasId;

    //颜色
    private String color;

    //告警方向
    private List<DirectionVO> direction;

    //管线粗细
    private String width;

    //箭头颜色
    private String arrowColor;

    //箭头粗细
    private String arrowWidth;

    /**
     * 场景编码 逗号分隔
     */
    private String sceneCode;

    /**
     * 是否回显 0:否
     */
    private String isShow;
}

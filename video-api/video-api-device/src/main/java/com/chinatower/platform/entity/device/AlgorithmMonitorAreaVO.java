package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmMonitorAreaVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmMonitorAreaVO {
    /** 监控区域ID */
    String monitorAreaId;
    /** 监控区域点 */
    List<AlgorithmMonitorPointVO> moniPointList;
    /** 2:基准区域，1：屏蔽区域，0：监控区域，默认为否 */
    private int monitorType;
    /** 经度 */
    String longitude;
    /** 纬度 */
    String latitude;
    /**
     * 填充颜色
     */
    String color;
    /**
     * 边框粗细
     */
    String frameWidth;
    /**
     * 边框颜色
     */
    String frameColor;

    /**
     * 场景编码 逗号分隔
     */
    String sceneCode;

    /**
     * 是否回显 0:否
     */
    String isShow;
}
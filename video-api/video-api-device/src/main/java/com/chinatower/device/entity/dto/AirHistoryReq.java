package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 16:05
 */
@Data
public class AirHistoryReq extends PageDTO {
    /**
     * 任务编码
     */
    private String taskRecordId;
    /**
     * 设备编码 优先机巢
     */
    private String deviceCode;
    /**
     * 航线id集合
     */
    private List<String> flightRouteId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 镜头类型
     */
    private Integer picType;
    /**
     * 受理节点
     */
    private String acceptNode;
    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 视频开始时间
     */
    private String videoStartTime;
    /**
     * 是视频结束时间
     */
    private String videoEndTime;
    /**
     * 视频时长
     */
    private Double duration;
    /**
     * 分页
     */
    private Integer pageNo;
    /**
     * 分页
     */
    private Integer pageSize;
    /**
     * 分页
     */
    private Integer page;
    /**
     * 分页
     */
    private Integer limit;
    /**
     * 排序
     */
    private Integer sort;
}

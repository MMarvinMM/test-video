package com.chinatower.airline.dto.request;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 16:05
 */
@Data
public class AirResultTaskReq extends PageDTO {
    /**
     * 节点
     */
    private String accessNode;
    /**
     * 任务编码
     */
    private String taskRecordId;
    /**
     * 查询条件  任务编码集合
     */
    private List<String> taskRecordIds;
    /**
     * 设备编码(优先机巢)
     */
    private String deviceCode;
    /**
     * 无人机编码
     */
    private String uavCode;
    /**
     * 航线id集合 筛选条件
     */
    private List<String> flightRouteId;
    /**
     * 航线名称 模糊查询
     */
    private String flightRouteName;
    /**
     * 任务名称 模糊查询
     */
    private String taskName;
    /**
     * 拍照类型 原版筛选
     */
    private String picType;
    /**
     * 拍照类型 新版本筛选
     */
    private List<String> picTypes;
    /**
     * 照片id筛选
     */
    private List<String> photoIds;
    /**
     * 接入节点
     */
    private String acceptNode;
    /**
     * 视频开始时间
     */
    private String videoStartTime;
    /**
     * 视频结束时间
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
     * 分页 飞控
     */
    private Integer page;
    /**
     * 分页 飞控
     */
    private Integer limit;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 排序规则
     */
    private String order;
    /**
     * 是否收藏 筛选  默认全部
     */
    private String isCollect;
    /**
     * 是否一键起飞筛选  默认全部
     */
    private String isReFly;
    /**
     * 一键起飞标识
     */
    private String refly;
    /**
     * 执行状态（0-全部 1-执行成功 2-执行失败）
     */
    private String taskStatus;
    /**
     * 执行开始时间
     */
    private String executeStartTime;
    /**
     * 执行结束时间
     */
    private String executeEndTime;
    /**
     * 照片开始时间
     */
    private String createStartTime;
    /**
     * 照片结束时间
     */
    private String createEndTime;
    /**
     * 照片名称筛选
     */
    private String photoName;
    /**
     * 视频名称筛选
     */
    private String videoName;
    /**
     * 复飞原始任务id 筛选复飞照片需要
     */
    private String originalTaskRecordId;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 识别地点3米范围误差
     */
    private Double rangeValue;
    /**
     * 照片算法编码筛选
     */
    private List<String> algorithmCodes;
    /**
     * 照片生成的告警筛选
     */
    private List<String> picSeIds;
    /**
     * 飞行成果物 假删除查询标识
     */
    private String flag;
    /**
     * 筛选条件 任务所属的计划策略集合
     */
    private List<String> planTypes;

    /**
     * 查询类型
     * NEST - 机巣
     * UAV - 无人机
     */
    private String deviceCodeType = "NEST";

}

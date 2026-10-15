package com.chinatower.airline.dto.response;

import com.chinatower.event.constants.Constants;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 16:09
 */
@Data
public class AirResultTaskRep {
    /**
     * 飞行任务名称
     */
    private String planTaskName;
    /**
     * 媒体同步状态
     */
    private String mediaSyncStatus;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 照片数量
     */
    private Integer photoCount;
    /**
     * 视频数量
     */
    private Integer videoCount;
    /**
     * 录屏数量
     */
    private Integer screencapCount;
    /**
     * 航线名称
     */
    private String airlineName;
    /**
     * 任务id
     */
    private String taskRecordId;
    /**
     * 航线id
     */
    private String flightRouteId;
    /**
     * 计划执行时间
     */
    private String scheduleExecTime;
    /**
     * 任务关联航线的备份id
     */
    private String snapshotId;
    /**
     * 航线名称
     */
    private String flightRouteName;
    /**
     * 操作人名称
     */
    private String nickName;
    /**
     * 实际起飞时间
     */
    private String realityTime;
    /**
     * 航线大类区分  默认0-正常航线  1 指点飞行 2 一键起飞
     */
    private String flightMainType;
    /**
     * 重复飞行状态：0-未飞行，1-已飞行
     */
    private String reFlyStatus = Constants.ZERO;
    /**
     * 采集状态：0-未收藏，1-已收藏
     */
    private String collectStatus = Constants.ZERO;
    /**
     * 是否主任务：0-主任务，1-复飞任务
     */
    private String isMainTaskStatus = Constants.ZERO;
    /**
     * 飞行批次记录
     */
    private List<FlightRecordResponse> flightRecordList;
    /**
     * 复飞类型
     */
    private String reFlyType;
    /**
     * 复飞原始任务id
     */
    private String originalTaskRecordId;
    /**
     * 删除标识（0-未删除、1-逻辑删、2-物理删）
     */
    private String deleteFlag;
    /**
     * 是否开启扩展水印（0-否、1-是）
     */
    private String isExpandWatermark;
    /**
     * 飞行里程
     */
    private String airMileage;

    /**
     * 计划id
     */
    private String planId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 任务飞行时长
     */
    private String airTime;

    //飞行任务id
    private Integer id;

    /**
     * 任务所属的计划策略集合
     */
    private String planType;

    /**
     * 起始机巢编号
     */
    private String startNestCode	;
    /**
     * 降落机巢编号
     */
    private String endNestCode	;


    // 架次记录实体类
    @Data
    public static class FlightRecordResponse {
        private String flightRecordId; // 架次记录ID（非必填）
        private String mediaSyncStatus; // 媒体数据同步状态（非必填）
        private Integer flyIndex; // 航线飞行次数（非必填）
        private String startTime; // 飞行记录开始时间（非必填）
        private Integer photoCount; // 架次拍照图片数量（非必填）
        private Integer videoCount; // 架次拍摄视频数量（非必填）
        private Integer screencapCount; // 架次飞行录屏数量（非必填）
    }
}

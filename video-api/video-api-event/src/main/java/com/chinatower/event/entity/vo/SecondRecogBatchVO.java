package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 二次识别配置下发参数
 * @author cuisj
 * @date 2023-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogBatchVO{

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务批次id
     */
    private String taskBatchId;

    /**
     * 任务类型  1：告警 2：无人机
     */
    private String taskType;

    /**
     * 任务状态   0=分析中，1=分析完成，-1=分析失败
     */
    private String analyType;

    /**
     * 图片id
     */
    private String picId;

    /**
     * 图片id
     */
    private String videoId;

    /**
     * 创建时间
     */
    private String createTime;

    /** 创建人 */
    private String creator;

    /** 修改人 */
    private String modifier;

    /**
     * 删除标识 0：正常  1：删除
     */
    private String deleteFlag;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 行业编码
     */
    private String industryCode;

    /** 应用版本编码 */
    private String appVerCode;

    /**
     * 受理节点编码
     */
    private String acceptNode;
    /**
     * 接入节点编码
     */
    private String accessNode;

    /**
     * 二次分析结果
     */
    private String secondJudgeResult;


    /**
     * 二次分析图片
     */
    private String originalPictureUrl;


    /**
     * 二次分析框图url
     */
    private String alarmPictureUrl;

    /**
     * 二次分析视频
     */
    private String alarmVideoUrl;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 飞行任务id
     * */
    private String airTaskId;

    /**
     * 飞行航线id
     * */
    private String airLineId;

    /**
     * 算法编码
     */
    private String algorithmCode;


    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;


    /**
     * 是否自动生成告警 0:否 1:是
     */
    private String makeAlarmFlag;

    /**
     * 基准图ID
     */
    private String referenceId;

    //算法仓识别结果
    private String resultJson;

    private String warningOrderId;

    private String orderType;

    //ptz信息结果
    private String ptzInfoJson;


    /**
     *无人机二次识别下发来源  1：对比图
     */
    private String uavRecogSource;

}

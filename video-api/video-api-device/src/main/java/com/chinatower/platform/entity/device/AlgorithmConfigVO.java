package com.chinatower.platform.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmConfigVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmConfigVO {

    /**
     * 配置编号
     */
    String configId;
    /**
     * 设备编号
     */
    String deviceCode;

    /**
     * 通道编码
     */
    String channelCode;
    /**
     * 位置类型：1通道，2预置位
     * @required true
     */
    Integer posType;
    /**
     * 预置位编号
     */
    Long presetId;
    /**
     * 客户编号
     */
    String tenantId;
    /**
     * 创建时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    String createTime;

    /**
     * 设备名称
     */
    String deviceName;
    /**
     * 通道名称
     */
    String channelName;
    /**
     * 预置位名称
     */
    String presetName;
    /**
     * 点位json
     */
    String points;
    /**
     * 图片
     */
    String img;

    /**
     * 算法编码
     */
    String algorithmCode;

    /**
     * 算法名称
     */
    String algorithmName;
    /**
     * 算法名称
     */
    String tenantName;
    /**
     * 算法仓任务id
     */
    String taskId;
    /**
     * 算法任务状态：0:启用 1:停用
     */
    String status;
    /**
     * 算法仓任务状态 1成功；2失败；3处理中；4下发中
     */
    String algorithmStatus;
    /**
     * 置信度阈值
     */
    String thresh;
    /**
     * 算法高级参数
     */
    String modelConfig;
    /**
     * 绊线
     */
    String tripThread;
    /**
     * 算法分析时段
     */
    String analysisPeriod;

    /**
     *  预置位ptz参数
     */
    AlgorithmParamPresetVO preset;

    /**
     *  算法区域列表
     *  @required true
     */
    List<AlgorithmAreaVO> algorithms;

    /**
     *  关联区域
     */
    List<AlgorithmMonitorAreaVO> monitorArea;

    /**
     * 算法类型，1后端算法;2前端算法
     */
    String taskType;

    /**
     * 识别区域与图斑关系集合
     */
    private List<PolygonPatchAlgoAreaVO> polygonAreaList;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 可视半径，单位：米
     */
    private String visualRadius;

    /** 油气管线json */
    private String oilGasPipelines;
    /** 更新人 */
    private String modifier;
    /** 创建人 */
    private String creator;
    /** 更新时间 */
    private String updateTime;
    /** 创建人 */
    private String creatorName;
    /** 更新人 */
    private String modifierName;

    /** 任务抽帧频率*/
    private Integer timeInterval;

    /** 抽帧频率单位,1:小时 2:分钟 3:秒 */
    private Integer timeIntervalUnit;

    /** 文本标签json */
    private String textLabels;

    /** 是否联动算法 0：否，1：是 */
    private String isLinkageAlgorithm;

    /**
     * 联动算法json
     */
    private String linkageAlgorithmInfo;

    /**
     * 告警框颜色字段
     */
    private String alarmFrameColor;

    /**
     * 告警框粗细字段
     */
    private String alarmFrameWidth;

    /**
     * 算法扩展字段json串，alarmFrameColor：告警框颜色
     */
    private String extendJsonAlgo;

    /**
     * 是否开启双通道并行分析 0：否，1：是
     */
    private String doubleImgAnalysis;

    /**
     * 模型类型 0:小模型 1：大模型
     * @required true
     */
    private Integer modelType;

    /**
     * 场景编码,逗号分隔
     */
    private String sceneCode;

    /**
     * 告警间隔时间
     */
    private Integer alarmInterval;
}

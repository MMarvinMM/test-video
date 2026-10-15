package com.chinatower.platform.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmAreaVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmAreaVO {

    public final static String INVOKESTATUS = "1";
    public final static String ALGORITHMOPENSTATUS = "1";
    public final static String ALGORITHMCLOSESTATUS = "0";

    public final static String ALGORITHMSUCCESSSTATUS = "1";
    public final static String ALGORITHMFAILSTATUS = "2";
    public final static String ALGORITHMLOADINGSTATUS = "3";

    public final static String SECOND = "s";
    public final static String MINUTES = "M";
    public final static String HOUR = "h";
    public final static String DAY = "d";
    public final static String ONSTATUS = "5";
    public final static String STOPSTATUS = "6";
    public final static String ERRRORSTATUS = "2";

    /**
     * 删除标识
     */
    public final static String ONE = "1";

    /** 配置编号 */
    String configId;
    /**
     * 算法编号
     * 算法任务新增时，必填
     * @required true
     */
    String algorithmCode;
    /** 坐标点json */
    String points;
    /** 截图
     * 算法任务新增时，必填
     * @required true
     */
    String img;
    /** 通道编码 */
    String channelCode;
    /** 通道名称 */
    String channelName;
    /** 设备编码 */
    String deviceCode;
    /** 设备名称 */
    String deviceName;
    /** 预置位ID */
    String presetId;
    /** 预置位名称 */
    String presetName;
    /** 预置位图片 */
    String imgUrl;
    /** 算法名称
     * 算法任务新增时，必填
     * @required true
     */
    String algorithmName;
    /** 客户编码 */
    String tenantId;
    /** 客户名称 */
    String tenantName;
    /** 客户省份 */
    String provinceName;
    /** 客户地市 */
    String cityName;

    /** 操作类型 1-新增；2-修改；3-删除（删除也传全字段数据）
     * 算法任务新增时，必填
     * @required true
     */
    Integer optType;
    /**
     * 监控任务唯一标识：32位uuid唯一主键
     * 查询算法状态时，必填
     * @required true
     */
    String taskId;
    /** 状态：0:停用 1:启用 */
    String status;
    /** 删除标识 0:删除 1:正常 */
    String deleteFlag;
    /** 关联区域 */
    List<AlgorithmMonitorAreaVO> monitorArea;

    /** 订单编码 */
    String orderCode;
    /** 订单状态 */
    String ordStatus;

    /** 操作类型 */
    Integer posType;

    /** 铁塔视角算法状态：1:启用成功 2:启用失败 3:处理中 */
    String algorithmStatus;

    /** 算法描述 */
    String algorithmDescription;

    /** url */
    String logoUrl;
    /** 置信度阈值 */
    Float thresh;
    /** 重复告警间隔 */
    Integer alarmInterval;
    /** 重复告警间隔单位:秒s,分M,时h,天d */
    String alarmIntervalType;
    /** 算法运行时间 */
    String[] algorithmTime;
    /** 算法时间段 */
    String algorithmTimeJosn;
    /** 算法操作 1.新增2.启用3.停用 */
    Integer operate;

    /** 预置位 */
    Object preset;
    /**
     * 行业版本
     */
    private String appVerCode;
    /** 行业编码 */
    private String industryCode;
    /** 修改人 */
    private String modifier;
    /** 修改时间 */
    private String updateTime;
    /** 创建时间 */
    private String createTime;
    /** 创建人 */
    private String creator;

    /** 1后端算法;2前端算法 */
    String taskType;

    /** 配置参数json */
    private String modelConfig;
    /** 绊线json */
    private String tripThread;
    /** 配置参数集合 */
    private List<ModelConfigVO> modelConfigList;
    /** 绊线集合 */
    private List<TripThreadVO> tripThreadList;

    /** 油气管线json */
    private String oilGasPipelines;
    /** 油气管线集合 */
    private List<OilGasPipelinesVO> oilGasPipelinesList;

    /**
     * 图标名称
     */
    private String iconCode;
    /**
     * 图标颜色
     */
    private String iconColour;

    /** 算法分析时段 */
    private String analysisPeriod;

    /** 识别区域与图斑关系集合 */
    private List<PolygonPatchConfig> polygonAreaList;
    /** 算法分析时段集合 */
    private List<AnalysisPeriodVO> analysisPeriodList;

    /** 后端算法名称 */
    private String backendAlgoName;

    /** 是否二次研判 0：是  1：否 */
    private String secondSwitch;

    /** 任务抽帧频率*/
    private Integer timeInterval;

    /** 抽帧频率单位,1:小时 2:分钟 3:秒 */
    private Integer timeIntervalUnit;

    /**
     * 文本标签集合
     */
    private List<TextLabelVO> textLabelList;

    /** 文本标签json */
    private String textLabels;

    /** 是否联动算法 0：否，1：是 */
    private String isLinkageAlgorithm;
    /**
     * 联动算法
     */
    private LinkageAlgorithmVO linkageAlgorithmVO;

    /**
     * 联动算法json
     */
    private String linkageAlgorithm;

    /**
     * 是否更新联动算法 0：否，1：是
     */
    private String isUpdateLinkageAlgorithm;

    /**
     * 识别区域、屏蔽区域、基准区域 经纬度点
     */
    private String pointsLonLat;

    /**
     * 绊线经纬度点
     */
    private String tripThreadLonLat;

    /**
     * 管道经纬度点
     */
    private String oilGasPipelinesLonLat;

    /**
     * 文本经纬度点
     */
    private String textLabelsLonLat;

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
     * 场景编码,逗号分隔
     */
    private String sceneCode;
}

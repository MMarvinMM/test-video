package com.chinatower.video.modules.query.vo;

import com.chinatower.platform.entity.device.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlgorithmAreaVO implements Serializable {

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

    /** 绊线json */
    private String tripThread;

    /** 绊线集合 */
    private List<TripThreadVO> tripThreadList;

    /** 油气管线json */
    private String oilGasPipelines;
    /** 油气管线集合 */
    private List<OilGasPipelinesVO> oilGasPipelinesList;


    private String textLabels;


    /**
     * 文本标签集合
     */
    private List<TextLabelVO> textLabelList;


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


}

package com.chinatower.event.api.model;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:42
 */

@Data
public class ApiInterceptOrderEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Excel(name = "工单号")
    private String warningOrderId;

    /**
     * 告警工单ID集合
     */
    private List<String> warningOrderIds;

    @Excel(name = "告警来源")
    private String warningSource;

    @Excel(name = "告警来源名称")
    private String warningSourceName;

    @Excel(name = "接入节点")
    private String accessNode;

    @Excel(name = "受理节点")
    private String acceptNode;

    @Excel(name = "任务id")
    private String taskId;

    @Excel(name = "告警类型")
    private String warningTypeId;

    @Excel(name = "告警原因")
    private String alarmReason;

    @Excel(name = "告警类型名称")
    private String warningTypeName;

    @Excel(name = "告警时间")
    private String alarmTime;

    @Excel(name = "租户id")
    private String tenantId;

    @Excel(name = "租户名称")
    private String tenantName;

    @Excel(name = "省份编码")
    private String provinceId;

    @Excel(name = "省份名称")
    private String provinceName;

    @Excel(name = "地市编码")
    private String cityId;

    @Excel(name = "市名称")
    private String cityName;

    @Excel(name = "县编码")
    private String countyId;

    @Excel(name = "县名称")
    private String countyName;

    @Excel(name = "乡镇编码")
    private String townCode;

    @Excel(name = "乡镇名称")
    private String townName;

    @Excel(name = "纬度")
    private String latitude;

    @Excel(name = "经度")
    private String longitude;

    @Excel(name = "告警内容")
    private String alarmBody;

    @Excel(name = "设备编码")
    private String deviceCode;

    @Excel(name = "设备名称")
    private String deviceName;

    @Excel(name = "通道编码")
    private String channelCode;

    @Excel(name = "通道名称")
    private String channelName;

    @Excel(name = "P位置")
    private String ptzP;

    @Excel(name = "T位置")
    private String ptzT;

    @Excel(name = "Z位置")
    private String ptzZ;

    @Excel(name = "详细地址")
    private String address;

    @Excel(name = "告警编码")
    private String alarmCode;

    @Excel(name = "算法编码")
    private String algorithmCode;

    @Excel(name = "告警等级")
    private String emergencyLevel;

    @Excel(name = "告警等级名称")
    private String emergencyLevelName;

    @Excel(name = "客户编号")
    private String customerCode;

    @Excel(name = "网格ID")
    private String gridId;

    @Excel(name = "网格编码")
    private String gridCode;

    @Excel(name = "网格名称")
    private String gridName;

    @Excel(name = "网格员姓名")
    private String gridUserName;

    @Excel(name = "视频路径")
    private String videoUrl;

    @Excel(name = "原图")
    private String fileImgUrlSrc;

    @Excel(name = "框图")
    private String fileImgUrlIcon;

    @Excel(name = "研判结果")
    private String judgeResult;

    private String createTime;

    private String modifierDate;

    private String deleteFlag;

    @Excel(name = "告警重要等级")
    private String importanceLevel;

    @Excel(name = "重要等级名称")
    private String importanceLevelName;

    @Excel(name = "告警压制")
    private String intercept;

    private Date prevAlarmTime;

    private String thisThresh;

    private String thresh;

    public Integer alarmInterval;

    /** 重复告警间隔单位 */
    public String alarmIntervalType;

    /** 算法运行时间 */
    public transient List<String> algorithmTime;

    @Excel(name = "行业扩展字段")
    private String extraField;

    private String appVerCode;

    @Excel(name = "消息类型")
    private String msgType;

    @Excel(name = "通知类型")
    private String noticeResource;

    @Excel(name = "置信度")
    private String confidenceLevel;

    @Excel(name = "最小置信度")
    private String confidenceLevelMin;

    @Excel(name = "最大置信度")
    private String confidenceLevelMax;

    @Excel(name = "综合置信度")
    private String compConfidenceLevel;

    @Excel(name = "前端告警图片")
    private String frontAlarmPicUrl;

    @Excel(name = "前端告警视频")
    private String frontAlarmVedioUrl;

    @Excel(name = "水平方位角")
    private String horiAzimuthAngle;

    private String cameraAlarmType;

    @Excel(name = "是否二次研判")
    private String isSecondJudge;

    @Excel(name = "二次研判结果")
    private String secondJudgeResult;

    /**
     * 是否二次识别 1-是；0-否
     */
    @Excel(name = "是否二次识别")
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     */
    @Excel(name = "二次识别结果")
    private String secondIdentifyResult;

    @Excel(name = "联动算法")
    private String linkageAlgorithm;

    /**
     * 物联设备编号/名称
     */
    private List<String> iotDeviceCode;

    @Excel(name = "预置位ID")
    private String presetId;

    @Excel(name = "预置位名称")
    private String presetName;

    @Excel(name = "是否3D放大")
    private String enlargeFlag;

    /**
     * 属地化节点
     */
    private String subSideId;

    /**
     * 设备经度
     */
    @Excel(name = "设备经度")
    private String deviceLongitude;

    /**
     * 设备维度
     */
    @Excel(name = "设备纬度")
    private String deviceLatitude;

    /**
     * 设备厂家
     */
    @Excel(name = "设备厂家")
    private String deviceFactoryName;

    /**
     * 图版对象信息
     */
    @Excel(name = "图斑信息")
    private String polygonOperate;

    /**
     * 设备地址
     */
    @Excel(name = "设备地址")
    private String deviceAddress;

    /**
     * 设备序列号
     */
    @Excel(name = "设备序列")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    @Excel(name = "设备型号")
    private String deviceModel;


    private Integer pageNum;

    private Integer pageSize;

    private String alarmTimeStart;

    private String alarmTimeEnd;

    private String minChar;

    private String maxChar;

    /**
     * 告警状态
     */
    private List<String> orderStatusList;

    /**
     * 是否是导出excel
     */
    private String isExport;
}

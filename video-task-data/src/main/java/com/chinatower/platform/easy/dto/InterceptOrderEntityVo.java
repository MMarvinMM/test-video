package com.chinatower.platform.easy.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:42
 */

@Data
@Document(indexName = "vm_intercept_order")
public class InterceptOrderEntityVo extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ExcelProperty( "工单号")
    private String warningOrderId;

    @ExcelProperty( "告警来源")
    private String warningSource;

    @ExcelProperty( "告警来源名称")
    private String warningSourceName;

    @ExcelProperty( "任务id")
    private String taskId;

    @ExcelProperty( "告警类型")
    private String warningTypeId;

    @ExcelProperty( "告警类型名称")
    private String warningTypeName;

    @ExcelProperty( "告警时间")
    private String alarmTime;

    private Date alarmTimeDate;

    @ExcelProperty( "租户id")
    private String tenantId;

    @ExcelProperty( "租户名称")
    private String tenantName;

    @ExcelProperty( "省份编码")
    private String provinceId;

    @ExcelProperty( "省份名称")
    private String provinceName;

    @ExcelProperty( "地市编码")
    private String cityId;

    @ExcelProperty( "市名称")
    private String cityName;

    @ExcelProperty( "县编码")
    private String countyId;

    @ExcelProperty( "县名称")
    private String countyName;

    @ExcelProperty( "乡镇编码")
    private String townCode;

    @ExcelProperty( "乡镇名称")
    private String townName;

    @ExcelProperty( "纬度")
    private String latitude;

    @ExcelProperty( "经度")
    private String longitude;

    @ExcelProperty( "告警内容")
    private String alarmBody;

    @ExcelProperty( "设备编码")
    private String deviceCode;

    @ExcelProperty( "设备名称")
    private String deviceName;

    @ExcelProperty( "通道编码")
    private String channelCode;

    @ExcelProperty( "通道名称")
    private String channelName;

    @ExcelProperty( "P位置")
    private String pan;

    @ExcelProperty( "T位置")
    private String tilt;

    @ExcelProperty( "Z位置")
    private String zoom;

    @ExcelProperty( "转义后P位置")
    private String ptzTransP;

    @ExcelProperty( "转义后T位置")
    private String ptzTransT;

    @ExcelProperty( "转义后Z位置")
    private String ptzTransZ;

    @ExcelProperty( "详细地址")
    private String address;

    @ExcelProperty( "告警编码")
    private String alarmCode;

    @ExcelProperty( "算法编码")
    private String algorithmCode;

    @ExcelProperty( "告警等级")
    private String emergencyLevel;

    @ExcelProperty( "告警等级名称")
    private String emergencyLevelName;

    @ExcelProperty( "客户编号")
    private String customerCode;

    @ExcelProperty( "网格ID")
    private String gridId;

    @ExcelProperty( "网格编码")
    private String gridCode;

    @ExcelProperty( "网格名称")
    private String gridName;

    @ExcelProperty( "网格员姓名")
    private String keeperName;

    @ExcelProperty( "视频路径")
    private String videoUrl;

    @ExcelProperty( "原图")
    private String fileImgUrlSrc;

    @ExcelProperty( "框图")
    private String fileImgUrlIcon;

    @ExcelProperty( "研判结果")
    private String judgeResult;

    @ExcelProperty( "创建时间")
    private String createTime;

    @ExcelProperty( "更新时间")
    private String modifierDate;

    @ExcelProperty( "删除标识")
    private String deleteFlag;

    @ExcelProperty( "告警重要等级")
    private String importanceLevel;

    @ExcelProperty( "重要等级名称")
    private String importanceLevelName;

    @ExcelProperty( "告警压制")
    private String intercept;

    @ExcelProperty( "告警压制原因")
    private String interceptName;

    @ExcelProperty( "上一次告警时间")
    private String prevAlarmTime;

    @ExcelProperty( "本次告警置信度阈值")
    private String thisThresh;

    @ExcelProperty( "算法配置置信度阈值")
    private String thresh;

    @ExcelProperty( "算法配置重复告警间隔")
    public Integer alarmInterval;

    @ExcelProperty( "算法配置重复告警间隔单位")
    public String alarmIntervalType;

    @ExcelProperty( "算法配置算法运行时间")
    private String algorithmTime;

    @ExcelProperty( "行业扩展字段")
    private String extraField;

    @ExcelProperty( "行业应用编码")
    private String industryCode;

    @ExcelProperty( "行业应用名称")
    private String industryName;

    @ExcelProperty( "行业版本")
    private String appVerCode;

    @ExcelProperty( "消息类型")
    private String msgType;

    @ExcelProperty( "消息类型名称")
    private String msgTypeName;

    @ExcelProperty( "通知类型")
    private String noticeResource;

    @ExcelProperty( "通知类型名称")
    private String noticeResourceName;

    @ExcelProperty( "置信度")
    private String confidenceLevel;

    @ExcelProperty( "最小置信度")
    private String confidenceLevelMin;

    @ExcelProperty( "最大置信度")
    private String confidenceLevelMax;

    private Double confidenceLevelMaxDouble;

    @ExcelProperty( "综合置信度")
    private String compConfidenceLevel;

    @ExcelProperty( "前端告警图片")
    private String frontAlarmPicUrl;

    @ExcelProperty( "前端告警视频")
    private String frontAlarmVedioUrl;

    @ExcelProperty( "水平方位角")
    private String horiAzimuthAngle;

    private String cameraAlarmType;

    @ExcelProperty( "是否二次研判")
    private String isSecondJudge;

    @ExcelProperty( "二次研判结果")
    private String secondJudgeResult;

    @ExcelProperty("是否二次识别")
    private String isSecondIdentify;

    @ExcelProperty("二次识别结果")
    private String secondIdentifyResult;

    @ExcelProperty("联动算法")
    private String linkageAlgorithm;

    @ExcelProperty( "预置位ID")
    private String presetId;

    @ExcelProperty( "预置位名称")
    private String presetName;

    @ExcelProperty( "是否3D放大")
    private String enlargeFlag;

    @ExcelProperty( "接入节点")
    private String accessNode;

    @ExcelProperty( "受理节点")
    private String acceptNode;

    @ExcelProperty( "节点名称")
    private String platformName;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    @ExcelProperty("告警定位")
    private String alarmLocation;

    @ExcelProperty( "设备厂家编号")
    private String modelId;

    @ExcelProperty( "设备挂高")
    private String height;

    @ExcelProperty( "设备经度")
    private String deviceLongitude;

    @ExcelProperty( "设备纬度")
    private String deviceLatitude;

    @ExcelProperty( "摄像机经度")
    private String videoLongitude;

    @ExcelProperty( "摄像机纬度")
    private String videoLatitude;

    @ExcelProperty( "设备厂家")
    private String modelFactoryName;

    @ExcelProperty( "物联设备型号")
    private String deviceModelCode;

    /**
     * 图版对象信息
     */
    @ExcelProperty( "图斑信息")
    private String polygonOperate;

    /**
     * 设备地址
     */
    @ExcelProperty( "设备地址")
    private String deviceAddress;

    /**
     * 设备序列号
     */
    //@ExcelProperty( "设备序列")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    //@ExcelProperty( "设备型号")
    private String deviceModel;

    @ExcelProperty("物联设备联动摄像机信息")
    private String relDeviceVo;

    @ExcelProperty("雷达设备联动摄像机信息")
    private String radarDeviceVo;

    @ExcelProperty("雷达设备信息")
    private String deviceSiteListVo;

    @ExcelProperty("卫星告警联动摄像机信息")
    private String satelliteDeviceListVo;

    @ExcelProperty( "告警原因")
    private String alarmReason;
    @ExcelProperty( "摄像机俯仰角")
    private String devicePitch;
    @ExcelProperty( "摄像机水平角")
    private String deviceHorizontal;

    @ExcelProperty("扩展字段")
    private String extendFields;

    /**
     * 事件描述
     */
    @ExcelProperty( "事件描述")
    private String description;
}

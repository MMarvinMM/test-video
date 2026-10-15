package com.chinatower.platform.modules;

import com.alibaba.excel.annotation.ExcelProperty;
import com.chinatower.common.annotation.Excel;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.entity.vo.SatelliteRelDeviceVo;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:42
 */

@Data
@Document(indexName = "vm_intercept_order",shards = 3,replicas = 2)
public class InterceptOrderEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 工单号
     */
    @ExcelProperty("工单号")
    private String warningOrderId;

    /**
     * 告警来源
     */
    @ExcelProperty("告警来源")
    private String warningSource;

    /**
     * 告警来源名称
     */
    @ExcelProperty("告警来源名称")
    private String warningSourceName;

    /**
     * 任务id
     */
    @ExcelProperty("任务id")
    private String taskId;

    /**
     * 告警类型
     */
    @ExcelProperty("告警类型")
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    @ExcelProperty("告警类型名称")
    private String warningTypeName;

    /**
     * 告警时间
     */
    @ExcelProperty("告警时间")
    private String alarmTime;

    private Date alarmTimeDate;

    /**
     * 租户id
     */
    @ExcelProperty("租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @ExcelProperty("租户名称")
    private String tenantName;

    /**
     * 省份编码
     */
    @ExcelProperty("省份编码")
    private String provinceId;

    /**
     * 省份名称
     */
    @ExcelProperty("省份名称")
    private String provinceName;

    /**
     * 地市编码
     */
    @ExcelProperty("地市编码")
    private String cityId;

    /**
     * 市名称
     */
    @ExcelProperty("市名称")
    private String cityName;

    /**
     * 县编码
     */
    @ExcelProperty("县编码")
    private String countyId;

    /**
     * 县名称
     */
    @ExcelProperty("县名称")
    private String countyName;

    /**
     * 乡镇编码
     */
    @ExcelProperty("乡镇编码")
    private String townCode;

    /**
     * 乡镇名称
     */
    @ExcelProperty("乡镇名称")
    private String townName;

    /**
     * 纬度
     */
    @ExcelProperty("纬度")
    private String latitude;

    /**
     * 经度
     */
    @ExcelProperty("经度")
    private String longitude;

    /**
     * 告警内容
     */
    @ExcelProperty("告警内容")
    private String alarmBody;

    /**
     * 设备编码
     */
    @ExcelProperty("设备编码")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ExcelProperty("设备名称")
    private String deviceName;

    /**
     * 通道编码
     */
    @ExcelProperty("通道编码")
    private String channelCode;

    /**
     * 通道名称
     */
    @ExcelProperty("通道名称")
    private String channelName;

    /**
     * P位置
     */
    @ExcelProperty("P位置")
    private String pan;

    /**
     * T位置
     */
    @ExcelProperty("T位置")
    private String tilt;

    /**
     * Z位置
     */
    @ExcelProperty("Z位置")
    private String zoom;

    /**
     * 转义后P位置
     */
    @ExcelProperty("转义后P位置")
    private String ptzTransP;

    /**
     * 转义后T位置
     */
    @ExcelProperty("转义后T位置")
    private String ptzTransT;

    /**
     * 转义后Z位置
     */
    @ExcelProperty("转义后Z位置")
    private String ptzTransZ;

    /**
     * 详细地址
     */
    @ExcelProperty("详细地址")
    private String address;

    /**
     * 告警编码
     */
    @ExcelProperty("告警编码")
    private String alarmCode;

    /**
     * 算法编码
     */
    @ExcelProperty("算法编码")
    private String algorithmCode;

    /**
     * 告警等级
     */
    @ExcelProperty("告警等级")
    private String emergencyLevel;

    /**
     * 告警等级名称
     */
    @ExcelProperty("告警等级名称")
    private String emergencyLevelName;

    /**
     * 客户编号
     */
    @ExcelProperty("客户编号")
    private String customerCode;

    /**
     * 网格ID
     */
    @ExcelProperty("网格ID")
    private String gridId;

    /**
     * 网格编码
     */
    @ExcelProperty("网格编码")
    private String gridCode;

    /**
     * 网格名称
     */
    @ExcelProperty("网格名称")
    private String gridName;

    /**
     * 网格员姓名
     */
    @ExcelProperty("网格员姓名")
    private String keeperName;

    /**
     * 视频路径
     */
    @ExcelProperty("视频路径")
    private String videoUrl;

    /**
     * 原图
     */
    @ExcelProperty("原图")
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    @ExcelProperty("框图")
    private String fileImgUrlIcon;

    /**
     * 研判结果
     */
    @ExcelProperty("研判结果")
    private String judgeResult;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    private String modifierDate;

    /**
     * 删除标识
     */
    @ExcelProperty("删除标识")
    private String deleteFlag;

    /**
     * 告警重要等级
     */
    @ExcelProperty("告警重要等级")
    private String importanceLevel;

    /**
     * 重要等级名称
     */
    @ExcelProperty("重要等级名称")
    private String importanceLevelName;

    /**
     * 告警压制
     */
    @ExcelProperty("告警压制")
    private String intercept;

    /**
     * 告警压制原因
     */
    @ExcelProperty("告警压制原因")
    private String interceptName;

    /**
     * 上一次告警时间
     */
    @ExcelProperty("上一次告警时间")
    private String prevAlarmTime;

    /**
     * 本次告警置信度阈值
     */
    @ExcelProperty("本次告警置信度阈值")
    private String thisThresh;

    /**
     * 算法配置置信度阈值
     */
    @ExcelProperty("算法配置置信度阈值")
    private String thresh;

    /**
     * 算法配置重复告警间隔
     */
    @ExcelProperty("算法配置重复告警间隔")
    public Integer alarmInterval;

    /**
     * 算法配置重复告警间隔单位
     */
    @ExcelProperty("算法配置重复告警间隔单位")
    public String alarmIntervalType;

    /**
     * 算法配置算法运行时间
     */
    @ExcelProperty("算法配置算法运行时间")
    private String algorithmTime;

    /**
     * 行业扩展字段
     */
    @ExcelProperty("行业扩展字段")
    private String extraField;

    /**
     * 行业应用编码
     */
    @ExcelProperty("行业应用编码")
    private String industryCode;

    /**
     * 行业应用名称
     */
    @ExcelProperty("行业应用名称")
    private String industryName;

    /**
     * 行业版本
     */
    @ExcelProperty("行业版本")
    private String appVerCode;

    /**
     * 消息类型
     */
    @ExcelProperty("消息类型")
    private String msgType;

    /**
     * 消息类型名称
     */
    @ExcelProperty("消息类型名称")
    private String msgTypeName;

    /**
     * 通知类型
     */
    @ExcelProperty("通知类型")
    private String noticeResource;

    /**
     * 通知类型名称
     */
    @ExcelProperty("通知类型名称")
    private String noticeResourceName;

    /**
     * 置信度
     */
    @ExcelProperty("置信度")
    private String confidenceLevel;

    /**
     * 最小置信度
     */
    @ExcelProperty("最小置信度")
    private String confidenceLevelMin;

    /**
     * 最大置信度
     */
    @ExcelProperty("最大置信度")
    private String confidenceLevelMax;

    private Double confidenceLevelMaxDouble;

    /**
     * 综合置信度
     */
    @ExcelProperty("综合置信度")
    private String compConfidenceLevel;

    /**
     * 前端告警图片
     */
    @ExcelProperty("前端告警图片")
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    @ExcelProperty("前端告警视频")
    private String frontAlarmVedioUrl;

    /**
     * 水平方位角
     */
    @ExcelProperty("水平方位角")
    private String horiAzimuthAngle;

    /**
     * 前端告警类型
     */
    private String cameraAlarmType;

    /**
     * 是否二次研判
     */
    @ExcelProperty("是否二次研判")
    private String isSecondJudge;

    /**
     * 二次研判结果
     */
    @ExcelProperty("二次研判结果")
    private String secondJudgeResult;

    /**
     * 是否二次识别
     */
    @ExcelProperty("是否二次识别")
    private String isSecondIdentify;

    /**
     * 二次识别结果
     */
    @ExcelProperty("二次识别结果")
    private String secondIdentifyResult;

    /**
     * 联动算法
     */
    @ExcelProperty("联动算法")
    private String linkageAlgorithm;

    /**
     * 预置位ID
     */
    @ExcelProperty("预置位ID")
    private String presetId;

    /**
     * 预置位名称
     */
    @ExcelProperty("预置位名称")
    private String presetName;

    /**
     * 是否3D放大
     */
    @ExcelProperty("是否3D放大")
    private String enlargeFlag;

    /**
     * 接入节点
     */
    @ExcelProperty("接入节点")
    private String accessNode;

    /**
     * 受理节点
     */
    @ExcelProperty("受理节点")
    private String acceptNode;

    /**
     * 节点名称
     */
    @ExcelProperty("节点名称")
    private String platformName;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 告警定位
     */
    @ExcelProperty("告警定位")
    private String alarmLocation;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    /**
     * 设备厂家编号
     */
    @ExcelProperty("设备厂家编号")
    private String modelId;

    /**
     * 设备挂高
     */
    @ExcelProperty("设备挂高")
    private String height;

    /**
     * 设备经度
     */
    @ExcelProperty("设备经度")
    private String deviceLongitude;

    /**
     * 设备纬度
     */
    @ExcelProperty("设备纬度")
    private String deviceLatitude;

    /**
     * 摄像机经度
     */
    @ExcelProperty("摄像机经度")
    private String videoLongitude;

    /**
     * 摄像机纬度
     */
    @ExcelProperty("摄像机纬度")
    private String videoLatitude;

    /**
     * 设备厂家
     */
    @ExcelProperty("设备厂家")
    private String modelFactoryName;

    /**
     * 物联设备型号
     */
    @ExcelProperty("物联设备型号")
    private String deviceModelCode;

    /**
     * 图版对象信息
     */
    @ExcelProperty("图斑信息")
    private String polygonOperate;

    /**
     * 设备地址
     */
    @ExcelProperty("设备地址")
    private String deviceAddress;

    /**
     * 设备序列号
     */
    //@ExcelProperty("设备序列")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    //@ExcelProperty("设备型号")
    private String deviceModel;

    /**
     * 物联设备联动摄像机信息
     */
    @ExcelProperty("物联设备联动摄像机信息")
    private String relDeviceVo;

    /**
     * 雷达设备联动摄像机信息
     */
    @ExcelProperty("雷达设备联动摄像机信息")
    private String radarDeviceVo;

    /**
     * 雷达设备信息
     */
    @ExcelProperty("雷达设备信息")
    private String deviceSiteListVo;

    /**
     * 告警原因
     */
    @ExcelProperty("告警原因")
    private String alarmReason;

    /**
     * 摄像机俯仰角
     */
    @ExcelProperty("摄像机俯仰角")
    private String devicePitch;

    /**
     * 摄像机水平角
     */
    @ExcelProperty("摄像机水平角")
    private String deviceHorizontal;

    /**
     * 卫星告警联动摄像机信息
     */
    @ExcelProperty("卫星告警联动摄像机信息")
    private String satelliteDeviceListVo;

    /**
     * 扩展字段
     */
    @ExcelProperty("扩展字段")
    private String extendFields;

    /**
     * 事件描述
     */
    @ExcelProperty("事件描述")
    private String description;

    /**
     * 通用新增储存备用的透传字段
     */
    private String transparentFields;
}

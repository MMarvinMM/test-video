package com.chinatower.platform.entity;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.entity.dto.AreaCodeReq;
import com.chinatower.event.entity.dto.RegionAlarmExtraField;
import com.chinatower.event.entity.vo.GridInfo;
import com.chinatower.event.entity.vo.IotDeviceInfoVO;
import com.chinatower.event.entity.vo.SatelliteRelDeviceVo;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:42
 */

@Data
public class InterceptOrderEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Excel(name = "事件编号")
    private String orderId;

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

    @Excel(name = "任务id")
    private String taskId;

    @Excel(name = "告警类型")
    private String warningTypeId;

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

    @Excel(name = "接入节点")
    private String accessNode;

    @Excel(name = "受理节点")
    private String acceptNode;

    @Excel(name = "纬度")
    private String latitude;

    @Excel(name = "经度")
    private String longitude;

    @Excel(name = "经纬度")
    private String longitudeAndLatitude;

    @Excel(name = "告警内容")
    private String alarmBody;

    @Excel(name = "设备编码")
    private String deviceCode;

    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    @Excel(name = "通道编码")
    private String channelCode;

    @Excel(name = "通道名称")
    private String channelName;

    @Excel(name = "P位置")
    private String pan;

    @Excel(name = "T位置")
    private String tilt;

    @Excel(name = "Z位置")
    private String zoom;

    @Excel(name = "转义后P位置")
    private String ptzTransP;

    @Excel(name = "转义后T位置")
    private String ptzTransT;

    @Excel(name = "转义后Z位置")
    private String ptzTransZ;

    @Excel(name = " PTZ(原始)")
    private String ptzInitial;

    @Excel(name = " PTZ(转义)")
    private String ptzTrans;

    @Excel(name = "详细地址")
    private String address;

    @Excel(name = "告警编码")
    private String alarmCode;

    @Excel(name = "算法编码")
    private String algorithmCode;

    private List<String> algorithmCodeList;

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

    @Excel(name = "网格信息")
    private GridInfo gridInfo;

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

    @Excel(name = "告警压制原因")
    private String interceptName;

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

    @Excel(name = "行业应用名称")
    private String industryName;

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

    @Excel(name = "是否二次识别")
    private String isSecondIdentify;

    @Excel(name = "二次识别结果")
    private String secondIdentifyResult;

    @Excel(name = "联动算法")
    private String linkageAlgorithm;

    //物联设备编号/名称
    private List<String> iotDeviceCode;

    @Excel(name = "预置位ID")
    private String presetId;

    @Excel(name = "预置位名称")
    private String presetName;

    @Excel(name = "是否3D放大")
    private String enlargeFlag;

    @Excel(name = "事件描述")
    private String description;

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

    @Excel(name = "设备维度")
    private String videoLatitude;

    @Excel(name = "设备经度")
    private String videoLongitude;

    @Excel(name = "摄像机厂家")
    private String modelId;

    @Excel(name = "设备挂高")
    private String height;

    @Excel(name = "设备厂家")
    private String deviceFactoryName;

    @Excel(name = "摄像机厂家名称")
    private String modelFactoryName;

    @Excel(name = "物联设备型号")
    private String deviceModelCode;

    /**
     * 图版对象信息
     */
    @Excel(name = "图斑信息")
    private String polygonOperate;

    private transient List<Map> polygonOperateList;

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
     * 节点名称
     */
    private String platformName;

    /**
     * 设备型号
     */
    @Excel(name = "设备型号")
    private String deviceModel;


    private Integer pageNum;

    private Integer pageSize;
    /**
     * 告警状态
     */
    private List<String> orderStatusList;

    private String alarmTimeStart;

    private String alarmTimeEnd;

    private List<String> deviceCodeList;

    private String minChar;

    private String maxChar;

    private String reciveTime;

    private List<String> warningTypeIdList;

    private List<String> imgList;

    @JsonProperty("_class")
    private String className;
    @JsonIgnore
    private long alarmTimeDate;

    private String industryCode;

    private String msgTypeName;

    private String noticeResourceName;

    private String keeperName;

    private List<AreaCodeReq> areaCodeList;

    private SatelliteRelDeviceVo radarDevice;

    private List<DeviceInfoEntity> relDevice;

    private List<DeviceInfoEntity> deviceSiteList;

    private List<DeviceInfoEntity> satelliteDeviceList;

    @Excel(name = "物联设备联动摄像机信息")
    private String relDeviceVo;

    @Excel(name = "雷达设备联动摄像机信息")
    private String radarDeviceVo;

    @Excel(name = "雷达设备信息")
    private String deviceSiteListVo;

    @Excel(name = "卫星告警联动摄像机信息")
    private String satelliteDeviceListVo;

    /** 分布图 */
    private String relativePathPose;
    /** 专题图 */
    private String relativePathThem;
    /** 红外图 */
    private String relativePathMir;

    @Excel(name = "摄像机到事发地点距离")
    private String incidentDistance;

    /**
     * 是否是导出excel
     */
    private String isExport;


    private String alarmReason;

    private String devicePitch;

    private String deviceHorizontal;

    private String alarmLocation;

    @Excel(name = "扩展字段")
    private String extendFields;

    private RegionAlarmExtraField spot;

    /**
     * 多组物联设备信息以及关联的摄像机信息
     */
    private List<IotDeviceInfoVO> iotDevInfoList;

    /**
     * 第三方告警实际告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String realAlarmSource;
}

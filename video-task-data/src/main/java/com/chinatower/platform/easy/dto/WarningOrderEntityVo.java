package com.chinatower.platform.easy.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/30 14:44
 */

@Data
@ToString
@Document(indexName = "vm_warning_order")
public class WarningOrderEntityVo extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 事件ID
     */
    @ExcelProperty( "工单号")
    private String warningOrderId;

    /**
     * 告警类型编码
     */
    @ExcelProperty( "告警类型")
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    @ExcelProperty( "告警类型名称")
    private String warningTypeName;

    /**
     * 告警内容
     */
    @ExcelProperty( "告警内容")
    private String alarmBody;

    /**
     * 任务id
     */
    @ExcelProperty( "任务id")
    private String taskId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    @ExcelProperty( "告警来源")
    private String warningSource;

    /**
     * 告警来源名称
     */
    @ExcelProperty( "告警来源名称")
    private String warningSourceName;

    /**
     * 形成告警时间
     */
    @ExcelProperty( "告警时间")
    private String alarmTime;

    private Date alarmTimeDate;

    /**
     * 省份编码
     */
    @ExcelProperty( "省份编码")
    private String provinceId;

    /**
     * 地市编码
     */
    @ExcelProperty( "地市编码")
    private String cityId;

    /**
     * 县编码
     */
    @ExcelProperty( "县编码")
    private String countyId;

    /**
     * 乡镇编码
     */
    @ExcelProperty( "乡镇编码")
    private String townCode;

    /**
     * 所在省名称
     */
    @ExcelProperty( "省份名称")
    private String provinceName;

    /**
     * 所在市名称
     */
    @ExcelProperty( "市名称")
    private String cityName;

    /**
     * 县名称
     */
    @ExcelProperty( "县名称")
    private String countyName;

    /**
     * 乡镇名称
     */
    @ExcelProperty( "乡镇名称")
    private String townName;

    /**
     * 详细地址
     */
    @ExcelProperty( "详细地址")
    private String address;

    /**
     * 经度
     */
    @ExcelProperty( "经度")
    private String longitude;

    /**
     * 纬度
     */
    @ExcelProperty( "纬度")
    private String latitude;

    /**
     * 事件等级
     */
    @ExcelProperty( "事件等级")
    private String emergencyLevel;

    /**
     * 事件等级名称
     */
    @ExcelProperty( "事件等级名称")
    private String emergencyLevelName;
    /**
     * 租户ID
     */
    @ExcelProperty( "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @ExcelProperty( "租户名称")
    private String tenantName;

    /**
     * 设备编码
     */
    @ExcelProperty( "设备编码")
    private String deviceCode;

    /**
     * 通道编码
     */
    @ExcelProperty( "通道编码")
    private String channelCode;

    /**
     * 设备名称
     */
    @ExcelProperty( "设备名称")
    private String deviceName;

    /**
     * 通道名称
     */
    @ExcelProperty( "通道名称")
    private String channelName;

    /**
     * 告警编码
     */
    @ExcelProperty( "告警编码")
    private String alarmCode;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @ExcelProperty( "工单状态")
    private String orderStatus;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @ExcelProperty( "工单状态名称")
    private String orderStatusName;

    /**
     * 事件发现人姓名
     */
    @ExcelProperty( "发现人姓名")
    private String findName;

    /**
     * 事件发现人电话
     */
    @ExcelProperty( "发现人电话")
    private String findMobile;


    /**
     * 事件描述
     */
    @ExcelProperty( "事件描述")
    private String description;

    /**
     * 备注
     */
    @ExcelProperty( "备注")
    private String remark;

    /**
     * 语音
     */
    @ExcelProperty( "语音")
    private String voice;

    /**
     * 视频路径
     */
    @ExcelProperty( "视频路径")
    private String videoUrl;

    /**
     * 原图
     */
    @ExcelProperty( "原图")
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    @ExcelProperty( "框图")
    private String fileImgUrlIcon;

    /**
     * 网格id
     */
    @ExcelProperty( "网格id")
    private String gridId;

    /**
     * 网格名称
     */
    @ExcelProperty( "网格名称")
    private String gridName;

    /**
     * 网格中心坐标
     */
    @ExcelProperty( "网格中心坐标")
    private String gridCenter;

    /**
     * 网格范围
     */
    //@ExcelProperty( "网格绘制点位")
    private String gridRange;

    /**
     * 网格员姓名
     */
    @ExcelProperty( "网格员姓名")
    private String keeperName;

    /**
     * 网格员id
     */
    @ExcelProperty( "网格员id")
    private String keeperId;

    /**
     * 网格员联系电话
     */
    @ExcelProperty( "网格员联系电话")
    private String keeperPhoneNumber;

    /**
     * 研判结果 0：误判 1：真实 2：重复报警
     */
    @ExcelProperty( "研判结果")
    private String judgeResult;

    @ExcelProperty( "研判人")
    private String checkMan;

    @ExcelProperty( "研判说明")
    private String checkRemark;

    /**
     * 修改时间
     */
    @ExcelProperty( "修改时间")
    private String modifierDate;

    /**
     * 创建时间
     */
    @ExcelProperty( "创建时间")
    private String createTime;

    private Date createTimeDate;

    /**
     * 删除标记
     */
    @ExcelProperty( "删除标记")
    private String deleteFlag;

    @ExcelProperty( "删除人")
    private String deleteUser;

    @ExcelProperty( "删除说明")
    private String deleteRemark;

    /**
     * 所有流转人
     */
    @ExcelProperty( "流转记录人")
    private String userRecord;

    @ExcelProperty( "组织/部门id流转记录")
    private String orgRecord;

    /**
     * 研判时间
     */
    @ExcelProperty( "研判时间")
    private String judgeTime;

    /**
     *设备地址
     */
    @ExcelProperty( "设备地址")
    private String deviceAddress;

    /**
     * 流程ID
     */
    @ExcelProperty( "流程ID")
    private String flowId;

    /**
     * 行业编码
     */
    @ExcelProperty( "行业编码")
    private String industryCode;

    /**
     * 行业应用名称
     */
    @ExcelProperty( "行业应用名称")
    private String industryName;

    /**
     * 角色id
     */
    @ExcelProperty( "角色id")
    private String roleId;

    /**
     * 环节ID
     */
    @ExcelProperty( "环节id")
    private String linkId;

    /**
     * 设备经度
     */
    //@ExcelProperty( "设备经度")
    private String deviceLongitude;

    /**
     * 设备维度
     */
    //@ExcelProperty( "设备维度")
    private String deviceLatitude;

    /**
     * 设备挂高
     */
    @ExcelProperty( "设备挂高")
    private String height;

    /**
     * 设备厂商
     */
    @ExcelProperty( "设备厂商")
    private String modelCode;

    /**
     * 重复告警ID
     */
    @ExcelProperty( "重复告警ID")
    private String duplicateOrderId;

    /**
     * 行业版本编码
     */
    @ExcelProperty( "行业版本编码")
    private String appVerCode;


    /**
     * 报警参数
     */
    @ExcelProperty( "报警参数")
    private String alarmParam;

    /**
     * 报警参数值
     */
    @ExcelProperty( "报警参数值")
    private String alarmParamValue;

    /**
     * 行业扩展字段
     */
    @ExcelProperty( "行业拓展字段")
    private String extraField;

    /**
     * 事件重要等级
     */
    @ExcelProperty( "事件重要等级")
    private String importanceLevel;

    /** 水平方位角 */
    @ExcelProperty( "水平方位角")
    private String horiAzimuthAngle;

    /**
     * 摄像机厂家
     */
    @ExcelProperty( "摄像机厂家")
    private String modelId;

    /**
     * 重要等级名称
     */
    @ExcelProperty( "重要等级名称")
    private String importanceLevelName;

    /**
     * 摄像机厂家名称
     */
    @ExcelProperty( "摄像机厂家名称")
    private String modelFactoryName;

    /**
     * 物联设备型号
     */
    @ExcelProperty( "物联设备型号")
    private String deviceModelCode;

    /**
     * 置信度
     */
    @ExcelProperty( "置信度")
    private String confidenceLevel;

    /**
     * 最小置信度
     */
    @ExcelProperty( "最小置信度")
    private String confidenceLevelMin;

    /**
     * 综合置信度
     */
    @ExcelProperty( "综合置信度")
    private String compConfidenceLevel;

    /**
     * 设备维度
     */
    @ExcelProperty( "设备维度")
    private String videoLatitude;

    /**
     * 设备经度
     */
    @ExcelProperty( "设备经度")
    private String videoLongitude;

    /**
     * AI告警来源
     * 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
     */
    @ExcelProperty( "通知类型")
    private String noticeResource;

    /**
     * AI告警来源名称
     */
    @ExcelProperty( "通知类型名称")
    private String noticeResourceName;

    /**
     * 前端告警类型
     */
    @ExcelProperty( "前端告警类型")
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     */
    @ExcelProperty( "是否二次研判")
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
    @ExcelProperty( "二次研判结果")
    private String secondJudgeResult;

    /**
     * 是否二次识别 1-是；0-否
     */
    @ExcelProperty("是否二次识别")
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     */
    @ExcelProperty("二次识别结果")
    private String secondIdentifyResult;

    @ExcelProperty("联动算法")
    private String linkageAlgorithm;

    /**
     *前端告警图片
     */
    @ExcelProperty( "前端告警图片")
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    @ExcelProperty( "前端告警视频")
    private String frontAlarmVedioUrl;

    /**
     * 特征点P
     */
    @ExcelProperty( "特征点T")
    private String featurePan;

    /**
     * 特征点T
     */
    @ExcelProperty( "特征点T")
    private String featureTilt;

    /**
     * 上个环节是否指派（0：不是 1：是）
     */
    @ExcelProperty( "上个环节是否指派")
    private String beforedispatchflag;

    /**
     * 真实环节类型
     */
    @ExcelProperty( "真实环节类型")
    private String realLinkType;

    /**
     * 预置位id
     */
    @ExcelProperty( "预置位id")
    private String presetId;

    /**
     * 预置位名称
     */
    @ExcelProperty( "预置位名称")
    private String presetName;

    /**
     * 3D放大标识（0-否，1-是）
     */
    @ExcelProperty( "3D放大标识")
    private String enlargeFlag;

    @ExcelProperty( "最大置信度")
    private String confidenceLevelMax;

    private Double confidenceLevelMaxDouble;

    @ExcelProperty( "受理状态")
    private String enableAcceptanceMode;

    @ExcelProperty( "受理时限")
    private String timelimitacceptance;

    /**
     * 请求唯一标识
     */
    @ExcelProperty( "请求唯一标识")
    private String msgId;

    /**
     * 消息类型
     */
    @ExcelProperty( "消息类型")
    private String msgType;

    /**
     * 消息类型名称
     */
    @ExcelProperty( "消息类型名称")
    private String msgTypeName;

    /**
     * 转义后的P位置
     */
    @ExcelProperty( "转义后P位置")
    private String ptzTransP;

    /**
     * 转义后的T位置
     */
    @ExcelProperty( "转义后T位置")
    private String ptzTransT;

    /**
     * 转义后的Z位置
     */
    @ExcelProperty( "转义后Z位置")
    private String ptzTransZ;

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

    /**
     * 算法编码
     */
    @ExcelProperty( "算法编码")
    private String algorithmCode;

    /**
     * 环节名称
     */
    @ExcelProperty( "环节名称")
    private String linkName;

    /**
     * 火区面积
     */
    @ExcelProperty( "火区面积")
    private String fireArea;
    /**
     * 下垫面类型
     */
    @ExcelProperty( "下垫面类型")
    private String underlyingSurfaceType;

    /**
     * 转换后的水平方位角
     */
    @ExcelProperty( "转换后的水平方位角")
    private String transHoriAzimuthAngle;

    @ExcelProperty( "P位置")
    private String pan;

    @ExcelProperty( "T位置")
    private String tilt;

    @ExcelProperty( "Z位置")
    private String zoom;

    @ExcelProperty( "节点时间")
    private String flowNodeTime;

    private Date flowNodeTimeDate;

    @ExcelProperty( "处理人")
    private String processId;

    @ExcelProperty( "处理人名称")
    private String processName;

    @ExcelProperty( "部门id")
    private String groupId;

    @ExcelProperty( "创建人")
    private String creator;

    @ExcelProperty( "环节处理时限")
    private String timeLimit;

    @ExcelProperty( "虚假告警说明")
    private String falseAlarmRemark;

    @ExcelProperty( "流程模板")
    private String flowTemplate;

    @ExcelProperty( "告警流程配置名称")
    private String alarmFlowName;

    @ExcelProperty( "接入节点")
    private String accessNode;

    @ExcelProperty( "受理节点")
    private String acceptNode;

    @ExcelProperty( "节点名称")
    private String platformName;

    /**
     * 设备厂家名称
     */
    @ExcelProperty( "设备厂家名称")
    private String deviceFactoryName;

    /**
     * 图斑信息
     */
    @ExcelProperty( "图斑信息")
    private String polygonOperate;

    /**
     * 设备序列
     */
    //@ExcelProperty( "设备序列")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    //@ExcelProperty( "设备型号")
    private String deviceModel;

    /**
     * 告警对应的流转信息
     */
    @ExcelProperty( "流程信息")
    private String orderFlowRecordJson;

    @ExcelProperty("物联设备联动摄像机信息")
    private String relDeviceVo;

    @ExcelProperty("雷达设备联动摄像机信息")
    private String radarDeviceVo;

    @ExcelProperty("雷达设备信息")
    private String deviceSiteListVo;

    @ExcelProperty("卫星告警联动摄像机信息")
    private String satelliteDeviceListVo;

    @ExcelProperty("所有环节附件信息")
    private String filesVo;

    @ExcelProperty( "告警原因")
    private String alarmReason;
    @ExcelProperty( "摄像机俯仰角")
    private String devicePitch;
    @ExcelProperty( "摄像机水平角")
    private String deviceHorizontal;

    @ExcelProperty("扩展字段")
    private String extendFields;
}

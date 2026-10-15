package com.chinatower.platform.entity;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.entity.dto.AreaCodeReq;
import com.chinatower.event.entity.dto.RegionAlarmExtraField;
import com.chinatower.event.entity.vo.GridInfo;
import com.chinatower.event.entity.vo.IotDeviceInfoVO;
import com.chinatower.event.entity.vo.SatelliteRelDeviceVo;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/30 14:44
 */

@Data
public class WarningOrderEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 事件ID
     */
    @Excel(name = "工单号")
    private String warningOrderId;

    /**
     * 告警工单ID集合
     */
    private List<String> warningOrderIds;

    /**
     * 告警类型编码
     */
    @Excel(name = "告警类型")
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    @Excel(name = "告警类型名称")
    private String warningTypeName;

    /**
     * 告警内容
     */
    @Excel(name = "告警内容")
    private String alarmBody;

    @Excel(name = "接入节点")
    private String accessNode;

    /**
     * 节点名称
     */
    private String platformName;

    @Excel(name = "受理节点")
    private String acceptNode;

    /**
     * 任务id
     */
    @Excel(name = "任务id")
    private String taskId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    @Excel(name = "告警来源")
    private String warningSource;

    /**
     * 告警来源名称
     */
    @Excel(name = "告警来源名称")
    private String warningSourceName;

    /**
     * 形成告警时间
     */
    @Excel(name = "告警时间")
    private String alarmTime;

    @Excel(name = "接收时间")
    private String reciveTime;

    /**
     * 省份编码
     */
    @Excel(name = "省份编码")
    private String provinceId;

    /**
     * 地市编码
     */
    @Excel(name = "地市编码")
    private String cityId;

    /**
     * 县编码
     */
    @Excel(name = "县编码")
    private String countyId;

    /**
     * 乡镇编码
     */
    @Excel(name = "乡镇编码")
    private String townCode;

    /**
     * 所在省名称
     */
    @Excel(name = "省份名称")
    private String provinceName;

    /**
     * 所在市名称
     */
    @Excel(name = "市名称")
    private String cityName;

    /**
     * 县名称
     */
    @Excel(name = "县名称")
    private String countyName;

    /**
     * 乡镇名称
     */
    @Excel(name = "乡镇名称")
    private String townName;

    /**
     * 详细地址
     */
    @Excel(name = "详细地址")
    private String address;

    /**
     * 经度
     */
    @Excel(name = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度")
    private String latitude;

    @Excel(name = "经纬度")
    private String longitudeAndLatitude;

    /**
     * 事件等级
     */
    @Excel(name = "事件等级")
    private String emergencyLevel;

    /**
     * 事件等级名称
     */
    @Excel(name = "事件等级名称")
    private String emergencyLevelName;
    /**
     * 租户ID
     */
    @Excel(name = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @Excel(name = "租户名称")
    private String tenantName;

    /**
     * 设备编码
     */
    @Excel(name = "设备编码")
    private String deviceCode;

    /**
     * 通道编码
     */
    @Excel(name = "通道编码")
    private String channelCode;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    /**
     * 通道名称
     */
    @Excel(name = "通道名称")
    private String channelName;

    /**
     * 告警编码
     */
    @Excel(name = "告警编码")
    private String alarmCode;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @Excel(name = "工单状态")
    private String orderStatus;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @Excel(name = "工单状态名称")
    private String orderStatusName;

    /**
     * 事件发现人姓名
     */
    @Excel(name = "发现人姓名")
    private String findName;

    /**
     * 事件发现人电话
     */
    @Excel(name = "发现人电话")
    private String findMobile;


    /**
     * 事件描述
     */
    @Excel(name = "事件描述")
    private String description;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 语音
     */
    @Excel(name = "语音")
    private String voice;

    /**
     * 视频路径
     */
    @Excel(name = "视频路径")
    private String videoUrl;

    /**
     * 原图
     */
    @Excel(name = "原图")
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    @Excel(name = "框图")
    private String fileImgUrlIcon;

    /**
     * 网格id
     */
    @Excel(name = "网格id")
    private String gridId;

    /**
     * 网格名称
     */
    @Excel(name = "网格名称")
    private String gridName;

    /**
     * 网格中心坐标
     */
    @Excel(name = "网格中心坐标")
    private String gridCenter;

    /**
     * 网格范围
     */
    @Excel(name = "网格绘制点位")
    private String gridRange;

    /**
     * 网格长姓名
     */
    @Excel(name = "网格长姓名")
    private String keeperName;

    /**
     * 网格长id
     */
    @Excel(name = "网格长id")
    private String keeperId;

    /**
     * 网格长联系电话
     */
    @Excel(name = "网格长联系电话")
    private String keeperPhoneNumber;

    @Excel(name = "网格信息")
    private GridInfo gridInfo;

    /**
     * 研判结果 0：误判 1：真实 2：重复报警
     */
    @Excel(name = "研判结果")
    private String judgeResult;

    @Excel(name = "研判结果")
    private String judgeResultName;

    @Excel(name = "研判人")
    private String checkMan;

    @Excel(name = "研判完成时间")
    private String checkTime;

    @Excel(name = "研判说明")
    private String checkRemark;

    /**
     * 修改时间
     */
    @Excel(name = "修改时间")
    private String modifierDate;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间")
    private String createTime;

    /**
     * 删除标记
     */
    @Excel(name = "删除标记")
    private String deleteFlag;

    @Excel(name = "删除人")
    private String deleteUser;

    @Excel(name = "删除说明")
    private String deleteRemark;

    /**
     * 所有流转人
     */
    @Excel(name = "流转记录人")
    private String userRecord;

    @Excel(name = "组织/部门id流转记录")
    private String orgRecord;

    /**
     * 研判时间
     */
    @Excel(name = "研判时间")
    private String judgeTime;

    /**
     *设备地址
     */
    @Excel(name = "设备地址")
    private String deviceAddress;

    /**
     * 流程ID
     */
    @Excel(name = "流程ID")
    private String flowId;

    @Excel(name = "告警流程配置名称")
    private String alarmFlowName;

    /**
     * 行业编码
     */
    @Excel(name = "行业编码")
    private String industryCode;

    /**
     * 行业应用名称
     */
    @Excel(name = "行业应用名称")
    private String industryName;

    /**
     * 角色id
     */
    @Excel(name = "角色id")
    private String roleId;

    /**
     * 环节ID
     */
    @Excel(name = "环节id")
    private String linkId;

    /**
     * 设备经度
     */
    @Excel(name = "设备经度")
    private String deviceLongitude;

    /**
     * 设备维度
     */
    @Excel(name = "设备维度")
    private String deviceLatitude;

    /**
     * 设备挂高
     */
    @Excel(name = "设备挂高")
    private String height;

    /**
     * 设备厂商
     */
    @Excel(name = "设备厂商")
    private String modelCode;

    /**
     * 重复告警ID
     */
    @Excel(name = "重复告警ID")
    private String duplicateOrderId;

    /**
     * 行业版本编码
     */
    @Excel(name = "行业版本编码")
    private String appVerCode;


    /**
     * 报警参数
     */
    @Excel(name = "报警参数")
    private String alarmParam;

    /**
     * 报警参数值
     */
    @Excel(name = "报警参数值")
    private String alarmParamValue;

    /**
     * 行业扩展字段
     */
    @Excel(name = "行业拓展字段")
    private String extraField;

    /**
     * 事件重要等级
     */
    @Excel(name = "事件重要等级")
    private String importanceLevel;

    /** 水平方位角 */
    @Excel(name = "水平方位角")
    private String horiAzimuthAngle;

    /**
     * 摄像机厂家
     */
    @Excel(name = "摄像机厂家")
    private String modelId;

    /**
     * 重要等级名称
     */
    @Excel(name = "重要等级名称")
    private String importanceLevelName;

    /**
     * 摄像机厂家名称
     */
    @Excel(name = "摄像机厂家名称")
    private String modelFactoryName;

    /**
     * 物联设备型号
     */
    @Excel(name = "物联设备型号")
    private String deviceModelCode;

    /**
     * 置信度
     */
    @Excel(name = "置信度")
    private String confidenceLevel;

    /**
     * 最小置信度
     */
    @Excel(name = "最小置信度")
    private String confidenceLevelMin;

    /**
     * 综合置信度
     */
    @Excel(name = "综合置信度")
    private String compConfidenceLevel;

    /**
     * 设备维度
     */
    @Excel(name = "设备维度")
    private String videoLatitude;

    /**
     * 设备经度
     */
    @Excel(name = "设备经度")
    private String videoLongitude;

    /**
     * AI告警来源
     * 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
     */
    @Excel(name = "AI告警来源")
    private String noticeResource;

    /**
     * AI告警来源名称
     */
    @Excel(name = "AI告警来源名称")
    private String noticeResourceName;

    /**
     * 前端告警类型
     */
    @Excel(name = "前端告警类型")
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     */
    @Excel(name = "是否二次研判")
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
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
     *前端告警图片
     */
    @Excel(name = "前端告警图片")
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    @Excel(name = "前端告警视频")
    private String frontAlarmVedioUrl;

    /**
     * 特征点P
     */
    @Excel(name = "特征点T")
    private String featurePan;

    /**
     * 特征点T
     */
    @Excel(name = "特征点T")
    private String featureTilt;

    /**
     * 上个环节是否指派（0：不是 1：是）
     */
    @Excel(name = "上个环节是否指派")
    private String beforedispatchflag;

    /**
     * 真实环节类型
     */
    @Excel(name = "真实环节类型")
    private String realLinkType;

    /**
     * 预置位id
     */
    @Excel(name = "预置位id")
    private String presetId;

    /**
     * 预置位名称
     */
    @Excel(name = "预置位名称")
    private String presetName;

    /**
     * 3D放大标识（0-否，1-是）
     */
    @Excel(name = "3D放大标识")
    private String enlargeFlag;

    @Excel(name = "最大置信度")
    private String confidenceLevelMax;

    @Excel(name = "受理状态")
    private String enableAcceptanceMode;

    @Excel(name = "受理时限")
    private String timelimitacceptance;

    /**
     * 请求唯一标识
     */
    @Excel(name = "请求唯一标识")
    private String msgId;

    /**
     * 消息类型
     */
    @Excel(name = "消息类型")
    private String msgType;

    /**
     * 消息类型名称
     */
    @Excel(name = "消息类型名称")
    private String msgTypeName;

    /**
     * 转义后的P位置
     */
    @Excel(name = "转义后P位置")
    private String ptzTransP;

    /**
     * 转义后的T位置
     */
    @Excel(name = "转义后T位置")
    private String ptzTransT;

    /**
     * 转义后的Z位置
     */
    @Excel(name = "转义后Z位置")
    private String ptzTransZ;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    /**
     * 算法编码
     */
    @Excel(name = "算法编码")
    private String algorithmCode;

    private List<String> algorithmCodeList;

    /**
     * 环节名称
     */
    @Excel(name = "环节名称")
    private String linkName;

    /**
     * 告警压制
     */
    @Excel(name = "告警压制")
    private String intercept;

    /**
     *  告警压制原因
     */
    @Excel(name = "告警压制原因")
    private String interceptName;

    /**
     * 火区面积
     */
    @Excel(name = "火区面积")
    private String fireArea;
    /**
     * 下垫面类型
     */
    @Excel(name = "下垫面类型")
    private String underlyingSurfaceType;

    /**
     * 转换后的水平方位角
     */
    @Excel(name = "转换后的水平方位角")
    private String transHoriAzimuthAngle;

    @Excel(name = "P位置")
    private String pan;

    @Excel(name = "T位置")
    private String tilt;

    @Excel(name = "Z位置")
    private String zoom;

    @Excel(name = " PTZ(原始)")
    private String ptzInitial;

    @Excel(name = " PTZ(转义)")
    private String ptzTrans;

    @Excel(name = "节点时间")
    private String flowNodeTime;

    @Excel(name = "处理人")
    private String processId;

    @Excel(name = "处理人名称")
    private String processName;

    @Excel(name = "部门id")
    private String groupId;

    @Excel(name = "创建人")
    private String creator;

    @Excel(name = "环节处理时限")
    private String timeLimit;

    @Excel(name = "虚假告警说明")
    private String falseAlarmRemark;

    @Excel(name = "流程模板")
    private String flowTemplate;

    /**
     * 节点id
     */
    @Excel(name = "节点id")
    private String subSideId;

    /**
     * 设备厂家名称
     */
    @Excel(name = "设备厂家名称")
    private String deviceFactoryName;

    /**
     * 图斑信息
     */
    @Excel(name = "图斑信息")
    private String polygonOperate;

    private transient List<Map> polygonOperateList;

    /**
     * 设备序列
     */
    @Excel(name = "设备序列")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    @Excel(name = "设备型号")
    private String deviceModel;

    /**
     * 告警对应的流转信息
     */
    @Excel(name = "流程信息")
    private String orderFlowRecordJson;

    private Integer pageNum;

    private Integer pageSize;

    private List<String> orderStatusList;

    private List<String> deviceCodeList;

    /**
     * 物联设备编号/名称
     */
    private List<String> iotDeviceCode;

    private String alarmTimeStart;

    private String alarmTimeEnd;

    private String minChar;

    private String maxChar;

    private String orderId;

    private List<String> warningTypeIdList;

    private List<String> imgList;

    private List<AreaCodeReq> areaCodeList;

    private SatelliteRelDeviceVo radarDevice;

    /**
     * 所有带水印的附件
     */
    private transient List<Map> files;

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

    @Excel(name = "所有环节附件信息")
    private String filesVo;

    /** 分布图 */
    private String relativePathPose;
    /** 专题图 */
    private String relativePathThem;
    /** 红外图 */
    private String relativePathMir;

    @Excel(name = "摄像机到事发地点距离")
    private String incidentDistance;

    @Excel(name = "告警定位")
    private String alarmLocation;

    /**
     * P位置
     */
    private String ptzP;

    /**
     * T位置
     */
    private String ptzT;

    /**
     * Z位置
     */
    private String ptzZ;

    /**
     * 是否是导出excel
     */
    private String isExport;


    private String alarmReason;

    private String devicePitch;

    private String deviceHorizontal;

    @Excel(name = "扩展字段")
    private String extendFields;

    private RegionAlarmExtraField spot;

    /**流程信息*/
    private List<OrderFlowRecordEntity> flowList;

    /**
     * 多组物联设备信息以及关联的摄像机信息
     */
    private List<IotDeviceInfoVO> iotDevInfoList;

    /**
     * 第三方告警实际告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String realAlarmSource;
}

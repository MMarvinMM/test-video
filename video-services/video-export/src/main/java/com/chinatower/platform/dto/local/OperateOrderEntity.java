package com.chinatower.platform.dto.local;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:30
 */
@Data
public class OperateOrderEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Excel(name = "工单号")
    private String warningOrderId;

    @Excel(name = "告警来源")
    private String warningSource;

    @Excel(name = "告警来源名称")
    private String warningSourceName;

    @Excel(name = "任务id")
    private String taskId;

    @Excel(name = "租户id")
    private String tenantId;

    @Excel(name = "接入节点")
    private String accessNode;

    @Excel(name = "受理节点")
    private String acceptNode;

    @Excel(name = "租户名称")
    private String tenantName;

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

    @Excel(name = "告警类型")
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    @Excel(name = "告警类型名称")
    private String warningTypeName;

    @Excel(name = "告警时间")
    private String alarmTime;

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

    /**
     * 告警图片url
     */
    @Excel(name = "告警图片url")
    private String imgUrl;

    private String createTime;

    private String modifierTime;

    private String deleteFlag;

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

    /**
     * 重复报警事件ID
     */
    private String duplicateOrderId;

    /**
     * 虚假告警说明
     */
    private String falseAlarmRemark;

    @Excel(name = "研判人ID")
    private String createUser;

    @Excel(name = "研判人姓名")
    private String createUserName;

    @Excel(name = "研判结果")
    private String judgeResult;

    /**
     * 研判时间
     */
    @Excel(name = "研判时间")
    private String judgeTime;

    @Excel(name = "网格ID")
    private String gridId;

    @Excel(name = "网格名称")
    private String gridName;

    /**
     * 网格员姓名
     */
    @Excel(name = "网格员姓名")
    private String gridUserName;

    @Excel(name = "视频路径")
    private String videoUrl;

    /**
     * 框图
     */
    @Excel(name = "框图")
    private String fileImgUrlIcon;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    @Excel(name = "告警重要等级")
    private String importanceLevel;

    /**
     * 重要等级名称
     */
    @Excel(name = "重要等级名称")
    private String importanceLevelName;

    /**
     * 来源：0-铁塔；1-客户
     */
    @Excel(name = "来源标识")
    private String source;

    /**
     * 运营研判标识：1:串行 2:并行
     */
    @Excel(name = "运营研判标识")
    private String operateType;

    @Excel(name = "行业应用编码")
    private String industryCode;

    @Excel(name = "行业应用名称")
    private String industryName;

    private String appVerCode;

    @Excel(name = "行业扩展字段")
    private String extraField;

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

    /**
     * 前端告警图片
     */
    @Excel(name = "前端告警图片")
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    @Excel(name = "前端告警视频")
    private String frontAlarmVedioUrl;

    @Excel(name = "水平方位角")
    private String horiAzimuthAngle;

    private String msgId;

    /**
     * 前端告警类型
     */
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

    @Excel(name = "预置位ID")
    private String presetId;

    /**
     * 预置位名称
     */
    @Excel(name = "预置位名称")
    private String presetName;

    /**
     * 3D放大标识（0-否，1-是）
     */
    @Excel(name = "是否3D放大")
    private String enlargeFlag;

    /**
     * 研判结果标签（0：算法问题 1：非算法问题）
     */
    @Excel(name = "研判结果标签")
    private String resultTab;

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
     * 设备纬度
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

}

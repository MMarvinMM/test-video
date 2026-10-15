package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *  告警工单公共类
 * </p>
 *
 * @author lyy
 * @since 2023/03/29
 */
@Data
public class BaseWarningOrderDO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警单ID(yyyyMM+租户id+)
     * @required true
     */
    @TableId
    private String warningOrderId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;

    /**
     * 上报类型：1-PC上报；2-APP上报
     */
    private String reportType;

    /**
     * 第三方任务ID
     */
    private String taskId;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 县编码
     */
    private String countyCode;

    /**
     * 县名称
     */
    private String countyName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

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
     * 备注
     */
    private String remark;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 语音
     */
    private String voice;

    /**
     * 当前环节ID
     */
    private String linkId;

    /**
     * 当前环节名称
     */
    private String linkName;

    /**
     * 当前环节开始时间
     */
    private Date flowNodeTime;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 小组id，组织编码
     */
    private String groupId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 完成时限;单位：分钟
     */
    private Integer timeLimit;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 事件等级，关联枚举值表
     */
    private String emergencyLevel;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;

    /**
     * 重复报警事件ID
     */
    private String duplicateOrderId;

    /**
     * 虚假告警说明
     */
    private String falseAlarmRemark;

    /**
     * 流程模板配置
     */
    private String flowTemplate;

    /**
     * 事件发现人姓名
     */
    private String findName;

    /**
     * 事件发现人电话
     */
    private String findMobile;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格编码
     */
    private String gridCode;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 视频路径
     */
    private String videoUrl;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    private String fileImgUrlIcon;

    /**
     * 研判结果 0：误判 1：真实 2：重复报警
     */
    private String judgeResult;

    /**
     * 最后更新时间
     */
    private Date modifierDate;

    /**
     * 用户id流转记录，用','拼接
     */
    private String userRecord;

    /**
     * 组织/部门id流转记录，用','拼接
     */
    private String orgRecord;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;


}

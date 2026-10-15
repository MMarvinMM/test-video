package com.chinatower.event.entity.dto;

import java.io.Serializable;
import java.util.List;

import com.chinatower.common.core.dto.OrderFileDTO;
import com.chinatower.common.core.dto.ProcessApproveDTO;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TradeSendAlarmOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/22 09:45
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TradeSendAlarmOrderDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 行业应用编码
     * @required false
     */
    private String tradeId;

    private String orderId;

    /**
     * 告警事件编码
     * @required true
     */
    private String warningOrderId;

    /**
     * 告警类型编码
     * @required true
     */
    private String warningTypeId;

    /**
     * 流程ID
     * @required false
     */
    private String flowId;

    /**
     * 当前环节ID
     * @required true
     */
    private String currentLink;

    /**
     * 环节ID（即将流转到哪个环节传哪个环节的ID）
     * @required true
     */
    private String linkId;

    /**
     * 环节名称
     * @required true
     */
    private String linkName;

    /**
     * 处理状态 0处理中，1处理完成，2无法处理（与linkId相同）
     * @required false
     */
    private String orderStatus;

    /**
     * 用户类型（1用户，2角色）
     * @required false
     */
    private String userType;

    /**
     * 消息通知类型，1：APP(废弃，走APP用户维度配置), 2：短信, 3：PC（多种方式中间用逗号隔开）
     * @required false
     */
    private String msgNotifyType;

    /**
     * 是否会签（0:不会签  1:会签）
     * @required true
     */
    private String isSign;

    /**
     * 处理时限，单位：分钟
     * @required false
     */
    private String timeLimit;

    /**
     * 事件等级，关联枚举值表
     * @required false
     */
    private String emergencyLevel;

    /**
     * 是否流转（1：流转下个环节，0：不流转）
     * @required true
     */
    private String isNextLink;

    /**
     * 下一环节处理人
     * 会签时，会有多个processId、processName
     * @required false
     */
    private List<ProcessDTO> processDTOList;

    /**
     * 是否回退流程（1：回退流程，当为回退流程时必传）
     * @required false
     */
    private String rollBack;

    /**
     * 告警类型
     * @required false
     */
    private String alarmType;

    /**
     * 角色ID，多个中间用逗号隔开
     * @required false
     */
    private String roleId;

    /**
     * 备注
     * @required false
     */
    private String remark;

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 省份名称
     * @required false
     */
    private String provinceName;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 地市名称
     * @required false
     */
    private String cityName;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

    /**
     * 区县名称
     * @required false
     */
    private String countyName;

    /**
     * 详细地址
     * @required false
     */
    private String address;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    /**
     * 研判结果（0：误判 1：真警 2：重复告警 3：待研判）
     * @required true
     */
    private String judgeResult;

    /**
     * 重复告警ID
     * @required true
     */
    private String duplicateOrderId;

    /**
     * 语音
     * @required false
     */
    private String voice;

    /**
     * 处置信息
     * @required false
     */
    private List<ProcessOrderDTO> processOrderVOList;

    /**
     * 核实信息
     * @required false
     */
    private List<ProcessApproveDTO> orderApproveVOList;

    /**
     * 是否指挥调度 1：是
     */
    private String isSchedulingCommand;

    /**
     * 上传附件
     * @required false
     */
    private List<OrderFileDTO> orderFileDTOList;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     * @required true
     */
    private String realLinkType;

    /**
     * 流转类型（0默认、1转派、2回退）
     * @required true
     */
    private String flowLinkType;

    /**
     * 流程环节属性ID
     * @required true
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启<待受理> 2:已受理）
     * 注：取详情接口返回的事件该字段
     * @required true
     */
    private String enableAcceptanceMode;

    /**
     * 受理时限
     * @required true
     */
    private String timeLimitAcceptance;

    /**
     * 下一环节是否开启受理模式（0:不开启 1:开启）
     * @required true
     */
    private String nextEnableAcceptanceMode;

    /**
     * 网格ID
     * @required false
     */
    private Long gridId;

    /**
     * 网格名称
     * @required false
     */
    private String gridName;

    /**
     * 水平方位角
     * @required false
     */
    private String horiAzimuthAngle;

    /**
     * 是否修改网格信息
     * @required false
     */
    private String checkGridFlag;


    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 告警原因
     */
    private String alarmReason;

    private String deviceHorizontal;
}

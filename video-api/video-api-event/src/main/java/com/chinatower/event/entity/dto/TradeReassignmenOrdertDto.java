package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.OrderFileDTO;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TradeReassignmenOrdertDto extends BaseDataScopeEntity implements Serializable {

    /**
     * 告警流程ID
     * @required true
     */
    private String alarmFlowId;

    /**
     * 行业应用版本
     * @required true
     */
    private String appVerCode;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;

    /**
     * 转派前环节ID
     * @required true
     */
    private String beforLink;

    /**
     * 流程模板ID
     * @required true
     */
    private String flowTemplate;

    /**
     * 转派后环节ID
     * @required true
     */
    private String curLink;

    /**
     * 转派后环节别名
     * @required true
     */
    private String linkName;

    /**
     * 转派后的真实环节类型（1研判2调度4处置5核实6办结7开始）
     * @required true
     */
    private String realLinkType;

    /**
     * 告警事件编码
     * @required true
     */
    private String warningOrderId;

    /**
     * 转派描述
     * @required true
     */
    private String remark;

    /**
     * 转派用户类型（角色：2 用户：1）
     * @required false
     */
    private String userType;

    /**
     * 转派后消息通知类型
     * @required false
     */
    private String msgNotifyType;

    /**
     * 录音
     * @required false
     */
    private String voice;

    /**
     * 转派所选择用户的集合（用户userID,用户名称）
     * @required true
     */
    List<ProcessDTO> processDTOList;

    /**
     * 事发地点
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
     * 网格id
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
     * 是否修改网格信息（1-是）
     * @required false
     */
    private String checkGridFlag;

    /**
     * 告警类型编码
     * @required false
     */
    private String warningTypeId;

    /**
     * 环节处理时限
     * @required false
     */
    private String timeLimit;

    /**
     * 流程环节属性ID
     * @required false
     */
    private String attrId;

    /**
     * 受理状态（取详情接口返回的事件该字段（0：不开启 1：开启（待受理）2：已受理 3：受理完成）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 受限时限
     * @required false
     */
    private String timeLimitAcceptance;

    /**
     * 下一环节是否开启受理模式（0:不开启 1:开启）
     * @required false
     */
    private String nextEnableAcceptanceMode;

    private String cityId;

    private String provinceId;

    private String countyId;

    /**
     * 附件
     */
    private List<OrderFileDTO> orderFileDTOList;

    /**
     * 处置环节-上传附件
     */
    private List<ProcessOrderDTO> processOrderVOList;

    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 告警原因
     */
    private String alarmReason;

    private String deviceHorizontal;

    /**
     * 告警等级
     */
    private String importanceLevel;
}

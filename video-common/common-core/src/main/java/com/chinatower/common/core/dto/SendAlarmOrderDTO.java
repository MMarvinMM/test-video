package com.chinatower.common.core.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: SendAlarmOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/15 17:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class SendAlarmOrderDTO{

    private String orderId;

    private String warningOrderId;

    private String warningTypeId;

    private String groupId;

    private String tenantId;

    private String closeOrderDate;

    private int orderNumber;

    private String orderStatus;

    private String processLevel;

    private String checkMan;

    private String sendMan;

    private String createDate;

    private String modifierDate;

    private String deleteFlag;

    private List<ProcessOrderDTO> processOrderVOList;

    private List<ProcessApproveDTO> orderApproveVOList;

    private String operator;

    private String operatorName;

    private String ifTimeOut;

    private String countyId;

    private String countyName;

    private String beginDate;

    private String endDate;

    private String imgSffUrl;

    private String alarmTime;

    private String orderStatusName;

    private String warningTypeName;

    private String tenantName;

    private int pageNum;

    private int pageSize;

    private String sendTime;

    private String checkTime;

    private String[] userRoleStr;

    private List orderStatusStr;

    private String provinceId;

    private String provinceName;

    private String cityId;

    private String cityName;

    private String judgeResult;

    private String checkRemark;

    private String sendRemark;

    private String sendType;

    private String processFlag;

    private String approveFlag;

    private String creator;

    private String groupName;

    private String handleEndTime;

    private String alarmLocation;

    private String custName;

    private List groupList;

    private String checkRole;
    private String sendRole;
    private String approveRole;
    private String processRole;


    private String ifCurrentLink;

    private String linkId;

    private String linkName;

    private String sendOrderTime;

    private String processOrderStatus;

    private String approveTime;

    private String approveStatusFlag;


    private String dateFlag;

    private String flowNodeTime;

    private String flowEndTime;

    private String processId;

    private String processName;

    private String processOrderTime;

    private String processOrderStatusFlag;

    private String approvePerson;

    private String duration;


    private String findName;

    private String findMobile;

    private Long gridId;

    private String gridName;

    private String keeperId;

    private String keeperName;

    private String falseAlarmRemark;

    private String expectTime;

    private String duplicateOrderId;

    private String emergencyLevel;


    private String address;
    private String longitude;
    private String latitude;

    // 配置的流程内容
    private String flowTemplate;

    // 当前环节
    private String currentLink;

    // 当前环节
    private String curLink;

    // 操作之前的linkId
    private String beforLink;

    private String alarmBody;

    // 20210708 联合办理数据对象
    private List<Map> joints;

    // 1联合办理流程
    private String flowType;

    private String voice;

    private String tableName;

    private Integer timeLimit;

    private String userRecord;

    private String orgRecord;

    //1:5分钟不再告警 2:1小时不再告警 3:今日不再告警 4:自定义
    private String intervalType;
    //自定义压制时间（分钟）
    private String intervalTime;

    /**
     * 流程id
     */
    private String flowId;

    /** 名称 */
    private String flowName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否流转流程
     * 1：流转下个环节
     * 0：不流转
     */
    private String isNextLink;

    /**
     * 是否会签0:不会签  1:会签
     */
    private String isSign;

    /**
     * 下一环节处理人
     * 会签时，会有多个processId、processName
     */
    private List<ProcessDTO> processDTOList;

    /**
     * 用户类型，1用户，2角色
     */
    private String userType;

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String msgNotifyType;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 角色ID，中间用逗号隔开
     */
    private String roleId;

    /**
     * 是否回退流程
     */
    private String rollBack;

    /**
     * 是否指挥调度 1：是
     */
    private String isSchedulingCommand;
    /**
     * 附件
     */
    private List<OrderFileDTO> orderFileDTOList;

    /**
     * 水平方位角
     */
    private String horiAzimuthAngle;
    /**
     * 行业信息id
     */
    private String industryCode;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;

    /**
     * 是否是调度操作（1：是  其他：不是）
     */
    private String beforeDispatchFlag;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 告警类型
     */
    private String warningSource;

    /**
     * 流转类型（0默认、 1转派、2回退 3受理）
     */
    private String flowLinkType;

    /**
     * 真实环节id
     */
    private String realLinkType;

    /**
     * 1：从关闭告警过来
     */
    private String fromCloseFlag;

    /**
     * 重要等级
     */
    private String importanceLevel;

    /**
     * 是否修改网格信息
     */
    private String checkGridFlag;

    /**
     * 推送极光传入app过滤的环节结束时间
     */
    private String flowOverTime;

    /**
     * 流程环节属性id
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     */
    private String enableAcceptanceMode;

    /**
     * 受限时限
     */
    private String timeLimitAcceptance;

    /**
     * 下一环节是否开启受理模式0:不开启 1:开启）
     */
    private String nextEnableAcceptanceMode;

    /**
     * 关闭说明
     */
    private String closeRemark;

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

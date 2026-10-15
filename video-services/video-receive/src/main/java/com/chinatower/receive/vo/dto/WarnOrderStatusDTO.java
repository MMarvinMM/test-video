package com.chinatower.receive.vo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WarnOrderStatusDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/6/8 10:59
 * @Version 1.0
 **/
@Data
public class WarnOrderStatusDTO implements Serializable {
    
    //告警单ID
    private String warningOrderId;
    
    //流程ID
    private String flowId;
    
    //租户Id
    private String tenantId;
    
    //告警内容
    private String alarmBody;
    
    //用户id流转记录
    private String userRecord;
    
    //组织/部门id流转记录
    private String orgRecord;
    
    //告警类型编码
    private String warningTypeId;
    
    //工单状态
    private String orderStatus;
    
    //当前环节名称
    private String linkName;

    //当前环节Id
    private String linkId;
    
    //告警时间
    private String alarmTime;
    
    //创建时间
    private String createTime;
    
    //省编码
    private String provinceId;
    
    //市编码
    private String cityId;
    
    //县编码
    private String countyId;
    
    //地址
    private String address;
    
    //经度
    private String longitude;
    
    //纬度
    private String latitude;
    
    //告警等级
    private String emergencyLevel;
    
    /**
     * 设备编码
     */
    private String deviceCode;
    
    /**
     * 通道编码
     */
    private String channelCode;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 通道名称
     */
    private String channelName;
    
    /**
     * 告警编码
     */
    private String alarmCode;
    
    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;
    
    /**
     * 流程模板配置
     */
    private String flowTemplate;
    
    /**
     * 处理人
     */
    private String processId;
    
    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 完成时限;单位：分钟
     */
    private String timeLimit;

    /**
     * 行业版本
     */
    private String tradeVerCode;

    /**
     * 行业版本编码
     */
    private String industryCode;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

    /**
     * 研判结果
     */
    private String judgeResult;

    /**
     * 网格id
     */
    private String gridId;

    private String enableAcceptanceMode;

    /**
     * 行业扩展字段
     */
    private String extraField;
}

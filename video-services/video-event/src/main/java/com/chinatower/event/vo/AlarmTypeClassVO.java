package com.chinatower.event.vo;

import lombok.Data;

import java.util.Date;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmTypeClassVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/24 20:57
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmTypeClassVO {

    private String typeClassId;

    private String alarmTypeId;
    private String typeValue;

    private String alarmLevel;

    private String alarmLevelName;

    private String state;

    private Integer alarmTypeCount;

    private String remarks;

    private String createBy;

    private Date createTime;

    private String deleteFlag;

    private String noticeMode;

    private String source;

    private String tenantor;

    private String provinceId; //省 编码
    private String provinceName; //省 名称
    private String cityId; //市编码
    private String cityName; //市名称
    private String userName; //客户名称
    private String tenantId; //客户Id

    private String alarmTypeName;
    private String icon;
    private String id;

    private String notifyManager;//短信通知 是否通知管理员
    private String notificationLink;//短信通知 通知环节
    
    //告警标签名称集合
    private String tagNames;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    private String appVerName;

    /**
     * 行业编码
     */
    private String industryCode;

    private String industryName;

    private String isVisible;

    /**
     * 大模型算法标识 true-大模型算法
     */
    private Boolean modelAlgorithmFlag;
}

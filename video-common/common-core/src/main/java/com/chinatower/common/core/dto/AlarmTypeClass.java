package com.chinatower.common.core.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description 告警分类
 * @author wjd
 * @Date 15:41 2021/7/7
 * @Param
 **/
@Data
public class AlarmTypeClass {
    private String typeClassId;

    private String alarmTypeId;

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
}

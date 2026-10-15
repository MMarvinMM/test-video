package com.chinatower.event.vo;

import java.util.Date;

import lombok.Data;

@Data
public class FlowTemplateVO {

    private String templateId; //模板ID

    private String templateName; //模板名称

    private String templateType; //模板类型：1-AI上报； 2-人工上报；3-一键告警

    private String templateRemark; //模板说明

    private String flow; //工作流程，用,分割

    private String createUser; //创建人

    private Date createTime; //创建时间

    private Date updateTime; //更新时间

    private String deleteFlag; //0-停用；1-启用

    private String ifDefault; //是否为默认流程：1-是；0-否

    private String userName;

    private String createTimeF;


    private String eventLevelCode; //事件等级code
    private String typeValue; //事件类型值
    private String tenantId;  //租户ID
}

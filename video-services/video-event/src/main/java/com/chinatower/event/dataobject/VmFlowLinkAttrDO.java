package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 流程环节属性表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_flow_link_attr")
public class VmFlowLinkAttrDO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /** 模板ID */
    private Long templateId;
    /** 当前环节Id */
    private Integer linkId;
    /** 告警流程配置ID */
    private Long alarmFlowId;
    /** 用户类型，1用户，2角色 */
    private String userType;
    /** 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗号隔开 */
    private String msgNotifyType;
    /** 短信通知间隔 单位分钟 */
    private Integer msgNotifyLimit;
    /** 短信通知间隔 单位 1：分钟，2：小时*/
    private String msgNotifyCompany;
    /** 转领导批示，1:：是，0：否 */
    private String leaderFlag;
    /** 会签标识，0，不会签，1会签 */
    private String signFlag;
    /** 回退标识 0-不回退，1-可回退 */
    private String rollbackFlag;
    /** 时限，单位分钟 */
    private Integer timeLimit;
    /** 是否通知管理员，0，不通知，1通知 */
    private String notifyAdminFlag;
}

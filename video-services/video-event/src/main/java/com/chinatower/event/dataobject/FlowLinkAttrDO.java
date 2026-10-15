package com.chinatower.event.dataobject;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 流程视图环节属性表
 * </p>
 *
 * @author AutoGen
 * @since 2022-06-13
 */
@Getter
@Setter
@TableName("vm_flow_link_attr")
public class FlowLinkAttrDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "flow_link_id", type = IdType.AUTO)
    private Long flowLinkId;

    /**
     * 告警流程配置ID
     */
    private Long alarmFlowId;

    /**
     * 模板ID
     */
    private Long flowId;

    /**
     * 当前环节
     */
    private Long curLink;

    /**
     * 环节属性，1开始环节，2中间环节，3结束环节
     */
    private String linkType;

    /**
     * 上一个环节
     */
    private String preLink;

    /**
     * 下一个环节
     */
    private String nextLink;

    /**
     * 组织ID, 中间用用逗号隔开
     */
    private String orgId;

    /**
     * 用户类型，1用户，2角色
     */
    private String userType;

    /**
     * 角色ID，中间用逗号隔开
     */
    private String roleId;

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String msgNotifyType;

    /**
     * 是否通知领导，0，不通知，1通知
     */
    private String leaderFlag;

    /**
     * 会签标识，0，不会签，1会签
     */
    private String signFlag;

    /**
     * 回退标识
     */
    private String rollbackFlag;

    /**
     * 时限，单位分钟
     */
    private Integer timeLimit;

    /**
     * 0为默认属性，1为算法配置属性
     */
    private String attrFlag;

    /**
     * 是否通知管理员，0，不通知，1通知
     */
    private String notifyAdminFlag;


}

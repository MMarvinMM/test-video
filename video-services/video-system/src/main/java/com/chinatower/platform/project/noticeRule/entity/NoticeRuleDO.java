package com.chinatower.platform.project.noticeRule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息提醒规则表
 * </p>
 *
 * @author
 * @since
 */
@Getter
@Setter
@TableName("vm_notice_rule_t")
public class NoticeRuleDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 0:默认规则 1:用户规则
     */
    private String ruleType;

    /**
     * 用户编码
     */
    private String userId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 版本编码
     */
    private String appVerCode;

    /**
     * 是否开启消息通知 0:关闭 1:开启
     */
    private String isNotice;

    /**
     * 告警类型json串
     */
    private String alarmTypeText;

    /**
     * 拓展信息json串
     */
    private String extendInfoText;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    /**
     * 删除状态: 0正常，1删除
     */
    private String deleteFlag;


}

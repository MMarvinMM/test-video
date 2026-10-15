package com.chinatower.platform.entity.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_msg_switch")
public class SysMsgSwitch {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField(value = "user_id")
    private String userId;

    /**
     * 铁塔用户使用 运营研判提醒 1-开启  0-关闭
     */
    @TableField(value = " alarm_switch")
    private Integer alarmSwitch;

    /**
     * 超期提醒 1-开启  0-关闭
     */
    @TableField(value = " alarm_overdue_switch")
    private Integer alarmOverdueSwitch;

    /**
     * 公告 1-开启  0-关闭
     */
    @TableField(value = " notice_switch")
    private Integer  noticeSwitch;

    /**
     * 其他 1-开启  0-关闭
     */
    @TableField(value = " other_switch")
    private Integer  otherSwitch;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(value = "industry_code")
    private String industryCode;

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @TableField(value = "update_by")
    private String updateBy;

    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
}

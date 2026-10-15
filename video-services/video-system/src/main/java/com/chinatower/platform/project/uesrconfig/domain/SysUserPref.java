package com.chinatower.platform.project.uesrconfig.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户设置偏好表
 *
 * @TableName sys_user_pref
 */
@TableName(value = "sys_user_pref")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserPref implements Serializable {
    /**
     * 表id
     */
    @TableId(value = "sys_user_pref_id", type = IdType.AUTO)
    private Long sysUserPrefId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 设置类型1地图模式2设备树展示模式3是否展示引导4是否展示订购
     */
    @TableField(value = "pref_type")
    private String prefType;

    /**
     * page:1全景2告警3视频4算法；
     */
    @TableField(value = "pref_page")
    private String prefPage;

    /**
     * value:0否1是;1常规地图2卫星地图3深色地图;1区域2组织;1区域2组织
     */
    @TableField(value = "pref_value")
    private String prefValue;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 删除标识 正常0  删除1
     */
    @TableField(value = "delete_flag")
    private String deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
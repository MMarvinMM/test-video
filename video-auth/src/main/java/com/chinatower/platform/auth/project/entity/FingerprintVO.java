package com.chinatower.platform.auth.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户设备指纹登录开关实体类
 *
 * @author system
 * @date 2026-05-11
 */
@Data
public class FingerprintVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设备唯一标识
     */
    private String deviceId;

    /**
     * 是否开启指纹登录 0-关闭 1-开启
     */
    private Integer fingerprintEnabled;

    /**
     * 开启时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enabledTime;

    /**
     * 最近指纹登录使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUsedTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 删除标识 正常0 删除1
     */
    private Integer deleteFlag;

    /**
     * 用户名（指纹登录时使用）
     */
    private String username;

    /**
     * APP标识（指纹登录时使用）
     */
    private String appId;

    /**
     * 密码（确认密码时使用）
     */
    private String password;

    /**
     * 是否置灰 true置灰 false不置灰
     */
    private boolean ifGray;

    /**
     * 是否显示弹框 true置灰 false不置灰
     */
    private boolean ifShowDialog;

    /**
     * 成功标识 1.成功;2 5次之上（含5次）的失败;3 5次以内的失败
     */
    private String flag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pwdUpdateTime;

}
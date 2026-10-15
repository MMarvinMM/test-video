package com.chinatower.platform.auth.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SSO密钥同步记录
 */
@Data
@TableName("sso_secret_sync")
public class SsoSecretSyncRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String appId;

    private String appSecret;

    private String appSecretOld;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}

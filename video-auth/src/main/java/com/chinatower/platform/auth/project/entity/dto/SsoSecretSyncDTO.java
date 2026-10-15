package com.chinatower.platform.auth.project.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * SSO密钥同步入参
 */
@Data
public class SsoSecretSyncDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;

    private String appSecret;

    private String appSecretOld;

    private String updateTime;
}

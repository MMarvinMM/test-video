package com.chinatower.platform.auth.project.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * SSO密钥同步出参（rsp: 0成功 1失败）
 */
@Data
public class SsoSecretSyncResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rsp;

    private String errDesc;
}

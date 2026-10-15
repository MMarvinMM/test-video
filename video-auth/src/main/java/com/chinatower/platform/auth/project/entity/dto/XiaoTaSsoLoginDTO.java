package com.chinatower.platform.auth.project.entity.dto;

import lombok.Data;

/**
 * 小塔单点登录入参
 */
@Data
public class XiaoTaSsoLoginDTO {

    /**
     * 登录账号（4A LOGINACCT）
     */
    private String username;

    /**
     * 登录后跳转页面编码，可空，空时用配置的默认值
     */
    private String redirectCode;
}

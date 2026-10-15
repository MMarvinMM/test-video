package com.chinatower.platform.auth.project.entity.dto;

import lombok.Data;

/**
 * 小塔单点登录出参
 */
@Data
public class XiaoTaSsoResultVO {

    /**
     * 重定向地址，前端引导用户跳转
     */
    private String redirectUrl;

    /**
     * 提示信息
     */
    private String msg;
}

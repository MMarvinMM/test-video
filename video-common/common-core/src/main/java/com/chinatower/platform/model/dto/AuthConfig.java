package com.chinatower.platform.model.dto;

import lombok.Data;

/**
 * @author qq on 2023/4/20
 */
@Data
public class AuthConfig {

    /**
     * web token失效时间，默认24小时
     */
    private int webTokenValiditySeconds = 86400;

    /**
     * app token失效时间，默认24小时
     */
    private int appTokenValiditySeconds = 86400;

    /**
     * web 刷新token失效时间，默认25小时
     */
    private int webRefreshTokenValiditySeconds = 90000;

    /**
     * app 刷新token失效时间，默认30天
     */
    private int appRefreshTokenValiditySeconds = 2592000;

}

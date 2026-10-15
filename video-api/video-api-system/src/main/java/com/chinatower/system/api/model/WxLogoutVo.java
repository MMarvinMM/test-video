package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信登出
 *
 * @author dencycheng
 */
@Data
public class WxLogoutVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;
}

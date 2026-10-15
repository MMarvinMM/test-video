package com.chinatower.platform.auth.model;

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

    private String userId;

    private String userName;
}

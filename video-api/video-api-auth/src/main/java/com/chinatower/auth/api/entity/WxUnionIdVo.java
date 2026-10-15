package com.chinatower.auth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxUnionIdVo implements Serializable {

    /**
     * 微信开放平台绑定的用户ID
     */
    private String unionId;

    /**
     * 微信公众号openId
     */
    private String openId;

    /**
     * 微信小程序openId
     */
    private String miniOpenId;

    /**
     * 微信昵称
     */
    private String nickName;

    private String code;

    private String source;
}

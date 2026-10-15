package com.chinatower.platform.auth.model;

import lombok.Data;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;

import java.util.List;
import java.util.Map;

@Data
public class WxLoginVo extends LoginVo {

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

    /**
     * 用户名
     */
    private String userName;

    /**
     * 微信小程序会话密钥
     */
    private String sessionKey;

    /**
     * 微信公众号接收消息提醒开关
     */
    private String receiveMsg;

    /**
     * 绑定过的账户信息
     */
    private List<Map<String,String>> bindUserNames;

    /**
     * 属地编码
     */
    private String colonyCode;
}

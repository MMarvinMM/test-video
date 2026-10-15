package com.chinatower.bdm.modules.QueryAnswer.entity;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.io.Serializable;
import java.util.Map;

@Data
public class QueryAnswerLoginVO {

    /**
     * token
     */
    private String accessToken;

    /**
     * token类型
     */
    private String tokenType;

    /**
     * 过期时间
     */
    private String expiresIn;

    /**
     * 认证范围
     */
    private String scope;

    /**
     * 用户id
     */
    private String tenantId;

    /**
     * 租户id
     */
    private String userId;

    /**
     * 授权类型-授权类型，目前只支持client_credentials
     */
    private String grantType;

    /**
     * 重定向地址-组件使用，不需要关注
     */
    private String redirectUri;

    /**
     * 客户端id-请求入参中的客户端id
     */
    private String clientId;

    /**
     * 唯一id---token唯一id
     */
    private String jti;

    /**
     * 附加信息
     */
    private Map additionalInformation;

}

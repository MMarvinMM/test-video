package com.chinatower.bdm.modules.QueryAnswer.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryAnswerLoginDTO implements Serializable {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 应用id-目前跟租户ID保持一致，后续拓展使用
     */
    private String appId;

    /**
     * 扩展参数--Json格式扩展参数。例如：{"user_id":"1008799"},AI大模型必须传入用户id
     */
    private String extParams;

    /**
     * 授权类型-授权类型，目前只支持client_credentials
     */
    private String grantType;

    /**
     * 组件编码-请求组件权限，AI大模型固定传值comp-ai
     */
    private String compCode;

    /**
     * 认证参数--token
     */
    private String authorization;

    /**
     * 接入端-WEB/APP，默认为WEB
     */
    private String accessType;
}

package com.chinatower.bdm.modules.QueryAnswer.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryAnswerDTO implements Serializable {

    /**
     * 用户请求的问题
     */
    private String msg;

    /**
     * 租户ID--暂时没用（写死）
     */
    private String tenantId;

    /**
     * 租户下应用code--暂时没用（写死）
     */
    private String appCode;

    /**
     * 查询类型--1-大语言模型问答接口（开放域问答）(闲聊) 2-请求问题答案接口（知识库领域内问答）（专业）
     */
    private String queryType;


    /**
     * 模型类型：目前知识qwen（默认）、baidu
     */
    private String modelType;
    private Boolean stream;

}

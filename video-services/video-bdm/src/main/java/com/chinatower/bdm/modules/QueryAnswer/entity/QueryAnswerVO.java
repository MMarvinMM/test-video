package com.chinatower.bdm.modules.QueryAnswer.entity;

import lombok.Data;

import java.util.Map;

@Data
public class  QueryAnswerVO {

    /**
     * 问题
     */
    private String query;

    /**
     * 答案
     */
    private String answer;

    /**
     * 问题接收时间
     */
    private String queryReceiveTime;

    /**
     * 答案生成时间
     */
    private String answerReturnTime;

    /**
     * 如果answer_route为FAQ，则给出答案所属类別id
     */
    private String dirId;

    /**
     * 答案生成方式分为：
     * LLM：大模型生成的答案
     * FAQ：知识库匹配的答案
     * None：问题暂时无法回答
     */
    private String answerRoute;

    /**
     * 如果answer_route为FAQ，则给出答案的详细信息
     */
    private Map<String,Object> answerMeta;
}

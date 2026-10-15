package com.chinatower.bdm.modules.QueryAnswer.service;


import com.chinatower.bdm.modules.QueryAnswer.entity.ChatVO;
import com.chinatower.bdm.modules.QueryAnswer.entity.QueryAnswerDTO;
import com.chinatower.common.entity.Result;
import reactor.core.publisher.Flux;

public interface IQueryAnswerService {

    Flux<String> streamData(QueryAnswerDTO queryAnswerDTO);

    Result<Object> queryProblemAnswer(QueryAnswerDTO queryAnswerDTO);

    /**
     * 多模态接口
     * @param chatVO
     * @return
     */
    Result<ChatVO> chat(ChatVO chatVO);
}

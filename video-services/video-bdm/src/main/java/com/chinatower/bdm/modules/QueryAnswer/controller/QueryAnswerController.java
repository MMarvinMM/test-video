package com.chinatower.bdm.modules.QueryAnswer.controller;


import com.chinatower.bdm.modules.QueryAnswer.entity.ChatVO;
import com.chinatower.bdm.modules.QueryAnswer.entity.QueryAnswerDTO;
import com.chinatower.bdm.modules.QueryAnswer.service.IQueryAnswerService;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * video-bdm
 * @ClassName QueryAnswerController
 * @Deacription 数字人问答模块
 * @Author chenmf
 * @Date 2024/06/27
 * @Version 1.0
 **/
@Api(tags = "数字人问答模块")
@RestController
@Slf4j
public class QueryAnswerController {

    @Autowired
    private IQueryAnswerService queryAnswerService;


    /**
     * 数字问答-查询问题答案
     * @param queryAnswerDTO
     * @return
     */
    @PostMapping("/numberModel/queryProblemAnswer")
    public Result<Object> queryProblemAnswer(@RequestBody QueryAnswerDTO queryAnswerDTO){
        return queryAnswerService.queryProblemAnswer(queryAnswerDTO);
    }

    /**
     * 数字问答-流式请求人工机器人
     * @param queryAnswerDTO
     * @return
     */
    @PostMapping(value = "/numberModel/queryProblemAnswerStream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> queryProblemAnswerStream(@RequestBody QueryAnswerDTO queryAnswerDTO){
        return queryAnswerService.streamData(queryAnswerDTO);
    }

    /**
     * 多模态接口
     * @param chatVO
     * @return
     */
    @PostMapping("/chat/completions")
    public Result<ChatVO> chat(@RequestBody @Validated ChatVO chatVO) {
        return queryAnswerService.chat(chatVO);
    }
}

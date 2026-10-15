package com.chinatower.message.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.message.api.model.OperationJudgeMsgReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "OperationJudgeMsgAPI",
        value = ServiceNameConstants.MESSAGE_SOCKET_SERVICE)
public interface OperationJudgeMsgAPI {

    @PostMapping("/msg/operation-judge/send-notice")
    Result<String> sendNotice(@RequestBody OperationJudgeMsg operationJudgeMsgReq);
}

package com.chinatower.message.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.LowAltPointMsg;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.message.api.model.OperationJudgeMsgReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "LowAltMsgAPI",
        value = ServiceNameConstants.MESSAGE_SOCKET_SERVICE)
public interface LowAltMsgAPI {

    /**
     * 低空态势服务增量轨迹点位消息通知
     */
    @PostMapping("/lowalt/flyTrace/lowAltPointNotice")
    Result<String> lowAltPointNotice(@RequestBody LowAltPointMsg lowAltPointMsg) ;

}

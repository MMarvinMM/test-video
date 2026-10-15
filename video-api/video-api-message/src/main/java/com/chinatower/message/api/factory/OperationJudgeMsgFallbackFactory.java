package com.chinatower.message.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.OperationJudgeMsgAPI;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.message.api.model.OperationJudgeMsgReq;
import com.chinatower.message.api.model.SendPresetArrivalNoticeVO;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息服务降级处理
 *
 * @author chinatower
 */
@Component
public class OperationJudgeMsgFallbackFactory implements FallbackFactory<OperationJudgeMsgAPI> {
    private static final Logger log = LoggerFactory.getLogger(OperationJudgeMsgFallbackFactory.class);


    @Override
    public OperationJudgeMsgAPI create(Throwable throwable) {
        log.error("消息服务调用失败:{}", throwable.getMessage());
        return new OperationJudgeMsgAPI() {

            @Override
            public Result<String> sendNotice(OperationJudgeMsg operationJudgeMsgReq) {
                return Result.fail(ResultCode.FAILURE,throwable.getMessage());
            }
        };
    }
}

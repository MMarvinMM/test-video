package com.chinatower.message.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.model.MsgBody;
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
public class MsgStationFallbackFactory implements FallbackFactory<MsgStationAPI>
{
    private static final Logger log = LoggerFactory.getLogger(MsgStationFallbackFactory.class);

    @Override
    public MsgStationAPI create(Throwable throwable)
    {
        log.error("消息服务调用失败:{}", throwable.getMessage());
        return new MsgStationAPI() {


            @Override
            public Result sendMsg2TargetService(MsgBody msg) {
                return Result.fail(ResultCode.FAILURE,throwable.getMessage());
            }

            @Override
            public Result sendMsg2TargetServiceBatch(List<MsgBody> msg) {
                return Result.fail(ResultCode.FAILURE,throwable.getMessage());
            }

            @Override
            public Result sendPresetArrivalNoticeBatch(List<SendPresetArrivalNoticeVO> msg){
                return Result.fail(ResultCode.FAILURE,throwable.getMessage());
            }
        };
    }
}

package com.chinatower.outdata.api.factory;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.dto.bdm.LayerAlarmCreateDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.SendKafkaAPI;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendKafkaAPIFactory implements FallbackFactory<SendKafkaAPI> {
    private static final Logger log = LoggerFactory.getLogger(SendKafkaAPIFactory.class);

    @Override
    public SendKafkaAPI create(Throwable throwable) {
        log.error("调用推送接口失败:{}", throwable.getMessage());
        return new SendKafkaAPI() {
            @Override
            public Result layerAlarm(List<LayerAlarmCreateDTO> dtos) {
                return Result.fail("发送卫片告警数据错:" + throwable.getMessage());
            }

            @Override
            public Result satelliteAlarm(JSONArray receiveSatelliteAlarmVos) {
                return Result.fail("卫星告警发送kafka错误:" + throwable.getMessage());
            }
        };
    }
}

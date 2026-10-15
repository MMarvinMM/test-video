package com.chinatower.outdata.api;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.bdm.LayerAlarmCreateDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.factory.SendKafkaAPIFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "SendKafkaAPI",
        value = ServiceNameConstants.OUTDATA_SERVICE,
        fallbackFactory = SendKafkaAPIFactory.class)
public interface SendKafkaAPI {

    /**
     * 发送卫片告警数据
     *
     * @param dtos 告警数据
     * @return Result
     * @apiNote 发送卫片告警数据
     * @apiVersion 3.35
     * @tag 三、设备能力
     */
    @PostMapping("/send/kafka/layerAlarm")
    Result layerAlarm(@RequestBody List<LayerAlarmCreateDTO> dtos);

    /**
     * 卫星告警发送kafka
     *
     * @param receiveSatelliteAlarmVos
     * @return
     */
    @PostMapping("/send/kafka/satelliteAlarm")
    Result satelliteAlarm(@RequestBody JSONArray receiveSatelliteAlarmVos);
}

package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.ReceiveAlarmFallbackFactory;
import com.chinatower.event.api.factory.ReceiveApiFallbackFactory;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "receiveApiService", value = ServiceNameConstants.EVENT_API_SERVICE, fallbackFactory = ReceiveApiFallbackFactory.class)
public interface IReceiveApiService {

    /**
     * 告警同步属地运营研判接口
     */
    @PostMapping("/alarm/receive/syncAlarm")
    @Headers("Content-Type: application/json")
    Result syncAlarm(@RequestBody ReceiveAlarmVO req);

    /**
     * 功能描述：无人机二次识别下发
     * @param: data
     * @return: Result<Map>
     */
    @PostMapping("/alarm/receive/algorithms/uavSynAnalysisTask")
    Result uavSynAnalysisTask(@RequestBody List<SecondRecogSendVO> list);
}

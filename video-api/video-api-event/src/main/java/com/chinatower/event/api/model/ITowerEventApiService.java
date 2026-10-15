package com.chinatower.event.api.model;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.TowerEventApiFallbackFactory;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "towerEventApiService", value = ServiceNameConstants.TTEVENT_SERVICE, fallbackFactory = TowerEventApiFallbackFactory.class)
public interface ITowerEventApiService {

    @PostMapping("/alarm/order/syncCheck")
    @Headers("Content-Type: application/json")
    Result syncCheck(@RequestBody SyncCheckReq req);


}

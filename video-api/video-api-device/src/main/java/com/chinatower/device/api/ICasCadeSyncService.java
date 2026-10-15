package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.CasCadeSyncFallbackFactory;
import com.chinatower.platform.entity.device.CasCadeSyncVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "casCadeSyncService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = CasCadeSyncFallbackFactory.class)
public interface ICasCadeSyncService {

    @PostMapping(value = "/device/synDelCascadeByOrder")
    Result<String> synDelCascadeByOrder(@RequestBody CasCadeSyncVO req);
}

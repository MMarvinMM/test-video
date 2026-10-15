package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AirMediumServiceFallbackFactory;
import com.chinatower.device.entity.dto.AirTaskStatusProcessDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author cuibaoluo
 * @date 2025/12/17
 * @description：
 */
@FeignClient(contextId = "airMediumService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirMediumServiceFallbackFactory.class)
public interface IAirMediumService {

    /**
     *  查询喇叭配置信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/airMedium/taskStatusProcess")
    Result taskStatusProcess(@RequestBody AirTaskStatusProcessDto dto);
}

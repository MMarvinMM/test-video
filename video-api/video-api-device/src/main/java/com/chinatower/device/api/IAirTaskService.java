package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AirTaskFallbackFactory;
import com.chinatower.device.entity.dto.AirTaskReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 任务管理
 */
@FeignClient(contextId = "airTaskService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirTaskFallbackFactory.class)
public interface IAirTaskService {

    /**
     * 更新任务实际里程(ws使用)
     *
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airTask/updateTaskAccumulateMileage")
    Result updateTaskAccumulateMileage(@RequestBody AirTaskReq airTaskReq);

}

package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.MemoryVO;
import com.chinatower.video.api.factory.MemoryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "memoryService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = MemoryFallbackFactory.class)
public interface IMemoryService {

    /**
     * 功能描述: 更新用户记忆点缓存信息通用接口
     *
     * @param  memoryVO
     * @return  Result
     * @author lyy
     * @date 2025/07/15 14:48
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/video/uptUserMemoryInfo")
    Result uptUserMemoryInfo(@RequestBody MemoryVO memoryVO);

    /**
     * 功能描述: 查看用户记忆点缓存信息通用接口
     *
     * @param memoryVO
     * @return Result
     * @author lyy
     * @date 2025/07/15 14:48
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/video/getUserMemoryInfo")
    Result getUserMemoryInfo(@RequestBody @Validated MemoryVO memoryVO);
}

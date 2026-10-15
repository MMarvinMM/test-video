package com.chinatower.platform.feign;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.dataScope.CustomDataScopeEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "video-custom-data-scope", contextId = "customDataScopeFeignClient", fallbackFactory = CustomDataFeignClientFallbackFactory.class)
public interface CustomDataFeignClient {
    @GetMapping(path = "/custom/dataScope")
    Result<CustomDataScopeEntity> customDataScope(@RequestBody CustomDataScopeEntity customDataScopeEntity);
}

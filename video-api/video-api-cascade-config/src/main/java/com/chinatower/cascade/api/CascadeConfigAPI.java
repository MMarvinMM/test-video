package com.chinatower.cascade.api;

import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.cascade.api.factory.CascadeConfigFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "CascadeConfigAPI",
        value = ServiceNameConstants.CASCADE_CONFIG_SERVICE,
        fallbackFactory = CascadeConfigFallbackFactory.class)
public interface CascadeConfigAPI {
    @PostMapping("/platformData/getInfo")
    Result<List<PlatformDataConfigVO>> getInfo(@RequestBody List<String> list);
}

package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AlgorithmInfoFallbackFactory;
import com.chinatower.device.entity.vo.FunctionTypeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(contextId = "algorithmInfoService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AlgorithmInfoFallbackFactory.class)
public interface IAlgorithmInfoServiceMigrate {
    @PostMapping("/video/algorithm/getFunction")
    Result<Map> getFunction(String tenantId);

    @PostMapping("/video/algorithm/getFunctionDetail/{id}")
    Result<FunctionTypeVO> getFunctionDetail(@PathVariable("id") String id);

}

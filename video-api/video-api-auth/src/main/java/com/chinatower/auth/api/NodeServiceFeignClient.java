package com.chinatower.auth.api;

import com.chinatower.auth.api.entity.AuthReplaceTokenVO;
import com.chinatower.auth.api.factory.NodeServiceFeignFallback;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import javax.validation.Valid;

@FeignClient(contextId = "nodeService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = NodeServiceFeignFallback.class)
public interface NodeServiceFeignClient {

    @PostMapping("/system/replaceToken")
    Result<String> replaceToken(@Valid @RequestBody AuthReplaceTokenVO authReplaceTokenVO);


}

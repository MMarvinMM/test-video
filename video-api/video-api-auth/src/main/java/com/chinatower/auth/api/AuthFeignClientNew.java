package com.chinatower.auth.api;

import com.chinatower.auth.api.entity.SdkToApiLoginVo;
import com.chinatower.auth.api.factory.AuthFallbackFactoryNew;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "authApiNew", value = ServiceNameConstants.VIDEO_AUTH_SERVICE, fallbackFactory = AuthFallbackFactoryNew.class)
public interface AuthFeignClientNew {
    @PostMapping("/sdkToApiLogin")
    Result sdkToApiLogin(@RequestBody SdkToApiLoginVo vo);
}

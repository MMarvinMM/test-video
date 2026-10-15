package com.chinatower.auth.api;

import com.chinatower.auth.api.factory.SystemApiFallbackFactory;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "systemApi", value = "admin", fallback = SystemApiFallbackFactory.class)
public interface SystemApiFeignClient {

    @GetMapping(value = "/system/user/selectUserByUserName/{userName}")
    Result selectUserOnlyByUserName(@PathVariable("userName") String userName, @RequestHeader("authorization") String Authorization);




}

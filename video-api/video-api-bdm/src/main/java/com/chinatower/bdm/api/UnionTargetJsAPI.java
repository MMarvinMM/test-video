package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.UnionTargetJsParam;
import com.chinatower.bdm.api.factory.UnionTargetJsFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "UnionTargetJsAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = UnionTargetJsFallbackFactory.class)
public interface UnionTargetJsAPI {

    /**
      * 应用平台通知雷达平台启动/关闭联动光电跟踪监测目标
      */
    @PostMapping("/unionTargetJs/targetFollow")
    Result targetFollow(@RequestBody UnionTargetJsParam unionTargetJsParam);


}

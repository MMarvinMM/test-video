package com.chinatower.gis.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.factory.GisFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(contextId = "sysClient",
        value = ServiceNameConstants.SYSTEM_SERVICE,
        fallbackFactory = GisFallbackFactory.class)
public interface SysClient {

    /**
     * 获取地区信息
     * @param areaCode
     * @return
     */
    @GetMapping(value = "/system/area/getAreaInfo/{areaCode}")
    Result getAreaInfo(@PathVariable("areaCode") String areaCode);
}

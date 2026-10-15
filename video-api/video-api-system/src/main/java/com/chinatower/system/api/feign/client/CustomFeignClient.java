package com.chinatower.system.api.feign.client;



import com.alibaba.fastjson.JSONObject;
import com.chinatower.system.api.feign.factory.CustomFallbackFactory;
import com.chinatower.system.api.model.AlarmTypeReqVo;
import com.chinatower.system.api.model.UpdateTenantReqVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigInfoSubVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigQueryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chinatower.common.entity.Result;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "customFeignClient",value = "admin",fallbackFactory = CustomFallbackFactory.class)
public interface CustomFeignClient {


    @PostMapping("/custom/manage/updateTenantInfoSync")
    Result updateTenantInfoNewSync(@RequestBody AlarmTypeReqVo alarmTypeReqVo);

    @PostMapping("/custom/manage/updateTenantInfoBatchSync")
    Result updateTenantInfoBatchSync(@RequestBody UpdateTenantReqVO updateTenantReqVO);

    @PostMapping("/system/advice/adviceSyncOrderSystem")
    Result adviceSyncOrderSystem(@RequestBody JSONObject sysAdvice);

    /**
     * 客户视角-给地图查询使用
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/custom/manage/queryTenantMapConfigInfo4Tenant")
    Result<Map<String, List<TenantMapConfigInfoSubVO>>> queryTenantMapConfigInfo4Tenant(@RequestBody TenantMapConfigQueryVO param);
}

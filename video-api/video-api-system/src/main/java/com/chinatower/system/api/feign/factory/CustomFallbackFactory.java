package com.chinatower.system.api.feign.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.domain.SysAdvice;
import com.chinatower.system.api.feign.client.CustomFeignClient;
import com.chinatower.system.api.model.AlarmTypeReqVo;
import com.chinatower.system.api.model.SysOrderAdviceVO;
import com.chinatower.system.api.model.UpdateTenantReqVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigInfoSubVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigQueryVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import com.chinatower.common.entity.Result;

import java.util.List;
import java.util.Map;

@Component
public class CustomFallbackFactory implements FallbackFactory<CustomFeignClient> {
    @Override
    public CustomFeignClient create(Throwable cause) {
        return new CustomFeignClient() {
            @Override
            public Result<String> updateTenantInfoNewSync(AlarmTypeReqVo alarmTypeReqVo) {
                return Result.fail("运营研判操作推送失败：" + cause.getMessage());
            }

            @Override
            public Result<Integer> updateTenantInfoBatchSync(UpdateTenantReqVO updateTenantReqVO) {
                return Result.fail("运营研判批量操作推送失败：" + cause.getMessage());
            }

            @Override
            public Result<String> adviceSyncOrderSystem(JSONObject sysOrderAdviceVO) {
                return Result.fail("意见反馈同步工单失败：" + cause.getMessage());
            }

            @Override
            public Result<Map<String, List<TenantMapConfigInfoSubVO>>> queryTenantMapConfigInfo4Tenant(TenantMapConfigQueryVO param) {
                return Result.fail("地图查询失败：" + cause.getMessage());
            }

        };
    }
}

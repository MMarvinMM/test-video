package com.chinatower.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.service.CommonRepository;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.user.GetUserByDeptAndDeviceScopeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 16:30
 */

@Slf4j
@Service
public class ICommonRepositoryImpl implements CommonRepository {

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public Result<List<SysUser>> getUserByDeptAndDeviceScope(List<String> userIdList, String tenantId, String deviceCode, String industryCode, String appVerCode, String alarmSource, String cityId, String countyId, String provinceId, String alarmTypeId) {
        //调用接口获取有摄像机权限的客户
        GetUserByDeptAndDeviceScopeRequest request = new GetUserByDeptAndDeviceScopeRequest();
        request.setUserIdList(userIdList);
        request.setDeviceCode(deviceCode);
        request.setIndustryCode(industryCode);
        request.setAppVerCode(appVerCode);
        request.setTenantId(tenantId);
        request.setAlarmSource(alarmSource);
        request.setCityId(cityId);
        request.setProvinceId(provinceId);
        request.setCountyId(countyId);
        request.setTypeValue(alarmTypeId);
        log.info("CommonRepositoryImpl-getUserByDeptAndDeviceScope-根据设备编号获取有权限的用户列表-req:{}", JSON.toJSONString(request));
        Result<List<SysUser>> reponse = systemFeignClient.getUserByDeptAndDeviceScope(request);
        log.info("CommonRepositoryImpl-getUserByDeptAndDeviceScope-根据设备编号获取有权限的用户列表-resp:{}", JSON.toJSONString(reponse));
        return reponse;
    }
}

package com.chinatower.system.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.system.api.feign.factory.SysLoginFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "sysLoginService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SysLoginFallbackFactory.class)
public interface SysLoginService {

    /**
     * 获取用户信息(新)
     *
     * @return 用户信息
     */
    @GetMapping("/base/getInfoNew")
    AjaxResult getInfoNew();

    /**
     * 获取權限信息
     *
     * @return 权限信息
     */
    @GetMapping("/base/getPermissions")
    AjaxResult getPermissions();

    /**
     * 根据组织编号获取详细信息
     *
     * @param deptId
     * @return
     */
    @GetMapping(value = "/system/tenantDept/detail/{deptId}")
    AjaxResult getInfo(@PathVariable String deptId);
}

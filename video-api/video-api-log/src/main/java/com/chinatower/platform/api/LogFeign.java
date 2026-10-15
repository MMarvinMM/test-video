package com.chinatower.platform.api;

import com.chinatower.common.constant.ProviderUrlConstant;
import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.api.dto.InterfaceOpeLog;
import com.chinatower.platform.api.dto.SysLogininfoDTO;
import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.api.entity.SysLogininfoEntity;
import com.chinatower.platform.api.entity.SysOperLogEntity;
import com.chinatower.platform.api.factory.LoginLogFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "tower-log", fallbackFactory = LoginLogFeignFallbackFactory.class)
public interface LogFeign {

    /**
     * 记录登录日志
     * @param logininfo
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER +"/logininfo/record")
    Result<Boolean> recordLoginInfo(@RequestBody SysLogininfoEntity logininfo);

    /**
     * 登录次数查询
     * @param logininfor
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER +"/logininfo/count")
    public Result<Long> getLoginNum(@RequestBody SysLogininfoDTO logininfor);

    /**
     * 记录操作日志
     * @param operLogEntity
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER +"/log/set")
    Result<Boolean> recordSysLog(@RequestBody SysOperLogEntity operLogEntity);

    /**
     * 记录操作日志
     * @param opeLog
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER +"/sysLog/add")
    Result<Boolean> addLog(@RequestBody InterfaceOpeLog opeLog);

    /**
     * 记录所有请求
     * @param buryPointDTO
     * @return
     */
    @PostMapping("/burypoint/saveOne")
    Result<Boolean> saveBuryPoint(@RequestBody BuryPointDTO buryPointDTO);

    /**
     * 登录日志列表查询
     * @param logininfor
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER +"/logininfo/list")
    public TableDataInfo list(@RequestBody SysLogininfoDTO logininfor);
    /**
     * 登录日志导出列表查询
     * @param logininfor
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER +"/logininfo/exportList")
    public Result<List<SysLogininfoEntity>> exportList(@RequestBody SysLogininfoDTO logininfor);
    /**
     * 操作日志列表查询
     * @param operLog
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER + "/sysLog/list")
    public TableDataInfo list(SysOperLogDTO operLog);
    /**
     * 操作日志列表导出
     * @param operLog
     * @return
     */
    @PostMapping(ProviderUrlConstant.PROVIDER + "/sysLog/exportList")
    public Result<List<SysOperLogEntity>> exportList(@RequestBody SysOperLogDTO operLog);
}

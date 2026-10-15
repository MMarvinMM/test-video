package com.chinatower.platform.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.platform.api.dto.InterfaceOpeLog;
import com.chinatower.platform.api.dto.SysLogininfoDTO;
import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.api.entity.SysLogininfoEntity;
import com.chinatower.platform.api.entity.SysOperLogEntity;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginLogFeignFallbackFactory implements FallbackFactory<LogFeign> {
    @Override
    public LogFeign create(Throwable throwable) {
        return new LogFeign() {
            @Override
            public Result<Boolean> recordLoginInfo(SysLogininfoEntity logininfo) {
                return Result.fail("记录登录日志失败:" + throwable.getMessage());
            }

            @Override
            public Result<Long> getLoginNum(SysLogininfoDTO logininfor) {
                return Result.data(Long.valueOf(0));
            }

            @Override
            public Result<Boolean> recordSysLog(SysOperLogEntity operLogEntity) {
                return Result.fail("记录操作日志失败:" + throwable.getMessage());
            }

            @Override
            public Result<Boolean> addLog(InterfaceOpeLog opeLog) {
                return Result.fail("记录操作日志失败:" + throwable.getMessage());
            }

            @Override
            public Result<Boolean> saveBuryPoint(BuryPointDTO buryPointDTO) {
                return Result.fail("记录所有接口请求失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo list(SysLogininfoDTO logininfor) {
                TableDataInfo info = new TableDataInfo();
                info.setCode(200);
                info.setTotal(0);
                return  info;
            }

            @Override
            public Result<List<SysLogininfoEntity>> exportList(SysLogininfoDTO logininfor) {
                return  Result.fail("导出日志失败");
            }

            @Override
            public TableDataInfo list(SysOperLogDTO operLog) {
                TableDataInfo info = new TableDataInfo();
                info.setCode(200);
                info.setTotal(0);
                return  info;
            }

            @Override
            public Result<List<SysOperLogEntity>> exportList(SysOperLogDTO operLog) {
                return Result.fail("操作日志导出失败");
            }
        };
    }
}

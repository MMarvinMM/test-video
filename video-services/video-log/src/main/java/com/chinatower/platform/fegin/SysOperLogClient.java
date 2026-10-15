package com.chinatower.platform.fegin;

import com.chinatower.common.constant.ProviderUrlConstant;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.entity.CommonLog;
import com.chinatower.common.entity.Result;
import com.chinatower.log.fegin.ISysOperLogClient;
import com.chinatower.platform.entity.SysOperLogEntity;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.service.ISysOperLogService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * tower-log
 * 功能说明:
 *
 * @author 李峥
 * @date 2021-11-21 10:14
 * @requestID 需求单号及需求名称
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "日志远程调用")
public class SysOperLogClient implements ISysOperLogClient {

    private final ISysOperLogService operLogService;

    @Override
    @PostMapping(ProviderUrlConstant.PROVIDER_LOG_SET)
    @ApiOperation(value = "日志设置", notes = "日志设置")
    public Result<Boolean> set(CommonLog commonLog) {
        String logMathOpen = RedisUtil.get("log_math_open", RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(logMathOpen) && logMathOpen.equals(Constants.ONE)){
            return Result.data(false);
        } else {
            SysOperLogEntity sysLog = new SysOperLogEntity();
            BeanUtils.copyProperties(commonLog, sysLog);
            return Result.data(operLogService.insertOperlog(sysLog));
        }
    }
}

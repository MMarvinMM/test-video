package com.chinatower.system.api.feign.factory;

import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.system.api.feign.client.SysLoginService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务降级处理
 *
 * @author chinatower
 */
@Component
public class SysLoginFallbackFactory implements FallbackFactory<SysLoginService>
{
    private static final Logger log = LoggerFactory.getLogger(SysLoginFallbackFactory.class);

    @Override
    public SysLoginService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new SysLoginService() {
            @Override
            public AjaxResult getInfoNew() {
                return AjaxResult.error("获取用户信息失败"+throwable.getMessage());
            }

            @Override
            public AjaxResult getPermissions() {
                return AjaxResult.error("获取权限信息失败"+throwable.getMessage());
            }

            @Override
            public AjaxResult getInfo(String deptId){
                return AjaxResult.error("获取部门信息失败"+throwable.getMessage());
            }
        };
    }
}

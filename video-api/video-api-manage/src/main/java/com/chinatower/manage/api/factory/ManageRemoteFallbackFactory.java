package com.chinatower.manage.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.IManageRemoteService;
import com.chinatower.manage.api.dto.LocalizationSysIndustryConf;
import com.chinatower.manage.api.dto.LocalizationSysIndustryVerConf;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ManageRemoteFallbackFactory implements FallbackFactory<IManageRemoteService> {
    @Override
    public IManageRemoteService create(Throwable throwable) {
        log.error("行业服务同步调用失败:{}", throwable.getMessage());
        return new IManageRemoteService() {

            @Override
            public Result localizationReceptionConf(LocalizationSysIndustryConf dto) {
                return Result.fail("行业信息同步节点调用失败:" + throwable.getMessage());
            }

            @Override
            public Result localizationReceptionVerConf(LocalizationSysIndustryVerConf dto) {
                return Result.fail("版本信息同步节点调用失败:" + throwable.getMessage());
            }

        };
    }
}

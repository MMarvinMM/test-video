package com.chinatower.manage.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.ILocalLizedService;
import com.chinatower.manage.api.IManageRemoteService;
import com.chinatower.manage.api.dto.LocalizationSysIndustryConf;
import com.chinatower.manage.api.dto.LocalizationSysIndustryVerConf;
import com.chinatower.manage.api.dto.LocalizedVersion;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LocalLizedRemoteFallbackFactory implements FallbackFactory<ILocalLizedService> {
    @Override
    public ILocalLizedService create(Throwable throwable) {
        log.error("行业服务同步调用失败:{}", throwable.getMessage());
        return new ILocalLizedService() {

            @Override
            public Result<LocalizedVersion> queryLocalizedInfo(LocalizedVersion localizedVersion) {
                return Result.fail("查询属地版本信息(暴露sdk)调用失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<LocalizedVersion>> queryLocalizedVersionList(LocalizedVersion localizedVersion) {
                return Result.fail("查询属地版本号下拉列表(暴露sdk)调用失败:" + throwable.getMessage());
            }

            @Override
            public Result<LocalizedVersion> queryIndustryConfigInfo(LocalizedVersion localizedVersion) {
                return Result.fail("查询属地行业应用版本信息调用失败:" + throwable.getMessage());
            }

            @Override
            public Result<LocalizedVersion> queryIndustryMenuConfigInfo(LocalizedVersion localizedVersion) {
                return Result.fail("查询属地行业应用版本菜单信息调用失败:" + throwable.getMessage());
            }

        };
    }
}

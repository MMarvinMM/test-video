package com.chinatower.manage.api;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.dto.LocalizedVersion;
import com.chinatower.manage.api.factory.LocalLizedRemoteFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "localLizedRemoteService", value = "ttmanage", fallbackFactory = LocalLizedRemoteFallbackFactory.class)
public interface ILocalLizedService {

    @PostMapping("/localizedVersionManagement/queryLocalizedInfo")
    Result<LocalizedVersion> queryLocalizedInfo(@RequestBody LocalizedVersion localizedVersion);

    @PostMapping("/localizedVersionManagement/queryLocalizedVersionList")
    Result<List<LocalizedVersion>> queryLocalizedVersionList(@RequestBody LocalizedVersion localizedVersion);

    @PostMapping("/localized/platform/queryIndustryConfigInfo")
    Result<LocalizedVersion> queryIndustryConfigInfo(@RequestBody LocalizedVersion localizedVersion);

    @PostMapping("/localized/platform/queryIndustryMenuConfigInfo")
    Result<LocalizedVersion> queryIndustryMenuConfigInfo(@RequestBody LocalizedVersion localizedVersion);
}

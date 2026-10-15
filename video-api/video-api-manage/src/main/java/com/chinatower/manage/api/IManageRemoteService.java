package com.chinatower.manage.api;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.dto.LocalizationSysIndustryConf;
import com.chinatower.manage.api.dto.LocalizationSysIndustryVerConf;
import com.chinatower.manage.api.factory.ManageRemoteFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "manageRemoteService", value = "ttmanage", fallbackFactory = ManageRemoteFallbackFactory.class)
public interface IManageRemoteService {

    @PostMapping("/industry/conf/localizationReceptionConf")
    Result localizationReceptionConf(@Validated @RequestBody LocalizationSysIndustryConf dto);

    @PostMapping("/industry/conf/localizationReceptionVerConf")
    Result localizationReceptionVerConf(@Validated @RequestBody LocalizationSysIndustryVerConf dto);
}

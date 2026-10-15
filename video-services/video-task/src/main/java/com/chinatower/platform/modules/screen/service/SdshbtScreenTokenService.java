package com.chinatower.platform.modules.screen.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.modules.screen.client.SdshbtSecurityClient;
import com.chinatower.platform.modules.screen.client.SdshbtTokenResult;
import com.chinatower.platform.modules.screen.dao.ThirdPartyScreenConfMapper;
import com.chinatower.platform.modules.screen.entity.ThirdPartyScreenConf;
import com.chinatower.platform.modules.screen.util.ScreenUrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SdshbtScreenTokenService {

    @Resource
    private SdshbtSecurityClient sdshbtSecurityClient;

    @Resource
    private ThirdPartyScreenConfMapper thirdPartyScreenConfMapper;

    @DS("base")
    public ScreenTokenRefreshResult refreshAll() {
        SdshbtTokenResult tokenResult = sdshbtSecurityClient.login();
        if (tokenResult == null) {
            return ScreenTokenRefreshResult.loginFailed();
        }

        List<ThirdPartyScreenConf> configs = thirdPartyScreenConfMapper.selectSdshbtScreenConfigs();
        if (configs == null || configs.isEmpty()) {
            log.info("sdshbt 大屏 token 刷新：无匹配配置");
            return ScreenTokenRefreshResult.empty();
        }

        ScreenTokenRefreshResult result = new ScreenTokenRefreshResult();
        for (ThirdPartyScreenConf conf : configs) {
            try {
                refreshSingle(conf, tokenResult, result);
            } catch (Exception e) {
                log.error("sdshbt 大屏 token 刷新失败, id={}", conf.getId(), e);
                result.setFailCount(result.getFailCount() + 1);
            }
        }
        result.setMessage(String.format("刷新完成：成功 %d，失败 %d，跳过 %d",
                result.getSuccessCount(), result.getFailCount(), result.getSkipCount()));
        log.info("sdshbt 大屏 token 刷新 {}", result.getMessage());
        return result;
    }

    private void refreshSingle(ThirdPartyScreenConf conf, SdshbtTokenResult tokenResult,
                               ScreenTokenRefreshResult result) {
        if (conf.getId() == null || StrUtil.isBlank(conf.getScreenUrl())) {
            log.warn("sdshbt 大屏配置无效, id={}", conf.getId());
            result.setSkipCount(result.getSkipCount() + 1);
            return;
        }

        String newUrl = ScreenUrlUtils.replaceTokenParams(
                conf.getScreenUrl(), tokenResult.getToken(), tokenResult.getRefreshToken());
        if (newUrl == null) {
            log.warn("sdshbt 大屏 URL 格式异常, id={}, url={}", conf.getId(), conf.getScreenUrl());
            result.setSkipCount(result.getSkipCount() + 1);
            return;
        }

        if (newUrl.equals(conf.getScreenUrl())) {
            log.info("sdshbt 大屏 URL 未变化, id={}", conf.getId());
            result.setSuccessCount(result.getSuccessCount() + 1);
            return;
        }

        int updated = thirdPartyScreenConfMapper.updateScreenUrl(conf.getId(), newUrl);
        if (updated > 0) {
            result.setSuccessCount(result.getSuccessCount() + 1);
            log.info("sdshbt 大屏 token 已更新, id={}", conf.getId());
        } else {
            result.setFailCount(result.getFailCount() + 1);
            log.error("sdshbt 大屏 token 更新 DB 失败, id={}", conf.getId());
        }
    }
}

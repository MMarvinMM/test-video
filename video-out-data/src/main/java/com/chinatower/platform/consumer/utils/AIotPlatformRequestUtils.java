package com.chinatower.platform.consumer.utils;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 调用AIot能力平台
 */
@Slf4j
@Component
public class AIotPlatformRequestUtils {

    @Resource
    private SystemFeignClient systemFeignClient;

    private static final String CONFIG_PLAT_CODE = "platform_code";

    private static final String CALL_NEW_VERSION = "3.4";

    private static final String AIOT_PLATFORM_CODE = "aiot";

    private String getCurrentLocationCode() {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(CONFIG_PLAT_CODE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (Objects.isNull(sysConfig)) {
                throw new CustomException("当前平台编码未配置，请配置后重试");
            }
            return sysConfig.getConfigValue();
        }
        else {
            throw new CustomException("获取平台编码配置失败，请稍后重试！");
        }
    }

    /**
     * 判断是否调用AIoT接口
     *
     * @return 如果调用AIoT接口，则返回true；否则返回false
     */
    public Boolean isCallAIot(String platformCode) {
        try {
            String locationCode = StringUtils.isEmpty(platformCode) ? getCurrentLocationCode() : platformCode;
            log.info("当前平台编码:{}", locationCode);
            String str = RedisUtil.get(Constants.COLONY_RELATION_ALL + locationCode, RedisDBType.PRIMARY);
            if (StringUtils.isEmpty(str)) {
                return false;
            }
            ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
            for (ColonyApp app : cascadeConfigSyncVO.getApps()) {
                if (AIOT_PLATFORM_CODE.equals(app.getAppId())) {
                    String platformNodeVersion = app.getPlatformNodeVersion();
                    if (StringUtil.isNotEmpty(platformNodeVersion)) {
                        return compareVersion(platformNodeVersion);
                    }
                }
            }
        } catch (Exception e) {
            log.error("AIotPlatformRequestUtils isCallAIot error:", e);
        }
        return false;
    }

    private static Boolean compareVersion(String v1) {
        if (StringUtils.isEmpty(v1)) {
            return false;
        }
        String[] parts1 = v1.split("\\.");
        log.info("当前版本号:{}", v1);
        String[] parts2 = CALL_NEW_VERSION.split("\\.");

        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            int result = Integer.compare(num1, num2);
            if (result != 0) {
                return result >= 0;
            }
        }
        return true;
    }
}

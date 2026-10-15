package com.chinatower.gis.factory;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.gis.api.entity.Constants;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.enums.GisMapServiceEnums;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 地图切换
 * @data 创建时间：2023/8/15 13:37
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GisMapFactory {
    private final SystemFeignClient systemFeignClient;
    private final Map<String, GisMapService> maps;

    /**
     * 以配置为准
     * @param name
     * @return
     */
    public GisMapService get(String name) {
        try {
            String globalMapType = null;
            SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + Constants.GLOBAL_MAP_TYPE, SysConfig.class, RedisDBType.PRIMARY);

            if (sysConfig != null) {
                globalMapType = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
            }

            if ("0".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.GD_WORLD.getServiceName());
            }else if ("1".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.OFFLINE_WORLD.getServiceName());
            }else if ("4".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.OFFLINE_ENCRYPT_WORLD.getServiceName());
            }
        } catch (Exception e) {
            log.error(Constants.REQUEST_GLOBAL_MAP_TYPE_ERROR, e);
        }
        return maps.get(name);
    }

    /**
     * 独立配置
     * @param name
     * @return
     */
    public GisMapService independentGet(String name) {
        try {
            String globalMapType = null;
            SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + Constants.gis_geocoding_config, SysConfig.class, RedisDBType.PRIMARY);

            if (sysConfig != null) {
                try {
                    globalMapType = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "false,0");
                    String[] split = globalMapType.split(",");
                    if ("true".equalsIgnoreCase(split[0])){
                        globalMapType = split[1];
                    }else {
                        return get(name);
                    }
                }catch (Exception e){
                    log.error("获取独立配置错误", e);
                    return get(name);
                }
            }else {
                return get(name);
            }


            if ("-1".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.MAP_WORLD.getServiceName());
            }else if ("0".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.GD_WORLD.getServiceName());
            }else if ("1".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.OFFLINE_WORLD.getServiceName());
            }else if ("4".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.OFFLINE_ENCRYPT_WORLD.getServiceName());
            }
        } catch (Exception e) {
            log.error(Constants.REQUEST_GLOBAL_MAP_TYPE_ERROR, e);
        }
        return maps.get(name);
    }


    /**
     * 以输入为准
     * @param enums
     * @param defaultEnums
     * @return
     */
    public GisMapService get(GisMapServiceEnums enums,GisMapServiceEnums defaultEnums) {
        try {
            if(enums != null){
                return maps.get(enums.getServiceName());
            }
            String globalMapType = null;
            AjaxResult ajaxResult = systemFeignClient.getConfValue(Constants.GLOBAL_MAP_TYPE);
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                globalMapType = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
            }

            if ("0".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.GD_WORLD.getServiceName());
            }else if ("1".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.OFFLINE_WORLD.getServiceName());
            }else if ("4".equals(globalMapType)) {
                return maps.get(GisMapServiceEnums.OFFLINE_ENCRYPT_WORLD.getServiceName());
            }
        } catch (Exception e) {
            log.error(Constants.REQUEST_GLOBAL_MAP_TYPE_ERROR, e);
        }
        return maps.get(defaultEnums.getServiceName());
    }
}

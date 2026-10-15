package com.chinatower.gis.factory;

import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.modules.gis.entity.GisMapTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GisMapServiceFactory {
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * 使用策略工厂获取具体策略实现
     * @param code
     * @return
     */
    @Deprecated
    public GisMapServiceAPI getGisMapService(String code) {
        try {
            //判断是否设置了离线模式，如果是的话，则将策略变更为04
            return (GisMapServiceAPI) applicationContext.getBean(Class.forName(GisMapTypeEnum.getClassNameByCode(GisMapTypeEnum.GAODE_MAP.getCode())).newInstance().getClass());
        } catch (Exception e) {
            log.error("旧版POI错误", e);
        }
        return null;
    }
}

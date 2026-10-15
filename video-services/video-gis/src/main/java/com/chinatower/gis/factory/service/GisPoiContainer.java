package com.chinatower.gis.factory.service;

import com.chinatower.gis.modules.gis.mapper.GisMapper;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class GisPoiContainer {
    @Resource
    protected GisMapper gisMapper;
    private final Map<String, SysArea> SYS_AREA_MAP = new HashMap<>();
    //省会城市
    private final Map<String, SysCapitalVO> SYS_CAPITAL_MAP = new HashMap<>();


    @PostConstruct
    public void initSysArea() {
        log.info("初始化区县省初数据");
        CompletableFuture.runAsync(() -> {
            List<SysArea> sysAreas = gisMapper.selectByDistrictAndCounty();
            for (SysArea sysArea : sysAreas) {
                SYS_AREA_MAP.put(sysArea.getAreaCode(), sysArea);
            }
        });
        log.info("初始化省会数据");
        CompletableFuture.runAsync(() -> {
            List<SysCapitalVO> capitalVOS = gisMapper.selectByProvList();
            for (SysCapitalVO sysArea : capitalVOS) {
                SYS_CAPITAL_MAP.put(sysArea.getProvCode(), sysArea);
            }
        });
    }

    /**
     * 查询行政区划数据
     */
    public SysArea getSysArea(String areaCode) {
        SysArea sysArea = SYS_AREA_MAP.get(areaCode);
        if (sysArea == null) {
            sysArea = gisMapper.selectByAreaCode(areaCode);
            if (sysArea != null) {
                SYS_AREA_MAP.put(areaCode, sysArea);
            }
        }
        return sysArea;
    }

    /**
     * 查询行政区划数据
     */
    public SysCapitalVO getSysCapital(String areaCode) {
        SysCapitalVO sysArea = SYS_CAPITAL_MAP.get(areaCode);
        if (sysArea == null) {
            sysArea = gisMapper.selectByProvCode(areaCode);
            if (sysArea != null) {
                SYS_CAPITAL_MAP.put(areaCode, sysArea);
            }
        }
        return sysArea;
    }
}

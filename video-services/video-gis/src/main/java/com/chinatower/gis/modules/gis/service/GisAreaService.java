package com.chinatower.gis.modules.gis.service;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.entity.LayerInfo;
import com.chinatower.gis.modules.gis.mapper.GisAreaMapper;
import com.chinatower.gis.modules.gis.mapper.LayerInfoMapper;
import com.chinatower.gis.modules.gis.mapper.PgLayerCommonMapper;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.enums.PGLayerTempEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @ClassName GisLayer
 * @Description GisLayer服务类
 * @Author D
 * @Date 2022/3/14 15:53
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GisAreaService {

    private final GisAreaMapper gisAreaMapper;

    private final LayerInfoMapper layerInfoMapper;
    private final PgLayerCommonMapper pgLayerCommonMapper;

    /**
     * 图斑面积统计 Features
     *
     * @param statisticDkmjReq
     * @return
     */
    public String statisticDkmj(StatisticDkmjReq statisticDkmjReq) {
        LayerInfo layerInfo = layerInfoMapper.selectById(statisticDkmjReq.getLayerID());
        if (layerInfo == null) {
            throw new CustomException("参数错误");
        }
        String areaField = pgLayerCommonMapper.getLayerAreaField(layerInfo.getLayerTypeSimple());
        if (StringUtils.isEmpty(areaField)){
            throw new CustomException("面积字段不存在");
        }
        return gisAreaMapper.statisticDkmj(layerInfo.getLayerName(), statisticDkmjReq.getFeatureIDs(), areaField);
    }

    /**
     * 耕地现状统计 Features
     *
     * @return
     */
    public String statisticGdxzmj(StatisticGdxzmjReq statisticGdxzmjReq) {
        List<String> layerNames = layerInfoMapper.selectByTenant(statisticGdxzmjReq.getTenantId(), statisticGdxzmjReq.getIndustryCode(), PGLayerTempEnums.XZTB_CESHITABLE.getLayerType());
        if (layerNames == null || layerNames.isEmpty()){
            return "0";
        }
        String statisticDkmj = gisAreaMapper.statisticGdxzmj(layerNames, statisticGdxzmjReq.getFarmlandType());
        return statisticDkmj;
    }

}

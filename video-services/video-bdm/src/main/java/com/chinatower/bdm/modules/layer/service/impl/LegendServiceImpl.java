package com.chinatower.bdm.modules.layer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.modules.grid.config.GeoserverStyleTemplate;
import com.chinatower.bdm.modules.grid.dao.LayerStyleFiledParamsDTO;
import com.chinatower.bdm.modules.grid.dao.LayerStyleRangeDTO;
import com.chinatower.bdm.modules.layer.domain.*;
import com.chinatower.bdm.modules.layer.mapper.LayerMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeFieldsStyleMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeStyleMappingMapper;
import com.chinatower.bdm.modules.layer.service.GeoService;
import com.chinatower.bdm.modules.layer.service.LegendService;
import com.chinatower.common.core.dto.bdm.LayerTypeStyleMappingVO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.enums.PgLayerStyleEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.BigDecimalUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LegendServiceImpl implements LegendService {

    private final GeoService geoService;
    private final LayerTypeStyleMappingMapper layerTypeStyleMappingMapper;
    private final LayerMapper layerMapper;
    private final LayerTypeFieldsStyleMapper layerTypeFieldsStyleMapper;

    /**
     * 生成样式文件名称
     *
     * @param abbreviation  简称
     * @param filedCode     字段
     * @param showBindField 是否需要字段
     */
    private static String getStyleName(String abbreviation, String filedCode, boolean showBindField) {
        StringBuilder sb = new StringBuilder();
        if (showBindField) {
            sb.append(abbreviation).append("_").append(filedCode).append("_");
        } else {
            sb.append(abbreviation).append("_");
        }
        sb.append(RandomStringUtils.randomAlphanumeric(8));
        return sb.toString();
    }

    /**
     * 获取范围字符串
     *
     * @param max 最大范围
     * @param min 最小范围
     * @return 范围字符串
     */
    private static String getRangeStr(LayerStyleRangeDTO max, LayerStyleRangeDTO min) {
        StringBuilder sb = new StringBuilder();
        if (max != null && min != null
                && StringUtils.isNotBlank(max.getValue()) && StringUtils.isNotBlank(min.getValue())
                && GeoserverStyleTemplate.LT_LTE.contains(max.getSymbol()) && GeoserverStyleTemplate.LT_LTE.contains(min.getSymbol())) {
            if (GeoserverStyleTemplate.LT.equals(min.getSymbol())) {
                sb.append("(").append(min.getValue()).append(",");
            } else if (GeoserverStyleTemplate.LTE.equals(min.getSymbol())) {
                sb.append("[").append(min.getValue()).append(",");
            }

            if (GeoserverStyleTemplate.LT.equals(max.getSymbol())) {
                sb.append(max.getValue()).append(")");
            } else if (GeoserverStyleTemplate.LTE.equals(max.getSymbol())) {
                sb.append(max.getValue()).append("]");
            }
        } else if (max != null && min != null) {
            appendMaxRangeStr(sb, max);
            appendMinRangeStr(sb, min);
        }
        return sb.toString();
    }

    /**
     * 添加最大范围字符串
     *
     * @param sb  字符串构建器
     * @param max 最大范围
     */
    private static void appendMaxRangeStr(StringBuilder sb, LayerStyleRangeDTO max) {
        if (StringUtils.isNotBlank(max.getValue()) && GeoserverStyleTemplate.LT.equals(max.getSymbol())) {
            sb.append("&lt;").append(max.getValue());
        }

        if (StringUtils.isNotBlank(max.getValue()) && GeoserverStyleTemplate.LTE.equals(max.getSymbol())) {
            sb.append("≤").append(max.getValue());
        }
    }

    /**
     * 添加最小范围字符串
     *
     * @param sb  字符串构建器
     * @param min 最小范围
     */
    private static void appendMinRangeStr(StringBuilder sb, LayerStyleRangeDTO min) {
        if (StringUtils.isNotBlank(min.getValue()) && GeoserverStyleTemplate.LT.equals(min.getSymbol())) {
            sb.append("&gt;").append(min.getValue());
        }

        if (StringUtils.isNotBlank(min.getValue()) && GeoserverStyleTemplate.LTE.equals(min.getSymbol())) {
            sb.append("≥").append(min.getValue());
        }
    }

    /**
     * 范围转换
     */
    private static LayerStyleRangeDTO getMaxOrMin(RangeValueJson range) {
        LayerStyleRangeDTO rangeDTO = null;
        if (range != null) {
            String value = range.getValue() == null ? "" : range.getValue();
            rangeDTO = LayerStyleRangeDTO.builder()
                    .symbol(range.getSymbol())
                    .value(value)
                    .build();
        }
        return rangeDTO;
    }


    @Override
    public void updateLegend(LayerTypeStyle style) {
        if (CollectionUtils.isEmpty(style.getDictJson())) {
            throw new CustomException("请添加样式数据");
        }
        log.info("修改或者新增图例样式字段 {}", style.getCode());
        if (StringUtils.isNotBlank(style.getCode())){
            style.setCode(style.getCode().toLowerCase());
        }
        log.info("修改或者新增图层类型样式 {}", JSON.toJSON(style));
        boolean showBindField = style.getShowBindField() != null && style.getShowBindField() == 1;
        //命名空间
        String workSpaceName = geoService.workSpace(style.getIndustryCode());

        String styleName = getStyleName(style.getAbbreviation(), style.getCode(), showBindField);
        //点线面 样式 sdl body
        String polygon;
        String point;
        String line;

        String polygonStyleName = styleName + "_polygon";
        String pointStyleName = styleName + "_point";
        String lineStyleName = styleName + "_line";
        List<LayerTypeStyleMapping> mappings = layerTypeStyleMappingMapper.selectByStyleId(style.getId(), null);
        //根据模板创建样式
        if (showBindField) {
            List<LayerStyleFiledParamsDTO> list = new ArrayList<>();
            for (FieldStyleDictJson dictJson : style.getDictJson()) {
                LayerStyleRangeDTO min = null;
                LayerStyleRangeDTO max = null;
                RangeJson range = dictJson.getRange();
                if (range != null) {
                    min = getMaxOrMin(range.getMin());
                    max = getMaxOrMin(range.getMax());
                }
                //如果是范围标签取
                boolean rangeFlag = style.getType() != null && style.getType() == 2;
                if (rangeFlag) {
                    dictJson.setFilterAttrValue(dictJson.getRangeLabel());
                    //如果标签是空的则取数值区间
                    if (StringUtils.isEmpty(dictJson.getFilterAttrValue())) {
                        dictJson.setFilterAttrValue(getRangeStr(max, min));
                    }
                }
                LayerStyleFiledParamsDTO dto = LayerStyleFiledParamsDTO.builder()
                        .ruleKey(dictJson.getFilterAttrKey())
                        .ruleValue(dictJson.getFilterAttrValue())
                        .ruleCode(style.getCode())
                        .rangeFlag(rangeFlag)
                        .min(min)
                        .max(max)
                        .fillColor(dictJson.getFillColor())
                        .labelColor(dictJson.getFillTransparency() == 100 ? "#FFFFFF" : dictJson.getBorderColor())
                        .fillOpacity(BigDecimalUtils.longToString100(dictJson.getFillTransparency(), 2))
                        .strokeColor(dictJson.getBorderColor())
                        .strokeOpacity(BigDecimalUtils.longToString100(dictJson.getBorderTransparency(), 2))
                        .strokeWidth(dictJson.getBorderWidth()).build();
                list.add(dto);
            }
            log.info("图例样式{}", JSON.toJSONString(list));
            if (style.getShowLabel() != null && style.getShowLabel() == 1) {
                polygon = GeoserverStyleTemplate.getLableMultipolygonTemplate(list);
                point = GeoserverStyleTemplate.getLablePointTemplate(list);
                line = GeoserverStyleTemplate.getLableLineTemplate(list);
            } else {
                polygon = GeoserverStyleTemplate.getFiledMultipolygonTemplate(list);
                point = GeoserverStyleTemplate.getFiledPointTemplate(list);
                line = GeoserverStyleTemplate.getFiledLineTemplate(list);
            }
        } else {
            FieldStyleDictJson dictJson = style.getDictJson().get(0);
            polygon = GeoserverStyleTemplate.getCommonMultipolygonTemplate(dictJson.getFillColor(), BigDecimalUtils.longToString100(dictJson.getFillTransparency(), 2), dictJson.getBorderColor(), BigDecimalUtils.longToString100(dictJson.getBorderTransparency(), 2), dictJson.getBorderWidth());
            point = GeoserverStyleTemplate.getCommonPointTemplate(dictJson.getFillColor(), BigDecimalUtils.longToString100(dictJson.getFillTransparency(), 2), dictJson.getBorderColor(), BigDecimalUtils.longToString100(dictJson.getBorderTransparency(), 2), dictJson.getBorderWidth());
            line = GeoserverStyleTemplate.getCommonLineTemplate(dictJson.getBorderColor(), BigDecimalUtils.longToString100(dictJson.getFillTransparency(), 2), dictJson.getBorderWidth());
        }
        log.info("样式名称 {}", styleName);

        List<LayerTypeStyleMapping> updates = new ArrayList<>();
        List<LayerTypeStyleMapping> adds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mappings)) {
            for (LayerTypeStyleMapping mapping : mappings) {
                if (PgLayerStyleEnums.POLYGON.getGeoType().equals(mapping.getGeoType())) {
                    polygonStyleName = mapping.getStyleName();
                }
                if (PgLayerStyleEnums.LINESTRING.getGeoType().equals(mapping.getGeoType())) {
                    lineStyleName = mapping.getStyleName();
                }
                if (PgLayerStyleEnums.POINT.getGeoType().equals(mapping.getGeoType())) {
                    pointStyleName = mapping.getStyleName();
                }
                mapping.setCode(style.getCode());
                mapping.setUpdateTime(new Date());
                mapping.setDeleteFlag(0);
                updates.add(mapping);
            }
        } else {
            List<String> geoTypes = Arrays.asList("polygon", "point", "line");
            for (String geoType : geoTypes) {
                LayerTypeStyleMapping mapping = new LayerTypeStyleMapping();
                mapping.setId(UUID.randomUUID().toString());
                mapping.setTypeStyleId(style.getId());
                if (PgLayerStyleEnums.POLYGON.getGeoType().equals(geoType)) {
                    mapping.setStyleName(polygonStyleName);
                }
                if (PgLayerStyleEnums.LINESTRING.getGeoType().equals(geoType)) {
                    mapping.setStyleName(lineStyleName);
                }
                if (PgLayerStyleEnums.POINT.getGeoType().equals(geoType)) {
                    mapping.setStyleName(pointStyleName);
                }
                mapping.setAbbreviation(style.getAbbreviation());
                mapping.setWorkspaceName(workSpaceName);
                mapping.setFieldName(style.getCode());
                mapping.setCreateTime(new Date());
                mapping.setUpdateTime(new Date());
                mapping.setDeleteFlag(0);
                mapping.setGeoType(geoType);
                adds.add(mapping);
            }
        }
        log.info("发布样式 原始 {} polygonStyleName {} pointStyleName {} lineStyleName {}", styleName, polygonStyleName, pointStyleName, lineStyleName);
        //上传点线面样式
        geoService.uploadGeoStyle(polygonStyleName, polygon, workSpaceName);
        geoService.uploadGeoStyle(pointStyleName, point, workSpaceName);
        geoService.uploadGeoStyle(lineStyleName, line, workSpaceName);
        if (!CollectionUtils.isEmpty(adds)) {
            layerTypeStyleMappingMapper.saveBatch(adds);
        }
        if (!CollectionUtils.isEmpty(updates)) {
            layerTypeStyleMappingMapper.updateBatchById(updates);
        }

    }

    @Override
    public void updateLegends(LayerTypeStyleModifyDeleteDto dto) {

        if (!CollectionUtils.isEmpty(dto.getDeleteList())) {
            LayerTypeStyle style = dto.getDeleteList().get(0);
            String workSpaceName = geoService.workSpace(style.getIndustryCode());
            //查询需要删除的样式文件名称
            List<String> typeIds = StreamUtils.toList(dto.getDeleteList(), LayerTypeStyle::getId);
            List<LayerTypeStyleMapping> mappings = layerTypeStyleMappingMapper.selectByTypeId(typeIds, style.getTenantId(), style.getIndustryCode());
            List<String> styleNames = StreamUtils.toList(mappings, LayerTypeStyleMapping::getStyleName);
            if (!CollectionUtils.isEmpty(styleNames)) {
                styleNames.forEach(d -> geoService.deleteGeoStyle(d, workSpaceName));
            }
            if (!CollectionUtils.isEmpty(typeIds)) {
                layerTypeStyleMappingMapper.deletes(typeIds);
            }
        }

        if (!CollectionUtils.isEmpty(dto.getAddList())) {
            dto.getAddList().forEach(this::updateLegend);
        }

        if (!CollectionUtils.isEmpty(dto.getUpdateList())) {
            dto.getUpdateList().forEach(this::updateLegend);
        }
    }


    @Override
    public Map<String, List<LayerTypeStyleMappingVO>> layerStyles(List<String> layerIds,LoginUser loginUser) {
        if (CollectionUtils.isEmpty(layerIds)) {
            return new HashMap<>();
        }
        //根据图层ID查询全部图层
        List<Layer> layers = layerMapper.selectByLayerIds(layerIds);
        if (CollectionUtils.isEmpty(layers)) {
            return new HashMap<>();
        }
        List<String> typeIds = layers.stream().map(Layer::getLayerTypeId).distinct().collect(Collectors.toList());
        List<String> abbreviations = layers.stream().map(Layer::getAbbreviation).distinct().collect(Collectors.toList());
        //查询当前租户下所有类型配置的样式
        List<LayerTypeStyleMapping> mappings = layerTypeStyleMappingMapper.selectByLayerTypeId(typeIds, loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode());
        //铁塔视角样式文件
        List<LayerTypeStyleMapping> adminStyleMappings = layerTypeStyleMappingMapper.selectByAbbreviations(abbreviations);
        //根据类型点线面样式分组
        Map<String, List<LayerTypeStyleMapping>> styleMap = StreamUtils.groupByKey(mappings, d -> d.getAbbreviation() + "_" + d.getGeoType());
        Map<String, List<LayerTypeStyleMapping>> adminStyleMap = StreamUtils.groupByKey(adminStyleMappings, d -> d.getAbbreviation() + "_" + d.getGeoType());
        //把图层根据点线面分组
        List<Layer> list = layers.stream().filter(d -> PgLayerStyleEnums.get(d.getGeoType()) != null).collect(Collectors.toList());
        Map<String, String> layerMap = StreamUtils.toMap(list, d -> d.getAbbreviation() + "_" + PgLayerStyleEnums.get(d.getGeoType()).getGeoType() + "," + d.getId(), Layer::getId);
        //图层和对应的样式
        Map<String, List<LayerTypeStyleMappingVO>> map = new HashMap<>();
        for (Map.Entry<String, String> entry : layerMap.entrySet()) {
            //前面是分组 后面是图层ID
            String[] split = entry.getKey().split(",");
            //用户自定义创建的样式
            List<LayerTypeStyleMapping> styles = styleMap.get(split[0]);
            if (styles != null) {
                List<LayerTypeStyleMappingVO> vos = BeanCopyUtils.copyList(styles, LayerTypeStyleMappingVO.class);
                map.put(entry.getValue(), vos);
            } else {//没有自定义则使用铁塔视角样式
                List<LayerTypeStyleMapping> adminStyles = adminStyleMap.get(split[0]);
                if (adminStyles != null) {
                    List<LayerTypeStyleMappingVO> vos = BeanCopyUtils.copyList(adminStyles, LayerTypeStyleMappingVO.class);
                    map.put(entry.getValue(), vos);
                }
            }
        }
        return map;
    }


    /**
     * 快速生成样式。
     *
     * @param tenantId     租户ID，用于标识租户信息。
     * @param industryCode 行业代码，用于标识行业信息。
     * @param abbreviation 简称，用于标识样式相关的简写。
     * @throws Exception 如果在生成样式过程中发生错误，则抛出异常。
     */
    public void quickGenStyles(String tenantId, String industryCode, String abbreviation) {
        List<LayerTypeStyle> list = layerTypeFieldsStyleMapper.selectLayerTypeStyles(tenantId, industryCode, abbreviation);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (LayerTypeStyle style : list) {
            List<FieldStyleDictJson> dictJsons = JSONArray.parseArray(JSON.toJSONString(style.getDictJson()), FieldStyleDictJson.class);
            style.setDictJson(dictJsons);
        }

        LayerTypeStyleModifyDeleteDto layerTypeStyleModifyDeleteDto = new LayerTypeStyleModifyDeleteDto(
                null, list, list
        );
        log.info("快捷生成样式:{}", JSON.toJSONString(layerTypeStyleModifyDeleteDto));
        this.updateLegends(layerTypeStyleModifyDeleteDto);
    }
}

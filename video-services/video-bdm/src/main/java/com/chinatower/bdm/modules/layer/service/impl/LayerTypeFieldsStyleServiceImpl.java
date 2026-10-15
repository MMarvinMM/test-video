package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.modules.layer.domain.*;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeFieldsStyleMapper;
import com.chinatower.bdm.modules.layer.service.LayerTypeFieldsStyleService;
import com.chinatower.bdm.modules.layer.service.LegendService;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class LayerTypeFieldsStyleServiceImpl implements LayerTypeFieldsStyleService {

    @Resource
    LayerTypeFieldsStyleMapper layerTypeFieldsStyleMapper;

    @Resource
    LegendService legendService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public void addOrModify(LayerTypeStyleVo layerTypeStyleVo) {
        //校验入参字段
        verifyParam(layerTypeStyleVo);
        //设置入库字段
        List<LayerTypeStyle> layerTypeStyles = setLayerTypeStyle(layerTypeStyleVo);
        //存储入参数据
        Map<String, LayerTypeStyle> newMap = new HashMap<>();
        for (LayerTypeStyle item : layerTypeStyles) {
            if (!StringUtils.isEmpty(item.getId())) {
                newMap.put(item.getId(), item);
            }
        }

        List<LayerTypeStyle> oldList = layerTypeFieldsStyleMapper.findByLayerTypeId(layerTypeStyleVo);

        //存储历史数据
        Map<String, LayerTypeStyle> oldMap = new HashMap<>();
        for (LayerTypeStyle item : oldList) {
            oldMap.put(item.getId(), item);
        }

        List<LayerTypeStyle> addList = new ArrayList<>();

        List<LayerTypeStyle> updateList = new ArrayList<>();

        List<LayerTypeStyle> delList = new ArrayList<>();

        if (oldList.isEmpty()) {
            addList.addAll(layerTypeStyles);
        } else {
            for (LayerTypeStyle layerTypeStyle : layerTypeStyles) {

                if (oldMap.containsKey(layerTypeStyle.getId())) {
                    updateList.add(layerTypeStyle);
                } else {
                    addList.add(layerTypeStyle);
                }
            }
            for (LayerTypeStyle layerTypeStyle : oldList) {
                if (!newMap.containsKey(layerTypeStyle.getId())) {
                    delList.add(layerTypeStyle);
                }
            }
        }

        LayerTypeStyleModifyDeleteDto layerTypeStyleModifyDeleteDto = new LayerTypeStyleModifyDeleteDto(
                addList, updateList, delList
        );
        log.info("请求gis:{}",JSON.toJSONString(layerTypeStyleModifyDeleteDto));
        legendService.updateLegends(layerTypeStyleModifyDeleteDto);

        if (CollectionUtil.isNotEmpty(delList)) {
            layerTypeFieldsStyleMapper.deleteBatch(delList);
        }

        if (CollectionUtil.isNotEmpty(addList)) {
            layerTypeFieldsStyleMapper.addBatch(addList);
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            layerTypeFieldsStyleMapper.updateBatch(updateList);
        }


    }



    private List<LayerTypeStyle> setLayerTypeStyle(LayerTypeStyleVo layerTypeStyleVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        layerTypeStyleVo.setTenantId(user.getTenantId());
        layerTypeStyleVo.setIndustryCode(user.getIndustryCode());
        List<LayerTypeStyle> list = new ArrayList<>();
        DateTime date = DateUtil.date();
        if (0 == layerTypeStyleVo.getShowBindField()) {
            LayerTypeStyle layerTypeStyle = new LayerTypeStyle();
            layerTypeStyle.setId(StringUtils.isEmpty(layerTypeStyleVo.getId()) ? IdUtils.simpleUUID() : layerTypeStyleVo.getId());
            layerTypeStyle.setLayerTypeId(layerTypeStyleVo.getLayerTypeId());
            layerTypeStyle.setAbbreviation(layerTypeStyleVo.getAbbreviation());
            layerTypeStyle.setShowBindField(layerTypeStyleVo.getShowBindField());
            layerTypeStyle.setDictJson(layerTypeStyleVo.getDictJson());
            layerTypeStyle.setCreator(user.getUserName());
            layerTypeStyle.setCreateTime(null == layerTypeStyleVo.getCreateTime() ? date : layerTypeStyleVo.getCreateTime());
            layerTypeStyle.setModifier(user.getUserName());
            layerTypeStyle.setTenantId(user.getTenantId());
            layerTypeStyle.setIndustryCode(user.getIndustryCode());
            list.add(layerTypeStyle);
        } else {
            List<StyleFieldsVo> fields = layerTypeStyleVo.getFields();

            for (int i = 0; i < fields.size(); i++) {
                date = DateUtil.offsetSecond(date, 1);
                StyleFieldsVo field = fields.get(i);
                LayerTypeStyle layerTypeStyle = new LayerTypeStyle();
                layerTypeStyle.setId(StringUtils.isEmpty(field.getId()) ? IdUtils.simpleUUID() : field.getId());
                layerTypeStyle.setCode(field.getCode());
                layerTypeStyle.setName(field.getName());
                layerTypeStyle.setAbbreviation(layerTypeStyleVo.getAbbreviation());
                layerTypeStyle.setLayerTypeId(layerTypeStyleVo.getLayerTypeId());
                layerTypeStyle.setShowBindField(layerTypeStyleVo.getShowBindField());
                layerTypeStyle.setShowLabel(field.getShowLabel());
                layerTypeStyle.setDictJson(field.getDictJson());
                layerTypeStyle.setCreator(user.getUserName());
                layerTypeStyle.setCreateTime(date);
                layerTypeStyle.setModifier(user.getUserName());
                layerTypeStyle.setTenantId(user.getTenantId());
                layerTypeStyle.setIndustryCode(user.getIndustryCode());
                layerTypeStyle.setDefaultShow(field.getDefaultShow());
                layerTypeStyle.setType(field.getType());
                list.add(layerTypeStyle);
            }
        }
        return list;
    }

    @Override
    public LayerTypeStyleVo listByLayerTypeId(LayerTypeStyleVo listByLayerTypeId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        listByLayerTypeId.setTenantId(user.getTenantId());
        listByLayerTypeId.setIndustryCode(user.getIndustryCode());
        List<LayerTypeStyle> list = layerTypeFieldsStyleMapper.findByLayerTypeId(listByLayerTypeId);
        log.info("list:{}", JSON.toJSONString(list));
        LayerTypeStyleVo layerTypeStyleVo = new LayerTypeStyleVo();
        if (!list.isEmpty()) {
            layerTypeStyleVo.setLayerTypeId(list.get(0).getLayerTypeId());
            layerTypeStyleVo.setShowBindField(list.get(0).getShowBindField());
            layerTypeStyleVo.setAbbreviation(list.get(0).getAbbreviation());

            List<StyleFieldsVo> fields = new ArrayList<>();
            for (LayerTypeStyle layerTypeStyle : list) {

                if (layerTypeStyle.getShowBindField() == 0) {
                    layerTypeStyleVo.setId(list.get(0).getId());
                    layerTypeStyleVo.setDictJson(list.get(0).getDictJson());
                    break;
                } else {
                    StyleFieldsVo styleFieldsVo = new StyleFieldsVo();
                    styleFieldsVo.setId(layerTypeStyle.getId());
                    styleFieldsVo.setCode(layerTypeStyle.getCode());
                    styleFieldsVo.setName(layerTypeStyle.getName());
                    styleFieldsVo.setType(layerTypeStyle.getType());
                    styleFieldsVo.setShowLabel(layerTypeStyle.getShowLabel());
                    styleFieldsVo.setDictJson(layerTypeStyle.getDictJson());
                    styleFieldsVo.setDefaultShow(layerTypeStyle.getDefaultShow());
                    fields.add(styleFieldsVo);
                }

            }
            layerTypeStyleVo.setFields(fields);

        }
        return layerTypeStyleVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public void delStyle(LayerTypeStyleVo listByLayerType) {
        List<LayerTypeStyle> list = layerTypeFieldsStyleMapper.findByLayerTypeId(listByLayerType);
        if (!list.isEmpty()) {
            LayerTypeStyleModifyDeleteDto layerTypeStyleModifyDeleteDto = new LayerTypeStyleModifyDeleteDto();
            layerTypeStyleModifyDeleteDto.setDeleteList(list);
            //删除geo server 样式
            legendService.updateLegends(layerTypeStyleModifyDeleteDto);
            layerTypeFieldsStyleMapper.deleteBatch(list);
        }
    }

    /**
     * 入参校验
     */
    private void verifyParam(LayerTypeStyleVo layerTypeStyleVo) {
        //数据校验
        if (0 == layerTypeStyleVo.getShowBindField()) {
            //不绑定 取dictJson
            List<FieldStyleDictJson> dictJson = layerTypeStyleVo.getDictJson();
            FieldStyleDictJson fieldStyleDictJson = dictJson.get(0);
            if (null != fieldStyleDictJson) {
                verifyParam(fieldStyleDictJson);
            } else {
                throw new CustomException("入参错误", 400);
            }
        } else {
            List<StyleFieldsVo> fields = layerTypeStyleVo.getFields();
            fields.forEach(item -> item.getDictJson().forEach(this::verifyParam));
        }
    }

    private void verifyParam(FieldStyleDictJson fieldStyleDictJson) {
        Integer fillTransparency = fieldStyleDictJson.getFillTransparency();
        Integer borderTransparency = fieldStyleDictJson.getBorderTransparency();
        if (0 > fillTransparency || fillTransparency > 100 || 0 > borderTransparency || borderTransparency > 100) {
            throw new CustomException("入参错误", 400);
        }
    }

}

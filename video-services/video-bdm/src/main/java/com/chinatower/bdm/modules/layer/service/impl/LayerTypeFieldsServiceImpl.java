package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.bdm.modules.layer.domain.LayerTypeFieldDictDto;
import com.chinatower.bdm.modules.layer.domain.LayerTypeFields;
import com.chinatower.bdm.modules.layer.mapper.*;
import com.chinatower.bdm.modules.layer.service.ILayerTypeService;
import com.chinatower.bdm.modules.layer.service.LayerTypeFieldsService;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.enums.PGLayerTempEnums;
import com.chinatower.platform.enums.SysLayerTypeFieldsTypeEnums;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LayerTypeFieldsServiceImpl implements LayerTypeFieldsService {

    private final LayerTypeFieldsMapper layerTypeFieldsMapper;
    private final LayerMapper layerMapper;
    private final LayerTypeMapper layerTypeMapper;
    private final LayerTypeAttrFilterMapper layerTypeAttrFilterMapper;
    private final VmLayerTemplateConfigMapper layerTemplateConfigMapper;
    private static final String yu_bi_du_1 = "yu_bi_du_1";

    /**
     * 检查图层类型
     */
    private LayerType getLayerType(String abbreviation) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return layerTypeMapper.selectByAbbreviation(abbreviation, loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode());
    }

    public LayerType getLayerTypeById(String id) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerType layerType = layerTypeMapper.selectById(id);
        if (layerType != null && layerType.getCategory() == 0) {
            return layerType;
        }
        if (layerType == null
                || !layerType.getTenantId().equals(loginUser.getUser().getTenantId())
                || !layerType.getIndustryCode().equals(loginUser.getUser().getIndustryCode())
        ) {
            throw new CustomException("图层类型错误");
        }
        return layerType;
    }


    @Override
    @Transactional
    public Result<Void> addOrModifyFields(AddOrModifyLayerTypeFieldsVO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerTypeFields info = layerTypeFieldsMapper.info(dto.getId());
        if (info != null) {
            return modify(dto, info);
        }
        LayerType layerType = getLayerType(dto.getAbbreviation());

        if (layerType.getCategory() == 0 && VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            throw new CustomException("初始化类型不能修改");
        }
        if (StringUtils.isEmpty(dto.getCode())) {
            return Result.fail("字段代码不能为空");
        }
        SysLayerTypeFieldsTypeEnums enums = SysLayerTypeFieldsTypeEnums.get(dto.getType());
        if (SysLayerTypeFieldsTypeEnums.THREE == enums && dto.getChartLength() == null) {
            dto.setChartLength(500);
        }
        checkFieldCode(layerType.getAbbreviation(), dto.getCode());
        LayerTypeFields copy = BeanCopyUtils.copy(dto, LayerTypeFields.class);
        copy.setAbbreviation(layerType.getAbbreviation());
        layerTypeFieldsMapper.insert(copy);
        return Result.success("新增成功");
    }

    @Override
    public Result<Void> delete(String id) {
        layerTypeFieldsMapper.delete(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<Void> deleteByID(String id) {
        layerTypeFieldsMapper.deleteByID(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<Void> deleteByAbbreviation(String abbreviation) {
        List<String> lst = new ArrayList<>();
        lst.add(abbreviation);
        layerTypeFieldsMapper.deleteByAbbreviation(lst);
        return Result.success("删除成功");
    }

    @Override
    public List<LayerTypeFieldDictDto> findFieldsDict(ListByConditionVO req) {
        List<AddOrModifyLayerTypeFieldsVO> infos = infos(req);
        log.info("lllll:{}", JSON.toJSONString(infos));
        return infos.stream()
                .map(info -> {
                    LayerTypeFieldDictDto dict = new LayerTypeFieldDictDto();
                    if (!StringUtils.isEmpty(info.getCode())) {
                        info.setCode(info.getCode().toLowerCase());
                    }
                    BeanUtils.copyProperties(info, dict);
                    if (0 == info.getMatchDictFlag()) {
                        dict.setFilters(null);
                    }
                    return dict;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Result<AddOrModifyLayerTypeFieldsVO> info(String id) {
        LayerTypeFields info = layerTypeFieldsMapper.info(id);
        AddOrModifyLayerTypeFieldsVO copy = BeanCopyUtils.copy(info, AddOrModifyLayerTypeFieldsVO.class);
        return Result.data(copy);
    }

    @Override
    public List<AddOrModifyLayerTypeFieldsVO> infos(ListByConditionVO req) {
        LayerType layerType = getLayerTypeById(req.getId());

        Integer count = layerMapper.selectLayerTypeIdCount(layerType.getTenantId(), layerType.getIndustryCode(), layerType.getId());

        List<LayerTypeFields> infos = layerTypeFieldsMapper.infos(layerType.getAbbreviation());
        List<AddOrModifyLayerTypeFieldsVO> vos = BeanCopyUtils.copyList(infos, AddOrModifyLayerTypeFieldsVO.class);
        List<String> codes = StreamUtils.toList(infos, LayerTypeFields::getCode);
        List<LayerTypeAttrFilterVO> fieldFilters = layerTypeAttrFilterMapper.selectByAbbreviationAndCodes(layerType.getAbbreviation(), codes);
        Map<String, List<LayerTypeAttrFilterVO>> fieldFiltersMap = StreamUtils.groupByKey(fieldFilters, LayerTypeAttrFilterVO::getCode);
        for (AddOrModifyLayerTypeFieldsVO vo : vos) {
            List<LayerTypeAttrFilterVO> dtos = fieldFiltersMap.get(vo.getCode().toLowerCase());
            if (!CollectionUtils.isEmpty(dtos)){
                for (LayerTypeAttrFilterVO dto : dtos) {
                    dto.setCode(vo.getCode());
                }
            }
            vo.setFilters(dtos);
            if (vo.getFilterIndex() != null && vo.getFilterIndex() == 0) {
                vo.setFilterIndex(null);
            }
            if (vo.getIdentifyIndex() != null && vo.getIdentifyIndex() == 0) {
                vo.setIdentifyIndex(null);
            }
            if (vo.getResultIndex() != null && vo.getResultIndex() == 0) {
                vo.setResultIndex(null);
            }
            if (count != null && count >= 1) {
                vo.setHasLayer(1);
            }
            if (vo.getCode().equals(layerType.getAreaRef())) {
                vo.setHasArea(1);
            }else {
                vo.setHasArea(0);
            }
            if (vo.getCode().equals(layerType.getTbbhRef())) {
                vo.setHasTbbh(1);
            }else {
                vo.setHasTbbh(0);
            }
        }
        if (PGLayerTempEnums.SLZY.getLayerType().equals(layerType.getAbbreviation()) || PGLayerTempEnums.SLZY.getLayerType().equals(layerType.getInitialTypeName())) {
            vos = vos.stream().filter(c -> !yu_bi_du_1.equals(c.getCode())).collect(Collectors.toList());
        }
        return vos;
    }

    @Override
    @Transactional
    public Result<Void> copyLayerTypeFields(CopyLayerTypeFieldsDTO dto) {
        //源图层类型字段
        List<LayerTypeFields> sourceFields = layerTypeFieldsMapper.selectByAbbreviation(dto.getTreatAbbreviation());
        List<LayerTypeFields> fields = sourceFields.stream().map(field -> {
            field.setAbbreviation(dto.getTargetAbbreviation());
            return field;
        }).collect(Collectors.toList());
        layerTypeFieldsMapper.saveBatch(fields);

        return Result.success("复制完成");
    }


    @Override
    public Result<List<LayerFieldsDTO>> advancedQueryLayerFields(String abbreviation) {
        if (StringUtils.isEmpty(abbreviation)) {
            return Result.fail("参数错误");
        }
        LayerType layerType = layerTypeMapper.selectByAbbreviation(abbreviation, null, null);
        if (layerType == null) {
            return Result.fail("参数错误");
        }
        List<LayerTypeFields> list = layerTypeFieldsMapper.selectByAbbreviation(abbreviation);
        if (list == null || list.isEmpty()) {
            return Result.fail("暂无字段");
        }
        List<LayerFieldsDTO> dtos = BeanCopyUtils.copyList(list, LayerFieldsDTO.class);
        perfectLayerFieldsDTO(dtos, layerType);
        if (PGLayerTempEnums.SLZY.getLayerType().equals(layerType.getAbbreviation()) || PGLayerTempEnums.SLZY.getLayerType().equals(layerType.getInitialTypeName())) {
            dtos = dtos.stream().filter(c -> !yu_bi_du_1.equals(c.getCode())).collect(Collectors.toList());
        }
        //筛选满足字典的字段
        List<String> dictCodes = dtos.stream().filter(d -> d.getFilterType() != null && (d.getFilterType() == 2 || d.getFilterType() == 3)).map(LayerFieldsDTO::getCode).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(dictCodes)){
            List<LayerTypeAttrFilterVO> filterVOS = layerTypeAttrFilterMapper.selectByAbbreviationAndCodes(abbreviation, dictCodes);
            Map<String, List<LayerTypeAttrFilterVO>> map = StreamUtils.groupByKey(filterVOS, LayerTypeAttrFilterVO::getCode);
            for(LayerFieldsDTO dto : dtos){
                List<LayerTypeAttrFilterVO> vos = map.get(dto.getCode());
                if (!CollectionUtils.isEmpty(vos)) {
                    dto.setDictList(vos);
                }
            }
        }
        return Result.data(dtos);
    }

    @Override
    public Result<List<LayerFieldsDTO>> layerFields(String abbreviation) {
        log.info("layerFields: {}",abbreviation);
        if (StringUtils.isEmpty(abbreviation)) {
            return Result.fail("图层类型简称不能为空");
        }
        LayerType layerType = layerTypeMapper.selectByAbbreviation(abbreviation, null, null);
        if (layerType == null) {
            return Result.fail("图层类型简称不存在");
        }
        List<LayerTypeFields> list;
        if ("qt".equals(layerType.getAbbreviation()) || "qt".equals(layerType.getSuperTypeName())) {
            List<String> abbreviations = new ArrayList<>();
            for (PGLayerTempEnums enums : PGLayerTempEnums.values()) {
                abbreviations.add(enums.getLayerType());
            }
            list = layerTypeFieldsMapper.selectByAbbreviations(abbreviations);
        } else {
            list = layerTypeFieldsMapper.selectByAbbreviation(abbreviation);
        }
        if (list == null || list.isEmpty()) {
            return Result.fail("暂无字段");
        }
        List<LayerFieldsDTO> dtos = BeanCopyUtils.copyList(list, LayerFieldsDTO.class);
        perfectLayerFieldsDTO(dtos, layerType);
        if (PGLayerTempEnums.SLZY.getLayerType().equals(layerType.getAbbreviation()) || PGLayerTempEnums.SLZY.getLayerType().equals(layerType.getInitialTypeName())) {
            dtos = dtos.stream().filter(c -> !yu_bi_du_1.equals(c.getCode())).collect(Collectors.toList());
        }
        //筛选满足字典的字段
        List<String> dictCodes = dtos.stream().filter(d -> d.getFilterType() != null && (d.getFilterType() == 2 || d.getFilterType() == 3)).map(LayerFieldsDTO::getCode).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(dictCodes)){
            List<LayerTypeAttrFilterVO> filterVOS = layerTypeAttrFilterMapper.selectByAbbreviationAndCodes(abbreviation, dictCodes);
            Map<String, List<LayerTypeAttrFilterVO>> map = StreamUtils.groupByKey(filterVOS, LayerTypeAttrFilterVO::getCode);
            for(LayerFieldsDTO dto : dtos){
                dto.setCode(dto.getCode().toLowerCase());
                List<LayerTypeAttrFilterVO> vos = map.get(dto.getCode());
                if (!CollectionUtils.isEmpty(vos)) {
                    dto.setDictList(vos);
                }
            }
        }
        return Result.data(dtos);
    }

    @Override
    public Result<List<LayerFieldsDTO>> layerWpFields(String abbreviation) {
        VmLayerTempConfigDTO dto = new VmLayerTempConfigDTO();
        dto.setAbbreviation(abbreviation);
        LayerTempConfigVO layerTempConfigVO = layerTemplateConfigMapper.queryLayerTemplateIsExists(dto);
        LayerType layerType = BeanCopyUtils.copy(layerTempConfigVO, LayerType.class);
        if (layerType == null) {
            return Result.fail("参数错误");
        }
        List<LayerTypeFields> list = layerTypeFieldsMapper.selectByAbbreviation(abbreviation);
        if (list == null || list.isEmpty()) {
            return Result.fail("暂无字段");
        }
        List<LayerFieldsDTO> dtos = BeanCopyUtils.copyList(list, LayerFieldsDTO.class);
        perfectLayerFieldsDTO(dtos, layerType);
        return Result.data(dtos);
    }

    private void perfectLayerFieldsDTO(List<LayerFieldsDTO> dtos, LayerType layerType) {
        dtos.forEach(dto -> {

            if (dto.getCode().equals(layerType.getTbbhRef())) {
                dto.setTbbhFlag(1);
            }
            if (dto.getFilterType() != null && dto.getFilterType() > 1) {
                dto.setMatchDictFlag(1);
            }
            if (dto.getIdentifyIndex() != null && dto.getIdentifyIndex() > 0) {
                dto.setNeedAlertFlag(1);
            }
            if (dto.getCode().equals(layerType.getAreaRef())) {
                dto.setAreaFlag(1);
            }
            if ("bdm".equals(dto.getCode())) {
                dto.setBsmFlag(1);
            }
            //其他类型之弹窗标识码
            if (("qt".equals(layerType.getAbbreviation()) || "qt".equals(layerType.getSuperTypeName())) && !"bsm".equalsIgnoreCase(dto.getCode())) {
                dto.setNeedAlertFlag(0);
            }
        });
    }

    @Override
    public Result<List<LayerFieldsFilterDTO>> layerFieldsFilters(String abbreviation) {
        List<LayerFieldsFilterDTO> list = layerTypeFieldsMapper.layerFieldsFilters(abbreviation);
        list.forEach(dto -> {
            if (dto.getFilterType() != null && dto.getFilterType() > 1) {
                dto.setMatchDictFlag(1);
            }
            if (dto.getIdentifyIndex() != null && dto.getIdentifyIndex() > 0) {
                dto.setNeedAlertFlag(1);
            }
        });
        return Result.data(list);
    }

    private void checkFieldCode(String abbreviation, String code) {
        LayerTypeFields fieldCode = layerTypeFieldsMapper.selectByFieldCode(abbreviation, code);
        if (fieldCode != null) {
            throw new CustomException("字段已存在，请确认后提交！");
        }
    }

    /**
     * 修改数据
     */
    private Result<Void> modify(AddOrModifyLayerTypeFieldsVO dto, LayerTypeFields info) {
        LayerTypeFields copy = BeanCopyUtils.copy(dto, LayerTypeFields.class);
        if (StringUtils.isNotEmpty(copy.getCode()) && !info.getCode().equals(copy.getCode())) {
            checkFieldCode(info.getAbbreviation(), dto.getCode());
        }
        layerTypeFieldsMapper.update(copy);
        return Result.success("修改成功");
    }
}

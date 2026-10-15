package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.modules.layer.domain.LayerTypeFieldDictDto;
import com.chinatower.common.core.dto.bdm.AddOrModifyLayerTypeFieldsVO;
import com.chinatower.common.core.dto.bdm.CopyLayerTypeFieldsDTO;
import com.chinatower.common.core.dto.bdm.LayerFieldsDTO;
import com.chinatower.common.core.dto.bdm.LayerFieldsFilterDTO;
import com.chinatower.common.entity.Result;

import java.util.List;

public interface LayerTypeFieldsService {

    /**
     * 新增修改字段
     */
    Result<Void> addOrModifyFields(AddOrModifyLayerTypeFieldsVO dto);

    /**
     * 查询字段详情
     */
    Result<AddOrModifyLayerTypeFieldsVO> info(String id);

    /**
     * 复制图层类型字段
     */
    Result<Void> copyLayerTypeFields(CopyLayerTypeFieldsDTO dto);

    /**
     * 查询图层字段
     */
    Result<List<LayerFieldsDTO>> layerFields(String abbreviation);

    /**
     * 卫片图层类型字段
     */
    Result<List<LayerFieldsDTO>> layerWpFields(String abbreviation);

    /**
     * 高级查询图层字段
     */
    Result<List<LayerFieldsDTO>> advancedQueryLayerFields(String abbreviation);

    /**
     * 查询图层条件筛选
     */
    Result<List<LayerFieldsFilterDTO>> layerFieldsFilters(String abbreviation);

    List<AddOrModifyLayerTypeFieldsVO> infos(ListByConditionVO req);

    Result<Void> delete(String id);

    Result<Void> deleteByID(String id);

    Result<Void> deleteByAbbreviation(String abbreviation);

    List<LayerTypeFieldDictDto> findFieldsDict(ListByConditionVO req);
}

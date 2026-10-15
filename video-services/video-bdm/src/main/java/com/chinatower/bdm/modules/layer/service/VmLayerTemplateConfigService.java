package com.chinatower.bdm.modules.layer.service;

import com.chinatower.common.core.dto.bdm.VmLayerTempFieldsVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.core.dto.bdm.VmLayerTempConfigDTO;

import java.util.List;

/**
 * 图层模板配置业务接口
 * @author lyy
 * @version v1.6.60
 * @date 2024/04/29 14:36
 */
public interface VmLayerTemplateConfigService {

    /**
     * 分页查询图层模板配置
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    Result<Object> getLayerTempConfigList(VmLayerTempConfigDTO layerTempConfigDTO);

    /**
     * 根据图层模板ID查询,校验当前图层模板下是否已有图层
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    Result<Object> queryLayerTemplateIsExists(VmLayerTempConfigDTO layerTempConfigDTO);

    /**
     * 查看图层模板字段详情、编辑回显
     * @param layerTempConfigVO
     * @return Result<Object>
     */
    List<VmLayerTempFieldsVO> queryLayerTempFieldsDetail(VmLayerTempConfigDTO layerTempConfigVO);

    /**
     * 校验t图层模板名称是否唯一
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    Result<Object> getLayerTempNameCount(VmLayerTempConfigDTO layerTempConfigDTO);

    /**
     * 新增图层模版属性、字典配置信息
     * @param tempConfigDTO
     * @return Result<String>
     */
    Result<String> addOrModifyLayerTempConfig(VmLayerTempConfigDTO tempConfigDTO) throws Exception;

    /**
     * 获取初始化图层模版信息
     * @param configDTO
     * @return
     */
    Result<Object> selectLayerFieldsByType(VmLayerTempConfigDTO configDTO);

    /**
     * 图层模板下拉列表
     * @param configDTO
     * @return
     */
    Result<Object> getLayerTempByTypeList(VmLayerTempConfigDTO configDTO);

    /**
     * 删除图层模板配置
     * @return Result<String>
     */
    Result<String> deleteLayerTempConfig(String layerTempId);
}

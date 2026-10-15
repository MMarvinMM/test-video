package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.entity.Result;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * <p>
 * 图层类别管理 服务类
 * </p>
 *
 * @author ch
 * @since 2023-04-06
 */
public interface ILayerTypeService {
    /**
     * 图层列表
     * @return
     */
    Result<List<LayerTypeDTO>> list(String typeName);

    /**
     *
     * @param vos 参数
     * @return 新增修改图层类型名称
     */
    Result<String> addLayerTypes(List<LayerTypeDTO> vos);

    /**
     * 校验重复名称
     */
    Result<String> layerTypeNamesRepeat(List<LayerTypeNamesRepeatVO> vos);

    /**
     *
     * @param
     * @return 删除类别
     */
    Result<String> deleted(List<String> ids);

    /**
     * 删除校验
     * @param ids
     * @return
     */
    Result<String> deleteCheck(List<String> ids);
    Result<String> validateLayerType(List<String> ids);

    /**
     * 图层类型复制数据返回
     */
    Result<LayerTypeDTO> copyGetData(String layerTypeId);
    /**
     * 图层类型复制入库
     */
    Result<String> copyAddData(String layerTypeId);

    /**
     * 新增类型
     */
    Result<String> addLayerType(@RequestBody SysLayerTypeDTO dto);

    /**
     * 修改图层类型
     */
    Result<String> modifyLayerType(@RequestBody SysLayerTypeDTO dto);

    /**
     * 图层类别列表
     */
    List<LayerTypeListVO> listByCondition(ListByConditionVO req);

    /**
     * @return 创建“其他类型”数据类型的表信息
     */
    Result<String>  createQtTypeInfo();

    List<LayerTypeAttrFilterVO> dictByAbbreviation(String abbreviation);
}

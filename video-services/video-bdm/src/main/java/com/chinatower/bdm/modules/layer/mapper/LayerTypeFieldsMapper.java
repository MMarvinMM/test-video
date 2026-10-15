package com.chinatower.bdm.modules.layer.mapper;

import com.chinatower.bdm.modules.layer.domain.LayerTypeFields;
import com.chinatower.common.core.dto.bdm.LayerFieldsFilterDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @desription: 图层类型字段
 * @data 创建时间：2024/1/17 16:58
 * @author chuan hao
 * @version 1.0
 *
 */
public interface LayerTypeFieldsMapper {
    /**
     * 新增
     * @author ch
     * @date 2024/01/17
     **/
    int insert(LayerTypeFields layerTypeFields);

    /**
     * 批量新增
     */
    int saveBatch(@Param("list") List<LayerTypeFields> list);

    /**
     * 刪除
     * @author ch
     * @date 2024/01/17
     **/
    int delete(String id);
    /**
     * 删除类型字段
     */
    int deleteByAbbreviation(@Param("list")List<String> list);

    /**
     * 删除图层类型字段
     */
    int deleteByIds(@Param("ids")List<String> ids);

    /**
     * 批量删除
     */
    int deletes(@Param("ids") List<String> ids);

    /**
     * 更新
     * @author ch
     * @date 2024/01/17
     **/
    int update(LayerTypeFields layerTypeFields);

    /**
     * 查询 根据主键 id 查询
     * @author ch
     * @date 2024/01/17
     **/
    LayerTypeFields info(String id);

    /**
     * 根据字段查询数据
     */
    LayerTypeFields selectByFieldCode(@Param("abbreviation")String abbreviation,@Param("code")String code);
    List<LayerTypeFields> selectByFieldCodes(@Param("abbreviation")String abbreviation,@Param("codes")List<String> codes);

    /**
     * 查询类型字段
     */
    List<LayerTypeFields> selectByAbbreviation(@Param("abbreviation")String abbreviation);
    List<LayerTypeFields> selectByAbbreviations(@Param("abbreviations")List<String> abbreviations);

    /**
     * 查询类型筛选属性
     */
    List<LayerFieldsFilterDTO> layerFieldsFilters(@Param("abbreviation")String abbreviation);

    /**
     * 修改原简称
     */
    int updateLayerTypeFieldsAbbreviation(@Param("sourceAbbreviation")String sourceAbbreviation,@Param("targetAbbreviation")String targetAbbreviation);

    List<LayerTypeFields> infos(@Param("abbreviation") String abbreviation);

    void deleteByID(@Param("id") String id);
}

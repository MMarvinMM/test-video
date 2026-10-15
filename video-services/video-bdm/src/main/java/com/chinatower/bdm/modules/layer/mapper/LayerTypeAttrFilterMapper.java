package com.chinatower.bdm.modules.layer.mapper;

import com.chinatower.bdm.modules.layer.domain.LayerTypeAttrFilter;
import com.chinatower.common.core.dto.bdm.LayerFieldsFilterChildListDTO;
import com.chinatower.common.core.dto.bdm.LayerTypeAttrFilterVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LayerTypeAttrFilterMapper {

    /**
     * 删除图层字典
     */
    int deleteByIds(@Param("ids")List<String> ids);

    /**
     * 删除类型字段
     */
    int deleteByAbbreviation(@Param("list")List<String> list);

    /**
     * 新增
     * @author ch
     * @date 2024/01/22
     **/
    int insert(LayerTypeAttrFilter layerTypeAttrFilter);


    /**
     * 更新
     * @author ch
     * @date 2024/01/22
     **/
    int update(LayerTypeAttrFilter layerTypeAttrFilter);

    /**
     * 批量新增
     */
    int saveBatch(@Param("list") List<LayerTypeAttrFilter> list);

    /**
     * 查询类型简称
     */
    List<LayerTypeAttrFilter> selectByAbbreviation(@Param("abbreviation")String abbreviation);

    List<LayerTypeAttrFilterVO> selectVOByAbbreviation(@Param("abbreviation")String abbreviation);

    List<LayerFieldsFilterChildListDTO> selectByAbbreviationAndCode(@Param("abbreviation")String abbreviation, @Param("code")String code);

    /**
     * 查询字段字典
     */
    List<LayerTypeAttrFilterVO> selectByAbbreviationAndCodes(@Param("abbreviation")String abbreviation, @Param("codes")List<String> codes);
}

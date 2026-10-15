package com.chinatower.bdm.modules.layer.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 图层类型模式管理 Mapper 接口
 * </p>
 *
 * @author ch
 * @since 2023-04-17
 */
public interface LayerModeMapper  {

    /**
     *
     * @param id
     * @return 查询记忆
     */
    String getLayerMode(String id);

    /**
     *
     * @param id
     * @param value 记忆值
     * @return 新增记忆
     */
    int insertLayerMode(@Param("id")String id,@Param("value")String value);

    /**
     *
     * @param id
     * @param value
     * @return 修改记忆类型
     */
    int updateLayerMode(@Param("id")String id,@Param("value")String value);

    /**
     *
     * @param id
     * @return 查询
     */
    int selectLayerMode(@Param("id")String id);
}

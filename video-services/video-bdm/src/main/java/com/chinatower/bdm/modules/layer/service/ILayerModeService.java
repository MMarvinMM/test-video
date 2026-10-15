package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.modules.layer.domain.LayerMode;
import com.chinatower.common.entity.Result;

/**
 * <p>
 * 图层类型模式管理 服务类
 * </p>
 *
 * @author ch
 * @since 2023-04-17
 */
public interface ILayerModeService  {
    /**
     *
     * @return 查询自己图层模式
     */
    Result<LayerMode> info();

    /**
     *
     * @param modeValue  0-默认 1-地图 2-球形
     * @return 修改模式
     */
    Result<String> addOrModify(Integer modeValue);
}

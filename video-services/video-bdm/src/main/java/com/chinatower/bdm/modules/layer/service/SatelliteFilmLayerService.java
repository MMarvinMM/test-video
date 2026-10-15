package com.chinatower.bdm.modules.layer.service;

import com.chinatower.common.core.dto.bdm.LayerIdVo;
import com.chinatower.common.core.dto.bdm.SatelliteFilmLayerUploadVo;

public interface SatelliteFilmLayerService {

    /**
     * 新增图层
     * @param dto
     */
    void satelliteFilmLayerUpload(SatelliteFilmLayerUploadVo dto);

    /**
     * 生成告警
     */
    void createAlarm(LayerIdVo vo);
}

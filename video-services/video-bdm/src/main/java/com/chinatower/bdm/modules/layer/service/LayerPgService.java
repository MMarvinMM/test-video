package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.api.entity.MineralsLayerReqVO;
import com.chinatower.bdm.api.entity.MineralsLayerVO;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.common.core.dto.bdm.LayerAddDTO;
import com.chinatower.common.core.dto.bdm.LayerResponseDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.LayerInfoAddDTO;
import com.chinatower.gis.api.entity.LayerPGDTO;

import java.util.concurrent.ThreadPoolExecutor;

public interface LayerPgService {
    /**
     * 新增图层
     * @param dto
     */
    String addLayer(LayerAddDTO dto);

    /**
     * 发送图层信息
     */
    void sendGeoJson(String value);

    /**
     * 编辑图层
     */
    String editLayer(LayerAddDTO dto);

    /**
     * 查询图层类型
     */
    LayerType getLayerType(String layerTypeId);


    /**
     * 查询探而未采图层ID
     */
    MineralsLayerVO mineralsLayer(int count, MineralsLayerReqVO req);

    /**
     * 探而未采图层保存
     */
    Result mineralsLayerSave(LayerInfoAddDTO req,String nodeCode,String appVerCode);

    ThreadPoolExecutor getLayerInsertPg();

    /**
     * 解析入库图层
     */
    int analysisShp(LayerPGDTO pgdto, ThreadPoolExecutor executorService, String fileUrl, String fileName);
}

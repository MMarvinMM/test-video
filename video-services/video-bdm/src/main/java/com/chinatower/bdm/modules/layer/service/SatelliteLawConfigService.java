package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.modules.inspectionRoute.entity.LayerInfoVO;
import com.chinatower.common.core.vo.SatelliteLawConfigVo;
import com.chinatower.common.entity.Result;

import java.util.List;

/**
 * 卫片执法配置相关接口
 * @author cuisj
 * @date 2024-04-24
 * @requestID 卫片执法配置相关接口
 */
public interface SatelliteLawConfigService {

    /**
     * 卫片执法配置列表查询
     * @param vo
     * @return
     */
    List<SatelliteLawConfigVo> getList(SatelliteLawConfigVo vo);

    /**
     * 查询图斑信息
     * @param vo
     * @return
     */
    List<LayerInfoVO> getLayerInfo(LayerInfoVO vo);


    /**
     * 卫片执法配置修改
     * @param vo
     * @return
     */
    void updateConfig(SatelliteLawConfigVo vo);

    /**
     * 卫片执法配置逻辑删除
     * @param vo
     * @return
     */
    void deleteConfig(SatelliteLawConfigVo vo);

    /**
     * 卫片执法配置判断名称是否重复
     * @param vo
     * @return
     */
    Result checkRepeatName(SatelliteLawConfigVo vo);
}

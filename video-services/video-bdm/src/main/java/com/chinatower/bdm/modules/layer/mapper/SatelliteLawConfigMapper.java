package com.chinatower.bdm.modules.layer.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.modules.inspectionRoute.entity.LayerInfoVO;
import com.chinatower.bdm.modules.layer.domain.VmSatelliteLayerConfig;
import com.chinatower.common.core.dto.bdm.VmSatelliteLayerConfigDTO;
import com.chinatower.common.core.vo.SatelliteLawConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 卫片执法配置相关接口
 * @author cuisj
 * @date 2024-04-24
 * @requestID 卫片执法配置相关接口
 */
@Mapper
public interface SatelliteLawConfigMapper {

    /**
     * 卫片执法配置列表查询
     * @param vo
     * @return
     */
    @DS("db1")
    List<SatelliteLawConfigVo> getList(SatelliteLawConfigVo vo);

    /**
     * 查询图斑信息
     * @param vo
     * @return
     */
    @DS("db1")
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
     */
    void deleteConfig(SatelliteLawConfigVo vo);

    /**
     * 卫片执法配置判断名称是否重复
     * @param vo
     */
    SatelliteLawConfigVo getConfigByOne(SatelliteLawConfigVo vo);

    List<Map<String,String>> getAlarmType(List<String> typeList);

    /**
     * 自定义卫片执法名称是否已存在
     */
    int selectCustomNameCount(@Param("tenantId") String tenantId,
                              @Param("industryCode") String industryCode,
                              @Param("customName") String customName);


    /**
     * 自定义卫片执法名称是否已存在
     */
    int selectCustomNameCountNotId(@Param("tenantId") String tenantId,
                              @Param("industryCode") String industryCode,
                              @Param("customName") String customName,
                                   @Param("id") String id

                                    );
    /**
     * 新增
     * @author ch
     * @date 2024/05/07
     **/
    int insert(VmSatelliteLayerConfig vmSatelliteLayerConfig);

    /**
     * 修改
     * @author ch
     * @date 2024/05/07
     **/
    int update(VmSatelliteLayerConfig vmSatelliteLayerConfig);

    /**
     * 图层数据
     */
    VmSatelliteLayerConfigDTO selectByVmSatelliteLayerConfigDTO(@Param("layerId")String layerId);

    List<Map<String,String>> getLayerTemplate(List<String> templateList);

    List<String> getLayerTemplateId(SatelliteLawConfigVo vo);
    /**
     * 修改生成告警状态
     */
    int updateIsBuildAlarm(@Param("layerId")String layerId,@Param("modifier")String modifier);
}

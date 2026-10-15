package com.chinatower.device.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.RadarAlarmTemplateDO;
import com.chinatower.device.dto.CancelUnionRegionDTO;
import com.chinatower.device.entity.dto.RadarAlarmTemplateDTO;
import com.chinatower.device.dto.RegionTemplateRelDTO;
import com.chinatower.device.dto.TemplateRegionRelDTO;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.chinatower.device.entity.vo.RadarAlarmBlackWhiteVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 雷达告警模板配置
 */
public interface RadarAlarmTemplateMapper extends BaseMapper<RadarAlarmTemplateDO> {

    List<RadarAlarmTemplateVO> queryAlarmTemplateList(@Param("dto")RadarAlarmTemplateDTO radarAlarmTemplateDTO);

    int updateTemplateStatusById(@Param("param")RadarAlarmTemplateDTO dto);

    RadarAlarmTemplateVO getTemplateInfoById(@Param("param")RadarAlarmTemplateDTO dto);

    List<RadarAlarmRegionVo> getRegionInfoByTemplateId(Long templateId);

    int addRelInfo(@Param("list") List<TemplateRegionRelDTO> templateRegionRelInfoList);

    int addWhiteInfo(@Param("list")List<RadarAlarmBlackWhiteVO> radarAlarmWhiteList);

    List<String> getTemplateNameList(Long templateId,String tenantId,String industryCode);

    int deleteTemplateById(@Param("param") RegionTemplateRelDTO dto);

    int deleteTemplateRegionRelInfo(@Param("param") CancelUnionRegionDTO dto);

    /**
     * 获取告警模板默认名称
     * @param tenantId 客户id
     * @param industryCode 行业编码
     * @return 告警模板默认名称
     */
    String getAutoConfigName(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    List<RadarAlarmRegionVo> getRegionListByTemplateId(@Param("list")List<Long> templateIdList);

}

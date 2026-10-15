package com.chinatower.event.mapper;

import com.chinatower.event.dataobject.FlowTemplateIndustryDO;
import com.chinatower.event.dto.FlowTemplateDTO;
import com.chinatower.event.entity.dto.FlowLinkIndustryConfigDO;
import com.chinatower.event.entity.dto.FlowTemplateIndustryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author lyy
 * @version 1.0
 * @date 2022/7/18 14:27
 */
@Mapper
public interface AlarmFlowDeployMapper {

    /**
     * 根据条件查询默认的流程配置
     * @param flowTemplateDTO
     * @return List<FlowTemplateDTO>
     */
    List<FlowTemplateDTO> getFlowTemplateList(FlowTemplateDTO flowTemplateDTO);

    /**
     * 根据行业应用版本、行业编码查询模板ID集合
     * @param param
     * @return String
     */
    String getFlowTemplateIds(@Param("param") Map<String, String> param);

    /**
     * 根据行业应用版本、行业编码查询模板ID集合
     * @param param
     * @return String
     */
    String getFlowTemplateIdsLocalVersion(@Param("param") Map<String, String> param);

    /**
     * 根据行业应用版本、行业编码查询默认流程关联告警类型信息
     * @param param
     * @return List<FlowTemplateIndustryDO>
     */
    List<FlowTemplateIndustryDO> queryFlowTemplateIndustry(@Param("param") Map<String, String> param);

    /**
     * 根据行业应用版本、行业编码查询默认流程关联告警类型信息
     * @param param
     * @return List<FlowTemplateIndustryDO>
     */
    List<FlowTemplateIndustryDO> queryFlowTemplateIndustryLocalVersion(@Param("param") Map<String, String> param);

    /**
     *  根据行业应用版本、行业编码查询流程环节配置信息
     * @param param
     * @return List<FlowLinkIndustryConfigDO>
     */
    List<FlowLinkIndustryConfigDO> queryFlowLinkIndustryConfig(@Param("param") Map<String, String> param);

    /**
     *  根据行业应用版本、行业编码查询流程环节配置信息
     * @param param
     * @return List<FlowLinkIndustryConfigDO>
     */
    List<FlowLinkIndustryConfigDO> queryFlowLinkIndustryConfigLocalVersion(@Param("param") Map<String, String> param);

    /**
     * 批量新增流程模板关联的告警类型
     * @param templateIndustryList
     * @return Integer
     */
    Integer addFlowTemplateIndustry(@Param("templateIndustryList") List<FlowTemplateIndustryDO> templateIndustryList);

    /**
     * 批量新增流程模板关联的告警类型
     * @param templateIndustryList
     * @return Integer
     */
    Integer addFlowTemplateIndustryLocalVersion(@Param("templateIndustryList") List<FlowTemplateIndustryDO> templateIndustryList);

    /**
     * 批量新增流程环节配置
     * @param industryConfigList
     * @return Integer
     */
    Integer addFlowLinkIndustryConfig(@Param("industryConfigList") List<FlowLinkIndustryConfigDO> industryConfigList);

    /**
     * 批量新增流程环节配置
     * @param industryConfigList
     * @return Integer
     */
    Integer addFlowLinkIndustryConfigLocalVersion(@Param("industryConfigList") List<FlowLinkIndustryConfigDO> industryConfigList);


    /**
     * 根据行业应用版本、行业编码删除流程告警类型
     * 表：vm_flow_template_industry
     * @param industryDTO
     * @return Integer
     */
    Integer delTemplateIndustry(FlowTemplateIndustryDTO industryDTO);

    /**
     * 根据行业应用版本、行业编码删除流程告警类型
     * 表：vm_flow_template_industry
     * @param industryDTO
     * @return Integer
     */
    Integer delTemplateIndustryLocalVersion(FlowTemplateIndustryDTO industryDTO);

    /**
     * 根据行业应用版本、行业编码删除流程环节配置
     * 表：vm_flow_link_industry_config
     * @param industryDTO
     * @return Integer
     */
    Integer delIndustryConfig(FlowTemplateIndustryDTO industryDTO);

    /**
     * 根据行业应用版本、行业编码删除流程环节配置(属地版本)
     * 表：vm_flow_link_industry_config
     * @param industryDTO
     * @return Integer
     */
    Integer delIndustryConfigLocalVersion(FlowTemplateIndustryDTO industryDTO);

}

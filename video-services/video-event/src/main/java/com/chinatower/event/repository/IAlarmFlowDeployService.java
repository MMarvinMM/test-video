package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.FlowTemplateDTO;
import com.chinatower.event.entity.dto.FlowTemplateIndustryDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;

import java.util.List;
import java.util.Map;


/**
 * 默认告警流程配置模板接口
 * @author lyy
 * @version v1.5
 * @date 2022/7/18 14:42
 */
public interface IAlarmFlowDeployService {
    /**
     * 查询默认流程模板信息
     * @author lyy
     * @param flowTemplateDTO
     * @return Result<Object>
     */
    Result<Object> getFlowTemplateList(FlowTemplateDTO flowTemplateDTO);

    /**
     * 根据行业应用版本、行业编码查询
     * vm_flow_template_industry（流程模板关联告警类型表）
     * vm_flow_link_industry_config（流程环节配置）
     * @author lyy
     * @param params
     * @return Result<Object>
     */
    Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustry(Map<String, String> params);

    /**
     * 根据行业应用版本、行业编码查询(属地版本)
     * vm_flow_template_industry（流程模板关联告警类型表）
     * vm_flow_link_industry_config（流程环节配置）
     * @author lyy
     * @param params
     * @return Result<Object>
     */
    Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustryLocalVersion(Map<String, String> params);

    /**
     * 新增行业、流程、环节配置信息
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    Result<Object> addFlowLinkConfigIndustry(FlowTemplateIndustryDTO templateIndustryDTO);

    /**
     * 新增行业、流程、环节配置信息(属地版本)
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    Result<Object> addFlowLinkConfigIndustryLocalVersion(FlowTemplateIndustryDTO templateIndustryDTO);

    /**
     * 订购初始化流程配置
     * @author lyy
     * @param flowConfigModel
     * @return Result<Object>
     */
    Result<Object> addAlarmFlowTypeAndConfig(VmAlarmFlowConfigModel flowConfigModel);

    /**
     * 功能描述： 判断角色是否已经被流程使用
     * @author liusq
     * @param param roleId,tenantId, industryCode, appVerCode
     * @return
     */
    Result<Object> getFlowsByRole(Map param);

    /**
     * 功能描述： 判断用户是否已经被流程使用
     * @author liusq
     * @param param userId,tenantId, industryCode, appVerCode
     * @return
     */
    Result<Object> getFlowsByUser(Map param);

    /**
     * 功能描述：判断工单表中是否有正在流转的事件
     * @author lyy
     * @param flowsHistory 告警流程配置list  两个参数有一个就行
     * @param templateId 流程模板id
     * @return
     */
    Result<Object> flowInUse(List<Map> flowsHistory, Long templateId,String userId);

}

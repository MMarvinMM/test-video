package com.chinatower.receive.service;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkDto;
import com.chinatower.receive.vo.entity.VmFlowLinkSearchDto;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 16:01
 */
public interface IFlowTemplateRepository {

    /**
     * 根据告警类型查询流程模板关联告警类型表，查询模板信息
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplateByAlarmType(FlowAlarmTemplateDto param);

    /**
     * 根据行业版本和告警来源查询行业初始化时的模板
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplateByIndustryAndWarningSource(FlowAlarmTemplateDto param);

    /**
     * 根据传入条件查询行业版本初始化配置模板信息
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param);

    /**
     * 查询流程环节节点
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询节点
     * @param flowLinkSearchDto
     * @return
     */
    VmFlowTemplateLinkDto getFlowLinkByCondition(VmFlowLinkSearchDto flowLinkSearchDto);

}

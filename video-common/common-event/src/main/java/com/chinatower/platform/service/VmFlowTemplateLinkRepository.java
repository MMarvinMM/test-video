package com.chinatower.platform.service;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.platform.entity.IVmFlowLinkSearchDto;
import com.chinatower.platform.entity.IIVmFlowTemplateLinkDto;

/**
 * @Description 告警流程环节 Dao层业务类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
public interface VmFlowTemplateLinkRepository {

    /**
     * 查询流程开始节点
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询开始节点
     * @param flowAlarmTemplateDto
     * @return
     */
    IIVmFlowTemplateLinkDto getStartFlowLinkByCondition(FlowAlarmTemplateDto flowAlarmTemplateDto);

    /**
     * 查询流程环节节点
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询节点
     * @param flowLinkSearchDto
     * @return
     */
    IIVmFlowTemplateLinkDto getFlowLinkByCondition(IVmFlowLinkSearchDto flowLinkSearchDto);
}

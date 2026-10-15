package com.chinatower.receive.service;

import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.receive.vo.dto.VmFlowLinkDto;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkDto;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 15:59
 */
public interface IFlowLinkService {

    public FlowAlarmTemplateDto getFlowTemplate(String appVerCode, String industryCode, String tenantId, String warningSource, String alarmType);

    public VmFlowTemplateLinkDto getFlowTemplateLink(Long templateId, Long alarmFlowId, Long realLinkType, Integer linkId, String industryCode, String appVerCode);

    VmFlowLinkDto getFlowLinkAndConfig(String appVerCode, String industryCode, String tenantId, Long templateId, Long alarmFlowId, Integer linkId) throws Exception;

    List<FlowModel> getFlowLink(AlarmFlowConfigModel model);

    VmFlowLinkAttrDto getFlowLinkAttrBycondition(String appVerCode, String industryCode, String tenantId, Long templateId, Long alarmFlowId, Integer linkId) throws Exception;
}

package com.chinatower.platform.service;

import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.FlowOverdueRemind;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.entity.IIVmFlowTemplateLinkDto;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 11:40
 */
public interface FlowLinkService {

    IIVmFlowTemplateLinkDto getStartFlowLink(String appVerCode, String industryCode, String tenantId, String warningSource, String alarmType);

    List<FlowModel> getFlowLink(AlarmFlowConfigModel model);

    VmFlowLinkAttrDto getFlowLinkAttrByAlarmFlowId(String tenantId, Long templateId, Long alarmFlowId, Integer linkId);

    IIVmFlowTemplateLinkDto getFlowTemplateLink(Long templateId, Long alarmFlowId, Long realLinkType, Integer linkId, String industryCode, String appVerCode);

    List<FlowOverdueRemind> getFlowOverdueRemindByAttrId(String attrId);
}
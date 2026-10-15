package com.chinatower.receive.service.impl;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import com.chinatower.receive.mapper.IVmFlowTemplateMapper;
import com.chinatower.receive.service.IFlowTemplateRepository;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkDto;
import com.chinatower.receive.vo.entity.VmFlowLinkSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 16:02
 */
@Service
public class FlowTemplateRepositoryImplImpl implements IFlowTemplateRepository {

    @Autowired
    private IVmFlowTemplateMapper IVmFlowTemplateMapper;
    @Override
    public FlowAlarmTemplateDto selectFlowTemplateByAlarmType(FlowAlarmTemplateDto param) {
        return IVmFlowTemplateMapper.selectFlowTemplateByAlarmType(param);
    }

    @Override
    public FlowAlarmTemplateDto selectFlowTemplateByIndustryAndWarningSource(FlowAlarmTemplateDto param) {
        return IVmFlowTemplateMapper.selectFlowTemplateByIndustryAndWarningSource(param);
    }

    @Override
    public FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param) {
        return IVmFlowTemplateMapper.selectFlowTemplate(param);
    }

    @Override
    public VmFlowTemplateLinkDto getFlowLinkByCondition(VmFlowLinkSearchDto flowLinkSearchDto) {
        VmFlowTemplateLinkDto flowLinkByTemplateId = IVmFlowTemplateMapper.getFlowLinkByTemplateId(flowLinkSearchDto);
        if(flowLinkByTemplateId != null){
            flowLinkSearchDto.setSignFlag("1");
        }
        return IVmFlowTemplateMapper.getFlowLinkByCondition(flowLinkSearchDto);
    }



}

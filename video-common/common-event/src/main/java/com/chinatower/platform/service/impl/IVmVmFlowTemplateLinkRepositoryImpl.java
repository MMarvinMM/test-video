package com.chinatower.platform.service.impl;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.platform.entity.IVmFlowLinkSearchDto;
import com.chinatower.platform.entity.IIVmFlowTemplateLinkDto;
import com.chinatower.platform.mapper.IVmFlowTemplateLinkMapper;
import com.chinatower.platform.service.VmFlowTemplateLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 14:37
 */
@Slf4j
@Service("vmFlowTemplateLinkRepository")
public class IVmVmFlowTemplateLinkRepositoryImpl implements VmFlowTemplateLinkRepository {

    @Autowired
    private IVmFlowTemplateLinkMapper vmFlowTemplateLinkMapper;

    @Override
    public IIVmFlowTemplateLinkDto getStartFlowLinkByCondition(FlowAlarmTemplateDto flowAlarmTemplateDto) {
        return  vmFlowTemplateLinkMapper.getStartFlowLinkByCondition(flowAlarmTemplateDto);
    }

    @Override
    public IIVmFlowTemplateLinkDto getFlowLinkByCondition(IVmFlowLinkSearchDto flowLinkSearchDto) {
        IIVmFlowTemplateLinkDto flowLinkByTemplateId = vmFlowTemplateLinkMapper.getFlowLinkByTemplateId(flowLinkSearchDto);
        if(flowLinkByTemplateId != null){
            flowLinkSearchDto.setSignFlag("1");
        }
        return vmFlowTemplateLinkMapper.getFlowLinkByCondition(flowLinkSearchDto);
    }
}

package com.chinatower.receive.service.impl;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.receive.mapper.IVmFlowLinkAttrMapper;
import com.chinatower.receive.service.IFlowLinkAttrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 18:40
 */
@Service
public class FlowLinkAttrRepositoryImpl implements IFlowLinkAttrRepository {

    @Autowired
    private IVmFlowLinkAttrMapper IVmFlowLinkAttrMapper;

    @Override
    public VmFlowLinkAttrDto getFlowLinkAttrByCondition(VmFlowLinkAttrDto flowLinkAttrDto) {
        return IVmFlowLinkAttrMapper.getFlowLinkAttrByCondition(flowLinkAttrDto);
    }
}

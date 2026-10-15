package com.chinatower.platform.service.impl;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.mapper.FlowLinkIndustryConfigMapper;
import com.chinatower.platform.service.VmFlowLinkIndustryConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 15:01
 */

@Slf4j
@Service("vmFlowLinkIndustryConfigRepository")
public class VmFlowLinkIndustryConfigRepositoryImpl implements VmFlowLinkIndustryConfigRepository {

    @Autowired
    private FlowLinkIndustryConfigMapper flowLinkIndustryConfigMapper;

    @Override
    public VmFlowLinkAttrDto getFlowLinkIndustryConfigByCondition(VmFlowLinkAttrDto flowLinkAttrDto) {
        return flowLinkIndustryConfigMapper.getFlowLinkIndustryConfigByCondition(flowLinkAttrDto);
    }
}

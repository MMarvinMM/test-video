package com.chinatower.platform.service.impl;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.mapper.IFlowLinkAttrMapper;
import com.chinatower.platform.service.VmFlowLinkAttrRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 14:49
 */
@Slf4j
@Service("vmFlowLinkAttrRepository")
public class VmFlowLinkAttrRepositoryImpl implements VmFlowLinkAttrRepository {

    @Autowired
    private IFlowLinkAttrMapper iFlowLinkAttrMapper;

    @Override
    public VmFlowLinkAttrDto getFlowLinkAttrByCondition(VmFlowLinkAttrDto flowLinkAttrDto) {
        return iFlowLinkAttrMapper.getFlowLinkAttrByCondition(flowLinkAttrDto);
    }

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByUserId(String userId) {
        return iFlowLinkAttrMapper.getFlowLinkByUserId(userId);
    }

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByRoleIdList(Map roleParam) {
        return iFlowLinkAttrMapper.getFlowLinkByRoleIdList(roleParam);
    }
}

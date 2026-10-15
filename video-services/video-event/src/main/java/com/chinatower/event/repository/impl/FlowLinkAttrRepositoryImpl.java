package com.chinatower.event.repository.impl;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.mapper.VmFlowLinkAttrMapper;
import com.chinatower.event.repository.IFlowLinkAttrRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description 告警流程环节属性 Dao层业务实现类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Slf4j
@Service("flowLinkAttrRepository")
public class FlowLinkAttrRepositoryImpl implements IFlowLinkAttrRepository {

    @Autowired
    private VmFlowLinkAttrMapper vmFlowLinkAttrMapper;

    @Override
    public VmFlowLinkAttrDto getFlowLinkAttrByCondition(VmFlowLinkAttrDto flowLinkAttrDto) {
        return vmFlowLinkAttrMapper.getFlowLinkAttrByCondition(flowLinkAttrDto);
    }

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByUserId(String userId) {
        return vmFlowLinkAttrMapper.getFlowLinkByUserId(userId);
    }

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByRoleId(Long  roleId,String appVerCode,String industryCode) {
        return vmFlowLinkAttrMapper.getFlowLinkByRoleId(roleId,appVerCode,industryCode);
    }

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByRoleIdList(Map roleParam) {
        return vmFlowLinkAttrMapper.getFlowLinkByRoleIdList(roleParam);
    }
}

package com.chinatower.platform.repository.impl;



import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.mapper.IVmFlowLinkAttrMapper;
import com.chinatower.platform.repository.IFlowLinkAttrRepository;
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
@Service("IFlowLinkAttrRepository")
public class IFlowLinkAttrRepositoryImpl implements IFlowLinkAttrRepository {

    @Autowired
    private IVmFlowLinkAttrMapper vmFlowLinkAttrMapper;

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByUserId(String userId) {
        return vmFlowLinkAttrMapper.getFlowLinkByUserId(userId);
    }

    @Override
    public List<VmFlowLinkAttrDto> getFlowLinkByRoleIdList(Map roleParam) {
        return vmFlowLinkAttrMapper.getFlowLinkByRoleIdList(roleParam);
    }
}

package com.chinatower.receive.service.impl;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.receive.mapper.IVmFlowLinkIndustryConfigMapper;
import com.chinatower.receive.service.IFlowLinkIndustryConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 告警流程环节行业初始化配置属性 Dao层业务实现类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Slf4j
@Service
public class FlowLinkIndustryConfigRepositoryImpl implements IFlowLinkIndustryConfigRepository {

    @Autowired
    private IVmFlowLinkIndustryConfigMapper IVmFlowLinkIndustryConfigMapper;


    @Override
    public VmFlowLinkAttrDto getFlowLinkIndustryConfigByCondition(VmFlowLinkAttrDto flowLinkAttrDto) {
        return IVmFlowLinkIndustryConfigMapper.getFlowLinkIndustryConfigByCondition(flowLinkAttrDto);
    }
}

package com.chinatower.event.repository.impl;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.mapper.VmFlowLinkIndustryConfigMapper;
import com.chinatower.event.repository.IFlowLinkIndustryConfigRepository;
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
@Service("flowLinkIndustryConfigRepository")
public class FlowLinkIndustryConfigRepositoryImpl implements IFlowLinkIndustryConfigRepository {

    @Autowired
    private VmFlowLinkIndustryConfigMapper vmFlowLinkIndustryConfigMapper;


    @Override
    public VmFlowLinkAttrDto getFlowLinkIndustryConfigByCondition(VmFlowLinkAttrDto flowLinkAttrDto) {
        return vmFlowLinkIndustryConfigMapper.getFlowLinkIndustryConfigByCondition(flowLinkAttrDto);
    }
}

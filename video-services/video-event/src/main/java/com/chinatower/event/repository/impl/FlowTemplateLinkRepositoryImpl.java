package com.chinatower.event.repository.impl;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.dto.VmFlowLinkSearchDto;
import com.chinatower.event.dto.VmFlowTemplateLinkDto;
import com.chinatower.event.mapper.VmFlowTemplateLinkMapper;
import com.chinatower.event.repository.IFlowTemplateLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 告警流程环节 Dao层业务实现类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Slf4j
@Service("flowTemplateLinkRepository")
public class FlowTemplateLinkRepositoryImpl implements IFlowTemplateLinkRepository {
    @Autowired
    private VmFlowTemplateLinkMapper vmFlowTemplateLinkMapper;


    /**
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询开始节点
     * @param flowAlarmTemplateDto
     * @return
     */
    @Override
    public VmFlowTemplateLinkDto getStartFlowLinkByCondition(FlowAlarmTemplateDto flowAlarmTemplateDto) {
        return  vmFlowTemplateLinkMapper.getStartFlowLinkByCondition(flowAlarmTemplateDto);
    }

    @Override
    public VmFlowTemplateLinkDto getFlowLinkByCondition(VmFlowLinkSearchDto flowLinkSearchDto) {
        VmFlowTemplateLinkDto flowLinkByTemplateId = vmFlowTemplateLinkMapper.getFlowLinkByTemplateId(flowLinkSearchDto);
        if(flowLinkByTemplateId != null){
            flowLinkSearchDto.setSignFlag("1");
        }
        return vmFlowTemplateLinkMapper.getFlowLinkByCondition(flowLinkSearchDto);
    }
}

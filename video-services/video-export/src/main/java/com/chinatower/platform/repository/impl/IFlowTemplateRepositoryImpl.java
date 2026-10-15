package com.chinatower.platform.repository.impl;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import com.chinatower.platform.mapper.IVmFlowTemplateMapper;
import com.chinatower.platform.repository.IFlowTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 告警流程模板 Dao层业务实现类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Slf4j
@Service("IFlowTemplateRepository")
public class IFlowTemplateRepositoryImpl implements IFlowTemplateRepository {
    @Autowired
    private IVmFlowTemplateMapper vmFlowTemplateMapper;

    /**
     * 根据传入条件查询模板信息
     *
     * @param param
     * @return
     */
    @Override
    public FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param) {
        return vmFlowTemplateMapper.selectFlowTemplate(param);
    }
}

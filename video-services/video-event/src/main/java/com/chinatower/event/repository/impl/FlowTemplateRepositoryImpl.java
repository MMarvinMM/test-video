package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.FlowOverdueRemind;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import com.chinatower.event.mapper.VmFlowTemplateMapper;
import com.chinatower.event.repository.IFlowTemplateRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 告警流程模板 Dao层业务实现类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Slf4j
@Service("flowTemplateRepository")
public class FlowTemplateRepositoryImpl implements IFlowTemplateRepository {
    @Autowired
    private VmFlowTemplateMapper vmFlowTemplateMapper;

    /**
     * 根据传入条件查询模板信息
     *
     * @param param
     * @return
     */
    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    public FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param) {
        return vmFlowTemplateMapper.selectFlowTemplate(param);
    }

    @Override
    public FlowAlarmTemplateDto selectFlowTemplateByAlarmType(FlowAlarmTemplateDto param) {
        return vmFlowTemplateMapper.selectFlowTemplateByAlarmType(param);
    }

    @Override
    public FlowAlarmTemplateDto selectFlowTemplateByIndustryAndWarningSource(FlowAlarmTemplateDto param) {
        return vmFlowTemplateMapper.selectFlowTemplateByIndustryAndWarningSource(param);
    }

    @Override
    public List<FlowOverdueRemind> getFlowOverdueRemindByAttrId(String attrId) {
        return vmFlowTemplateMapper.getFlowOverdueRemindByAttrId(attrId);
    }

    @Override
    public String getAttrId(String alarmFlowId, String linkId) {
        return vmFlowTemplateMapper.getAttrId(alarmFlowId,linkId);
    }
}

package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dto.*;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.IFlowTemplateIndustryRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Description 告警流程模板关联行业应用 Dao层业务实现类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Slf4j
@Service
public class FlowTemplateIndustryRepositoryImpl implements IFlowTemplateIndustryRepository {
    @Autowired
    private VmFlowTemplateIndustryMapper vmFlowTemplateIndustryMapper;

    /**
     * 根据传入条件查询行业版本初始化配置模板信息
     * @param param
     * @return
     */
    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    public List<VmFlowTemplateIndustryModel> selectFlowTemplateIndustry(VmFlowTemplateIndustryModel param) {
        return vmFlowTemplateIndustryMapper.selectFlowTemplateIndustry(param);
    }
}

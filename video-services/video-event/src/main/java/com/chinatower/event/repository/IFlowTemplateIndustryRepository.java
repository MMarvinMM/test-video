package com.chinatower.event.repository;

import com.chinatower.event.dto.VmFlowTemplateIndustryModel;

import java.util.List;

/**
 * @Description 告警流程模板关联行业应用 Dao层业务类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
public interface IFlowTemplateIndustryRepository {

    /**
     * 根据传入条件查询行业版本初始化配置模板信息
     * @param param
     * @return
     */
    List<VmFlowTemplateIndustryModel> selectFlowTemplateIndustry(VmFlowTemplateIndustryModel param);

}

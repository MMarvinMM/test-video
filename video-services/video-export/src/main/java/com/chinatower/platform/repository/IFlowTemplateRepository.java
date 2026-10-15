package com.chinatower.platform.repository;


import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;

/**
 * @Description 告警流程模板 Dao层业务类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
public interface IFlowTemplateRepository {

    /**
     * 根据传入条件查询行业版本初始化配置模板信息
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param);
}

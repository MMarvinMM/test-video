package com.chinatower.event.repository;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.FlowOverdueRemind;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 根据告警类型查询流程模板关联告警类型表，查询模板信息
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplateByAlarmType(FlowAlarmTemplateDto param);

    /**
     * 根据行业版本和告警来源查询行业初始化时的模板
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplateByIndustryAndWarningSource(FlowAlarmTemplateDto param);

    /**
     * 根据流程环节id获取该环节超期/受理提醒数据
     * @param attrId
     * @return
     */
    List<FlowOverdueRemind> getFlowOverdueRemindByAttrId(String attrId);
    /**
     * 根据流程id和linkId查询流程环节id
     * @param alarmFlowId
     * @param linkId
     * @return
     */
    String getAttrId(String alarmFlowId,String linkId);
}

package com.chinatower.platform.mapper;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.FlowOverdueRemind;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 告警流程模板
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
public interface IFlowTemplateMapper {

    /**
     * 根据传入条件查询行业版本初始化配置模板信息，默认删除标志del_flag = 0
     * 查询条件：tenant_id、app_ver_code、industry_code、flow_type、init_source
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param);

    /**
     * 根据告警类型查询流程模板关联告警类型表，查询模板信息
     * 查询条件：alarm_type、warning_source、tenant_id、app_ver_code、industry_code
     * 根据租户id和告警类型查询租户配置的告警类型关联模板信息
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplateByAlarmType(FlowAlarmTemplateDto param);

    /**
     * 根据行业版本和告警来源查询流程模板关联告警类型表，获取模板信息
     * 查询条件：app_ver_code、industry_code、warning_source
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
    List<FlowOverdueRemind> getFlowOverdueRemindByAttrId(@Param("attrId") String attrId);

    /**
     * 根据流程id和linkId查询流程环节id
     * @param alarmFlowId
     * @param linkId
     * @return
     */
    String getAttrId(@Param("alarmFlowId") String alarmFlowId,@Param("linkId")String linkId);
}

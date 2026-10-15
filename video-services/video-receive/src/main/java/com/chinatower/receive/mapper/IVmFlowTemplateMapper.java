package com.chinatower.receive.mapper;

import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkDto;
import com.chinatower.receive.vo.entity.VmFlowLinkSearchDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 16:04
 */
@Mapper
public interface IVmFlowTemplateMapper {

    /**
     * 根据传入条件查询行业版本初始化配置模板信息，默认删除标志del_flag = 0
     * 查询条件：tenant_id、app_ver_code、industry_code、flow_type、init_source
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param);

    /**
     * 根据行业版本和告警来源查询流程模板关联告警类型表，获取模板信息
     * 查询条件：app_ver_code、industry_code、warning_source
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplateByIndustryAndWarningSource(FlowAlarmTemplateDto param);

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
     * 查询流程环节节点以及节点属性
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询环节节点
     *
     * @param flowLinkSearchDto
     * @return
     */
    VmFlowTemplateLinkDto getFlowLinkByCondition(VmFlowLinkSearchDto flowLinkSearchDto);



    VmFlowTemplateLinkDto getFlowLinkByTemplateId(VmFlowLinkSearchDto flowLinkSearchDto);
}

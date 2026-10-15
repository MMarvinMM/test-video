package com.chinatower.platform.mapper;


import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.platform.entity.IVmFlowLinkSearchDto;
import com.chinatower.platform.entity.IIVmFlowTemplateLinkDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 告警流程模板
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
public interface IVmFlowTemplateLinkMapper {

    /**
     * 查询流程开始节点以及节点属性
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询开始节点
     *
     * @param flowAlarmTemplateDto
     * @return
     */
    IIVmFlowTemplateLinkDto getStartFlowLinkByCondition(FlowAlarmTemplateDto flowAlarmTemplateDto);

    /**
     * 查询流程环节节点以及节点属性
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询环节节点
     *
     * @param flowLinkSearchDto
     * @return
     */
    IIVmFlowTemplateLinkDto getFlowLinkByCondition(IVmFlowLinkSearchDto flowLinkSearchDto);



    IIVmFlowTemplateLinkDto getFlowLinkByTemplateId(IVmFlowLinkSearchDto flowLinkSearchDto);
}

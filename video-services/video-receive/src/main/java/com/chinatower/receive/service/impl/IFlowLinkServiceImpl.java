package com.chinatower.receive.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.receive.mapper.IAlarmFlowConfigMapper;
import com.chinatower.receive.mapper.IVmFlowLinkIndustryConfigMapper;
import com.chinatower.receive.service.IFlowLinkService;
import com.chinatower.receive.service.IFlowLinkAttrRepository;
import com.chinatower.receive.service.IFlowLinkIndustryConfigRepository;
import com.chinatower.receive.service.IFlowTemplateRepository;
import com.chinatower.receive.vo.dto.VmFlowLinkDto;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkDto;
import com.chinatower.receive.vo.entity.VmFlowLinkSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 16:00
 */
@Service
public class IFlowLinkServiceImpl implements IFlowLinkService {

    @Autowired
    private IFlowTemplateRepository flowTemplateRepository;

    @Autowired
    private IFlowLinkAttrRepository flowLinkAttrRepository;

    @Autowired
    private IFlowLinkIndustryConfigRepository flowLinkIndustryConfigRepository;

    @Autowired
    private IAlarmFlowConfigMapper IAlarmFlowConfigMapper;

    @Autowired
    private IVmFlowLinkIndustryConfigMapper flowLinkIndustryConfigMapper;

    @Override
    public FlowAlarmTemplateDto getFlowTemplate(String appVerCode, String industryCode, String tenantId, String warningSource, String alarmType) {
        /**
         *      告警流程模板存在3类情况：
         *          * （1） 系统默认流程模板，绑定告警来源 vm_flow_template表中init_source字段
         *          * （2） 行业初始化绑定流程模板，流程模板与行业建立关系配置告警来源 vm_flow_template_industry表
         *          * （3） 租户自定义流程模板，绑定告警来源和类型 vm_flow_template_alarm_type和vm_alarm_flow_config表中init_source字段
         *
         *       其中租户进行过流程配置，则绑定模板与告警类型的关系，vm_flow_template_alarm_type表
         *      查询先后顺序：（3） -> (2) -> (1)
         */
        /** 通过告警流程配置中告警类型关系查询模板 **/
        FlowAlarmTemplateDto flowAlarmTemplate = getFlowTemplateByAlarmType(appVerCode, industryCode, tenantId, warningSource, alarmType);
        if (flowAlarmTemplate == null) {
            /** 查询行业初始化版本告警来源绑定模板 **/
            FlowAlarmTemplateDto industryFlowTemplate = getDefaultFlowTemplateByIndustryAndWarningSource(appVerCode, industryCode, warningSource);
            if (industryFlowTemplate == null) {
                /** 查询默认流程模板 **/
                FlowAlarmTemplateDto defaultFlowTemplate = getDefaultFlowTemplateByWarningSource(warningSource);
                if (defaultFlowTemplate == null) {
                    return null;
                } else {
                    return defaultFlowTemplate;
                }
            } else {
                industryFlowTemplate.setSignFlag(Constants.ONE);
                return industryFlowTemplate;
            }
        } else {
            return flowAlarmTemplate;
        }
    }

    @Override
    public VmFlowTemplateLinkDto getFlowTemplateLink(Long templateId, Long alarmFlowId, Long realLinkType, Integer linkId, String industryCode, String appVerCode) {
        VmFlowLinkSearchDto flowLinkSearchDto = new VmFlowLinkSearchDto();
        flowLinkSearchDto.setTemplateId(templateId);
        flowLinkSearchDto.setAlarmFlowId(alarmFlowId);
        flowLinkSearchDto.setLinkId(linkId);
        flowLinkSearchDto.setRealLinkType(realLinkType);
        flowLinkSearchDto.setIndustryCode(industryCode);
        flowLinkSearchDto.setAppVerCode(appVerCode);
        VmFlowTemplateLinkDto flowLink = flowTemplateRepository.getFlowLinkByCondition(flowLinkSearchDto);
        return flowLink;
    }

    @Override
    public VmFlowLinkDto getFlowLinkAndConfig(String appVerCode, String industryCode, String tenantId, Long templateId, Long alarmFlowId, Integer linkId) throws Exception {
        VmFlowLinkDto flowLinkDto = new VmFlowLinkDto();
        //查询流程环节节点
        VmFlowTemplateLinkDto flowLink = getFlowTemplateLink(templateId, alarmFlowId, null,linkId,industryCode,appVerCode);
        //查询环节节点流程配置属性，若不存在则查询行业初始化属性配置
        VmFlowLinkAttrDto vmFlowLinkAttrDto = getFlowLinkAttrBycondition(appVerCode, industryCode, tenantId, templateId, alarmFlowId, linkId);

        flowLinkDto.setFlowTemplateLinkDto(flowLink);
        flowLinkDto.setFlowLinkAttrDto(vmFlowLinkAttrDto);

        return flowLinkDto;
    }

    @Override
    public List<FlowModel> getFlowLink(AlarmFlowConfigModel model) {
        //先查告警流程配置信息 关联attr
        List<FlowModel> flowModelList = IAlarmFlowConfigMapper.queryFlowLink(model);
        if(CollectionUtil.isEmpty(flowModelList)){
            flowModelList = flowLinkIndustryConfigMapper.queryFlowLinkByFlowId(model);
        }
        //没有则使用行业版本初始化流程配置节点属性
        if(CollectionUtil.isEmpty(flowModelList)){
            model.setId(model.getTemplateId() != null?model.getTemplateId():model.getId());
            flowModelList = flowLinkIndustryConfigMapper.queryFlowLinkIndustryConfig(model);
            //行业默认流程名字拼接1
            for (FlowModel flowModel:flowModelList) {
                if(Constants.DEFAULT_FLOW.contains(String.valueOf(flowModel.getLinkId())) && !StringUtils.isEmpty(flowModel.getLinkName()) && !flowModel.getLinkName().contains(Constants.ONE)){
                    flowModel.setLinkName(flowModel.getLinkName()+"1");
                }
            }
            if(CollectionUtil.isEmpty(flowModelList)){
                VmFlowTemplateModel flowTemplateModel = new VmFlowTemplateModel();
                flowTemplateModel.setInitSource(model.getWarningSource());
                flowTemplateModel.setFlowType("1");//默认流程
                FlowAlarmTemplateDto flowAlarmTemplateDto = flowTemplateRepository.selectFlowTemplate(flowTemplateModel);
                if(flowAlarmTemplateDto != null){
                    AlarmFlowConfigModel newModel = new AlarmFlowConfigModel();
                    newModel.setLinkId(model.getLinkId());
                    newModel.setLinkList(model.getLinkList());
                    newModel.setId(flowAlarmTemplateDto.getTemplateId());
                    flowModelList = flowLinkIndustryConfigMapper.queryFlowLinkIndustryConfig(newModel);
                    //行业默认流程名字拼接1
                    for (FlowModel flowModel:flowModelList) {
                        if(Constants.DEFAULT_FLOW.contains(String.valueOf(flowModel.getLinkId())) && !StringUtils.isEmpty(flowModel.getLinkName()) && !flowModel.getLinkName().contains(Constants.ONE)){
                            flowModel.setLinkName(flowModel.getLinkName()+"1");
                        }
                    }
                }
            }
        }
        return flowModelList;
    }

    /**
     * 根据模板信息查询节点属性配置
     * 1、首先查询租户配置的告警流程配置中的节点属性配置
     * 2、若租户没有对这个节点进行配置，则查询行业应用初始化配置中对改节点的属性配置
     */
    public VmFlowLinkAttrDto getFlowLinkAttrBycondition(String appVerCode, String industryCode, String tenantId, Long templateId, Long alarmFlowId, Integer linkId) throws Exception {
        if (alarmFlowId != null) {//租户配置了告警流程配置，用户自定义的流程环节查询不到则查询订购之后关联的默认流程环节
            VmFlowLinkAttrDto vmFlowLinkAttrDto = getFlowLinkAttrByAlarmFlowId(tenantId, templateId, alarmFlowId, linkId);
            if(vmFlowLinkAttrDto==null){
                vmFlowLinkAttrDto = getFlowLinkAttrByIndustry(appVerCode, industryCode, linkId);
            }
            return vmFlowLinkAttrDto;
        } else {//查询行业版本初始化流程配置节点属性配置
            return getFlowLinkAttrByIndustry(appVerCode, industryCode, linkId);
        }
    }

    /**
     * 查询行业应用初始化配置中节点的属性配置
     */
    public VmFlowLinkAttrDto getFlowLinkAttrByIndustry(String appVerCode, String industryCode, Integer linkId) {
        VmFlowLinkAttrDto flowLinkAttrDto = new VmFlowLinkAttrDto();
        flowLinkAttrDto.setLinkId(linkId);
        flowLinkAttrDto.setIndustryCode(industryCode);
        flowLinkAttrDto.setAppVerCode(appVerCode);
        flowLinkAttrDto = flowLinkIndustryConfigRepository.getFlowLinkIndustryConfigByCondition(flowLinkAttrDto);
        return flowLinkAttrDto;
    }

    /**
     * 查询租户配置的告警流程配置中的节点属性配置
     */
    public VmFlowLinkAttrDto getFlowLinkAttrByAlarmFlowId(String tenantId, Long templateId, Long alarmFlowId, Integer linkId) {
        VmFlowLinkAttrDto flowLinkAttrDto = new VmFlowLinkAttrDto();
        flowLinkAttrDto.setLinkId(linkId);
        flowLinkAttrDto.setAlarmFlowId(alarmFlowId);
        flowLinkAttrDto.setTemplateId(templateId);
        flowLinkAttrDto.setTenantId(tenantId);
        flowLinkAttrDto = flowLinkAttrRepository.getFlowLinkAttrByCondition(flowLinkAttrDto);
        return flowLinkAttrDto;
    }

    /**
     * 根据租户id、告警类型查询流程模板关联告警类型表，获取流程模板信息
     *
     * @param tenantId
     * @param warningSource
     * @param alarmType
     * @return
     */
    public FlowAlarmTemplateDto getFlowTemplateByAlarmType(String appVerCode, String industryCode, String tenantId, String warningSource, String alarmType) {
        FlowAlarmTemplateDto flowAlarmTemplateDto = new FlowAlarmTemplateDto();
        flowAlarmTemplateDto.setTenantId(tenantId);
        flowAlarmTemplateDto.setWarningSource(warningSource);
        flowAlarmTemplateDto.setAppVerCode(appVerCode);
        flowAlarmTemplateDto.setIndustryCode(industryCode);
        flowAlarmTemplateDto.setAlarmType(alarmType);
        return flowTemplateRepository.selectFlowTemplateByAlarmType(flowAlarmTemplateDto);
    }


    /**
     * 根据行业版本和告警来源查询行业初始化时的模板
     *
     * @param warningSource
     * @return
     */
    private FlowAlarmTemplateDto getDefaultFlowTemplateByIndustryAndWarningSource(String appVerCode, String industryCode, String warningSource) {
        FlowAlarmTemplateDto flowAlarmTemplateDto = new FlowAlarmTemplateDto();
        flowAlarmTemplateDto.setWarningSource(warningSource);
        flowAlarmTemplateDto.setAppVerCode(appVerCode);
        flowAlarmTemplateDto.setIndustryCode(industryCode);
        return flowTemplateRepository.selectFlowTemplateByIndustryAndWarningSource(flowAlarmTemplateDto);
    }

    /**
     * 根据告警来源查询默认流程
     *
     * @param warningSource
     * @return
     */
    public FlowAlarmTemplateDto getDefaultFlowTemplateByWarningSource(String warningSource) {
        VmFlowTemplateModel flowTemplateModel = new VmFlowTemplateModel();
        flowTemplateModel.setInitSource(warningSource);
        flowTemplateModel.setFlowType("1");//默认流程
        return flowTemplateRepository.selectFlowTemplate(flowTemplateModel);
    }
}

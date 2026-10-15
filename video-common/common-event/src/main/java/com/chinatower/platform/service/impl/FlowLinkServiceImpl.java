package com.chinatower.platform.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.platform.entity.IVmFlowLinkSearchDto;
import com.chinatower.platform.entity.IIVmFlowTemplateLinkDto;
import com.chinatower.platform.mapper.IVmAlarmFlowConfigMapper;
import com.chinatower.platform.mapper.FlowLinkIndustryConfigMapper;
import com.chinatower.platform.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 14:28
 */
@Service
@Slf4j
public class FlowLinkServiceImpl implements FlowLinkService {

    @Autowired
    private VmFlowTemplateLinkRepository vmFlowTemplateLinkRepository;

    @Autowired
    private VmFlowLinkAttrRepository vmFlowLinkAttrRepository;

    @Autowired
    private VmFlowLinkIndustryConfigRepository vmFlowLinkIndustryConfigRepository;

    @Autowired
    private VmFlowTemplateRepository vmFlowTemplateRepository;

    @Autowired
    private IVmAlarmFlowConfigMapper IVmAlarmFlowConfigMapper;

    @Autowired
    private FlowLinkIndustryConfigMapper flowLinkIndustryConfigMapper;

    @Override
    public IIVmFlowTemplateLinkDto getStartFlowLink(String appVerCode, String industryCode, String tenantId, String warningSource, String alarmType) {
        /**
         * 1、首先查询告警流程模板
         *      告警流程模板存在3类情况：
         *          * （1） 系统默认流程模板，绑定告警来源 vm_flow_template表中init_source字段
         *          * （2） 行业初始化绑定流程模板，流程模板与行业建立关系配置告警来源 vm_flow_template_industry表
         *          * （3） 租户自定义流程模板，绑定告警来源 vm_flow_template表中init_source字段
         *
         *          其中租户进行过流程配置，则绑定模板与告警类型的关系，vm_flow_template_alarm_type表
         *      查询先后顺序：（3） -> (2) -> (1)
         * 2、获取流程模板中流程环节的开始节点。获取节点属性时，首先查询租户对模板是否进行过告警流程配置，如果配置过，则节点属性取租户
         *    告警流程配置的属性值；若租户没有对模板进行流程配置，则获取行业版本初始化时流程配置的节点属性
         */
        try {
            /**
             * 首先查询查询模板关联告警类型表，查不到则查租户自定义流程，如果还查不到，则去查默认流程
             */
            return getStartFlowLink(getFlowTemplate(appVerCode, industryCode, tenantId, warningSource, alarmType));
        } catch (Exception e) {
            log.error("========= 查询流程模板开始节点信息失败：传入参数：appVerCode={}, industryCode={},tenantId={}, warningSource={},alarmType={},异常：{} ===========",
                    appVerCode, industryCode, tenantId, warningSource, alarmType, e);
        }

        return new IIVmFlowTemplateLinkDto();
    }

    @Override
    public List<FlowModel> getFlowLink(AlarmFlowConfigModel model) {
        //先查告警流程配置信息 关联attr
        List<FlowModel> flowModelList = IVmAlarmFlowConfigMapper.queryFlowLink(model);
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
                FlowAlarmTemplateDto flowAlarmTemplateDto = vmFlowTemplateRepository.selectFlowTemplate(flowTemplateModel);
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
     * 获取流程开始节点
     *
     * @param flowAlarmTemplateDto
     * @return
     */
    public IIVmFlowTemplateLinkDto getStartFlowLink(FlowAlarmTemplateDto flowAlarmTemplateDto) throws Exception {
        IIVmFlowTemplateLinkDto startFlowLink = vmFlowTemplateLinkRepository.getStartFlowLinkByCondition(flowAlarmTemplateDto);
        //查询环节节点流程配置属性，若不存在则查询行业初始化属性配置
        VmFlowLinkAttrDto vmFlowLinkAttrDto = getFlowLinkAttrBycondition(flowAlarmTemplateDto.getAppVerCode(), flowAlarmTemplateDto.getIndustryCode(),
                flowAlarmTemplateDto.getTenantId(), flowAlarmTemplateDto.getTemplateId(), flowAlarmTemplateDto.getAlarmFlowId(), startFlowLink.getCurLink());
        //赋值属性配置
        if(vmFlowLinkAttrDto != null){
            startFlowLink.setTimeLimit(vmFlowLinkAttrDto.getTimeLimit());
            startFlowLink.setNotifyAdminFlag(vmFlowLinkAttrDto.getNotifyAdminFlag());
            startFlowLink.setDataFilterPermiss(vmFlowLinkAttrDto.getDataFilterPermiss());
            startFlowLink.setEnableAcceptanceMode(vmFlowLinkAttrDto.getEnableAcceptanceMode());
            startFlowLink.setTimeLimitAcceptance(vmFlowLinkAttrDto.getTimeLimitAcceptance());
            //BeanUtil.copyProperties(vmFlowLinkAttrDto, startFlowLink );
            //默认流程时环节名称拼接1
            if(Constants.DEFAULT_FLOW.contains(String.valueOf(startFlowLink.getCurLink())) && !StringUtils.isEmpty(startFlowLink.getLinkName()) && !startFlowLink.getLinkName().contains(Constants.ONE)){
                startFlowLink.setLinkName(startFlowLink.getLinkName()+Constants.ONE);
            }
        }
        return startFlowLink;
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
     * 查询租户配置的告警流程配置中的节点属性配置
     */
    public VmFlowLinkAttrDto getFlowLinkAttrByAlarmFlowId(String tenantId, Long templateId, Long alarmFlowId, Integer linkId) {
        VmFlowLinkAttrDto flowLinkAttrDto = new VmFlowLinkAttrDto();
        flowLinkAttrDto.setLinkId(linkId);
        flowLinkAttrDto.setAlarmFlowId(alarmFlowId);
        flowLinkAttrDto.setTemplateId(templateId);
        flowLinkAttrDto.setTenantId(tenantId);
        flowLinkAttrDto = vmFlowLinkAttrRepository.getFlowLinkAttrByCondition(flowLinkAttrDto);
        return flowLinkAttrDto;
    }

    @Override
    public IIVmFlowTemplateLinkDto getFlowTemplateLink(Long templateId, Long alarmFlowId, Long realLinkType, Integer linkId, String industryCode, String appVerCode) {
        IVmFlowLinkSearchDto flowLinkSearchDto = new IVmFlowLinkSearchDto();
        flowLinkSearchDto.setTemplateId(templateId);
        flowLinkSearchDto.setAlarmFlowId(alarmFlowId);
        flowLinkSearchDto.setLinkId(linkId);
        flowLinkSearchDto.setRealLinkType(realLinkType);
        flowLinkSearchDto.setIndustryCode(industryCode);
        flowLinkSearchDto.setAppVerCode(appVerCode);
        IIVmFlowTemplateLinkDto flowLink = vmFlowTemplateLinkRepository.getFlowLinkByCondition(flowLinkSearchDto);
        return flowLink;
    }


    /**
     * 查询行业应用初始化配置中节点的属性配置
     */
    public VmFlowLinkAttrDto getFlowLinkAttrByIndustry(String appVerCode, String industryCode, Integer linkId) {
        VmFlowLinkAttrDto flowLinkAttrDto = new VmFlowLinkAttrDto();
        flowLinkAttrDto.setLinkId(linkId);
        flowLinkAttrDto.setIndustryCode(industryCode);
        flowLinkAttrDto.setAppVerCode(appVerCode);
        flowLinkAttrDto = vmFlowLinkIndustryConfigRepository.getFlowLinkIndustryConfigByCondition(flowLinkAttrDto);
        return flowLinkAttrDto;
    }


    /**
     * 查询行业应用初始化配置中节点的属性配置
     */
    @Override
    public List<FlowOverdueRemind> getFlowOverdueRemindByAttrId(String attrId) {
        if(StringUtils.isEmpty(attrId)){
            return new ArrayList<>();
        }
        return vmFlowTemplateRepository.getFlowOverdueRemindByAttrId(attrId);
    }



    /**
     * 查询告警流程模板
     * 1、首先查询查询模板关联告警类型表，
     * 2、查不到则查租户自定义流程，
     * 3、如果还查不到，则去查默认流程
     *
     * @param appVerCode
     * @param industryCode
     * @param tenantId
     * @param warningSource
     * @param alarmType
     * @return
     */
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
        return vmFlowTemplateRepository.selectFlowTemplateByAlarmType(flowAlarmTemplateDto);
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
        return vmFlowTemplateRepository.selectFlowTemplateByIndustryAndWarningSource(flowAlarmTemplateDto);
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
        return vmFlowTemplateRepository.selectFlowTemplate(flowTemplateModel);
    }
}

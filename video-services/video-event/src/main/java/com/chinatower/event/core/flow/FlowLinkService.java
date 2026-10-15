package com.chinatower.event.core.flow;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.flow.dto.QueryLinkDto;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.mapper.AlarmFlowConfigMapper;
import com.chinatower.event.mapper.VmFlowLinkIndustryConfigMapper;
import com.chinatower.event.repository.*;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 告警流程配置相关查询，状态流转功能service
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Component("flowLinkService")
@Slf4j
public class FlowLinkService {

    @Autowired
    private IFlowTemplateRepository flowTemplateRepository;
    @Autowired
    private IFlowTemplateLinkRepository flowTemplateLinkRepository;
    @Autowired
    private IFlowLinkAttrRepository flowLinkAttrRepository;
    @Autowired
    private IFlowLinkIndustryConfigRepository flowLinkIndustryConfigRepository;

    @Autowired
    private FlowLinkActionExcute flowLinkActionExcute;
    @Autowired
    private AlarmFlowConfigMapper alarmFlowConfigMapper;
    @Autowired
    private VmFlowLinkIndustryConfigMapper flowLinkIndustryConfigMapper;
    @Autowired
    private ISysService sysService;

    /**
     * 根据行业编码、行业应用版本、租户、告警来源、告警类型等条件获取当前节点信息
     *
     * @param appVerCode    行业应用版本
     * @param industryCode  行业编码
     * @param tenantId      租户
     * @param warningSource 告警来源
     * @param alarmType     告警类型
     * @return
     */
    public VmFlowTemplateLinkDto getStartFlowLink(String appVerCode, String industryCode, String tenantId, String warningSource, String alarmType) {
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

        return new VmFlowTemplateLinkDto();
    }

    /** 批量获取
     * 根据行业编码、行业应用版本、租户、告警来源、告警类型等条件获取当前节点信息
     *
     * @param queryLinkDtoList
     * @return
     */
//    public List<VmFlowTemplateLinkDto> getBatchStartFlowLink(List<QueryLinkDto> queryLinkDtoList) {
//        try {
//            /**
//             * 首先查询查询模板关联告警类型表，查不到则查租户自定义流程，如果还查不到，则去查默认流程
//             */
//            return batchStartFlowLink(batchFlowTemplate(queryLinkDtoList));
//        } catch (Exception e) {
//            log.error("========= 查询流程模板开始节点信息失败：传入参数：queryLinkDtoList={},异常：{} ===========",
//                    queryLinkDtoList, e);
//        }
//        return null;
//    }

    /**
     * 批量查询流程模板信息
     */
    private List<FlowAlarmTemplateDto> batchFlowTemplate(List<QueryLinkDto> queryLinkDtoList) {

        return null;
    }
    /**
     * 批量查询流程开始环节信息
     */
    private List<VmFlowTemplateLinkDto> batchStartFlowLink(List<FlowAlarmTemplateDto> flowAlarmTemplateDtoList) {

        return null;
    }

    /**
     * 获取流程开始节点
     *
     * @param flowAlarmTemplateDto
     * @return
     */
    public VmFlowTemplateLinkDto getStartFlowLink(FlowAlarmTemplateDto flowAlarmTemplateDto) throws Exception {
        VmFlowTemplateLinkDto startFlowLink = flowTemplateLinkRepository.getStartFlowLinkByCondition(flowAlarmTemplateDto);
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
     * 获取流程环节节点以及节点配置属性信息，并按照属性配置进行处理操作
     *
     * @param appVerCode   应用编码
     * @param industryCode 行业编码
     * @param tenantId     租户id
     * @param templateId   模板id
     * @param alarmFlowId  流程配置id
     * @param linkId       环节id
     * @return
     */
    public void getFlowLinkAndExcute(String appVerCode, String industryCode, String tenantId, Long templateId, Long alarmFlowId, Integer linkId) throws Exception {
        //获取流程环节节点以及节点配置属性信息
        VmFlowLinkDto flowLinkDto = getFlowLinkAndConfig(appVerCode, industryCode, tenantId, templateId, alarmFlowId, linkId);
        //执行节点配置动作
        flowLinkActionExcute.jdugeFlowLink(flowLinkDto.getFlowTemplateLinkDto().getLinkType()).doAction(flowLinkDto.getFlowLinkAttrDto());
    }

    /**
     * 获取流程环节节点以及节点配置属性信息
     *
     * @param appVerCode
     * @param industryCode
     * @param tenantId
     * @param templateId
     * @param alarmFlowId
     * @param linkId
     * @return
     */
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

    /**
     * 获取流程环节节点
     * 根据传入参数alarmFlowId判断租户是否配置告警流程，若租户没有配置告警流程，则根据模板id查询节点
     */
    public VmFlowTemplateLinkDto getFlowTemplateLink(Long templateId, Long alarmFlowId, Long realLinkType,Integer linkId,String industryCode,String appVerCode) {
        VmFlowLinkSearchDto flowLinkSearchDto = new VmFlowLinkSearchDto();
        flowLinkSearchDto.setTemplateId(templateId);
        flowLinkSearchDto.setAlarmFlowId(alarmFlowId);
        flowLinkSearchDto.setLinkId(linkId);
        flowLinkSearchDto.setRealLinkType(realLinkType);
        flowLinkSearchDto.setIndustryCode(industryCode);
        flowLinkSearchDto.setAppVerCode(appVerCode);
        VmFlowTemplateLinkDto flowLink = flowTemplateLinkRepository.getFlowLinkByCondition(flowLinkSearchDto);
        return flowLink;
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
        flowLinkAttrDto = flowLinkAttrRepository.getFlowLinkAttrByCondition(flowLinkAttrDto);
        return flowLinkAttrDto;
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
     * 查询行业应用初始化配置中节点的属性配置
     */
    public List<FlowOverdueRemind> getFlowOverdueRemindByAttrId(String attrId) {
        if(StringUtils.isEmpty(attrId)){
            return new ArrayList<>();
        }
        return flowTemplateRepository.getFlowOverdueRemindByAttrId(attrId);
    }

    /**
     * 根据流程id和linkId查询流程环节id
     * @param flowId
     * @param linkId
     * @return
     */
    public String getAttrId(String flowId,String linkId){
        return flowTemplateRepository.getAttrId(flowId,linkId);
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
     * 首先根据租户id、告警来源和告警类型查询模板关联告警类型表，如果查不到则去查默认流程
     *
     * @param tenantId
     * @param warningSource
     * @param alarmType
     * @return
     */
    public FlowAlarmTemplateDto getFlowTemplate(String tenantId, String warningSource, String alarmType) {
        return getFlowTemplate(null,null, tenantId, warningSource, alarmType);
    }


    /**
     * 根据租户id、行业版本信息、告警来源查询租户自定义流程
     *
     * @param appVerCode
     * @param industryCode
     * @param tenantId
     * @param warningSource
     * @return
     */
    public FlowAlarmTemplateDto getTenantFlowTemplateByCondition(String appVerCode, String industryCode, String tenantId, String warningSource) {
        VmFlowTemplateModel flowTemplateModel = new VmFlowTemplateModel();
        flowTemplateModel.setTenantId(tenantId);
        flowTemplateModel.setAppVerCode(appVerCode);
        flowTemplateModel.setIndustryCode(industryCode);
        flowTemplateModel.setInitSource(warningSource);
        flowTemplateModel.setFlowType("0");//租户流程
        return flowTemplateRepository.selectFlowTemplate(flowTemplateModel);
    }

    /**
     * 根据租户id、行业版本信息、告警来源查询租户自定义流程
     *
     * @param tenantId
     * @param warningSource
     * @return
     */
    public FlowAlarmTemplateDto getTenantFlowTemplateByCondition(String tenantId, String warningSource) {
        VmFlowTemplateModel flowTemplateModel = new VmFlowTemplateModel();
        flowTemplateModel.setTenantId(tenantId);
        flowTemplateModel.setInitSource(warningSource);
        flowTemplateModel.setFlowType("0");//租户流程
        return flowTemplateRepository.selectFlowTemplate(flowTemplateModel);
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
    public FlowAlarmTemplateDto getDefaultFlowTemplateByWarningSource(String appVerCode, String industryCode, String warningSource) {
        VmFlowTemplateModel flowTemplateModel = new VmFlowTemplateModel();
        flowTemplateModel.setInitSource(warningSource);
        flowTemplateModel.setAppVerCode(appVerCode);
        flowTemplateModel.setIndustryCode(industryCode);
        flowTemplateModel.setFlowType("1");//默认流程
        return flowTemplateRepository.selectFlowTemplate(flowTemplateModel);
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
     * 根据租户id、告警类型查询流程模板关联告警类型表，获取流程模板信息
     *
     * @param tenantId
     * @param warningSource
     * @param alarmType
     * @return
     */
    public FlowAlarmTemplateDto getFlowTemplateByAlarmType(String tenantId, String warningSource, String alarmType) {
        FlowAlarmTemplateDto flowAlarmTemplateDto = new FlowAlarmTemplateDto();
        flowAlarmTemplateDto.setTenantId(tenantId);
        flowAlarmTemplateDto.setWarningSource(warningSource);
        flowAlarmTemplateDto.setAlarmType(alarmType);
        return flowTemplateRepository.selectFlowTemplateByAlarmType(flowAlarmTemplateDto);
    }
    /**
     * 获取流程环节信息
     * @param model
     * @return
     */
    public List<FlowModel> getFlowLink(AlarmFlowConfigModel model){
        //先查告警流程配置信息 关联attr
        List<FlowModel> flowModelList = alarmFlowConfigMapper.queryFlowLink(model);
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
     * 获取所有下一个流程环节信息
     * @param model
     * @return
     */
    public List<FlowModel> getAllNextFlowLink(AlarmFlowConfigModel model){
        //先查告警流程配置信息
        List<FlowModel> flowModelList = alarmFlowConfigMapper.queryAllNextFlowLink(model);
        if(CollectionUtil.isEmpty(flowModelList)){//没有则使用行业版本初始化流程配置节点属性
            model.setId(model.getTemplateId() != null?model.getTemplateId():model.getId());
            flowModelList = flowLinkIndustryConfigMapper.queryAllNextFlowLinkIndustryConfig(model);
            if(CollectionUtil.isEmpty(flowModelList)){
                AlarmFlowConfigModel newModel = new AlarmFlowConfigModel();
                newModel.setLinkId(model.getLinkId());
                flowModelList = flowLinkIndustryConfigMapper.queryAllNextFlowLinkIndustryConfig(model);
            }
        }
        return flowModelList;
    }

    /**
     * 功能说明：根据用户id和角色id查询流程环节信息
     *
     * @author: zhang.xionghui
     * @param:
     * @return:
     * @date: 2022/8/10 14:21
     * @requestID  需求单号及需求名称
     */
    public String getFlowLinkAttrByUser(SysUser user, List<String> roleIdList){
        String flowLinkIds = "";
        String userId = sysService.getLoginUserId();  //获取当前登录用户id(切换后取对应账号的id)
        user.setUserId(userId);
        //根据用户id查环节信息
        List<VmFlowLinkAttrDto> flowLinkUserList = flowLinkAttrRepository.getFlowLinkByUserId(userId);
        List<VmFlowLinkAttrDto> flowLinkRoleList = new ArrayList<>();
        if(roleIdList!=null && !roleIdList.isEmpty()){
            Map roleParam = new HashMap();
            roleParam.put("roleIdList",roleIdList);
            roleParam.put("tenantId",user.getTenantId());
            roleParam.put("industryCode",user.getIndustryCode());
            roleParam.put("appVerCode",user.getAppVerCode());
            flowLinkRoleList = flowLinkAttrRepository.getFlowLinkByRoleIdList(roleParam);
        }
//        for (Long role:roleIds) {
//            //根据角色id查环节信息
//            List<VmFlowLinkAttrDto> flowLinkList = flowLinkAttrRepository.getFlowLinkByRoleId(role,currentUser.getUser().getAppVerCode(),currentUser.getUser().getIndustryCode());
//            if(!CollectionUtil.isEmpty(flowLinkList)){
//                flowLinkRoleList.addAll(flowLinkList);
//            }
//        }

        //汇总数据
        flowLinkUserList.addAll(flowLinkRoleList);
        if(!CollectionUtil.isEmpty(flowLinkUserList)) {
            //去重处理加拼接处理
            flowLinkIds = flowLinkUserList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getAlarmFlowId() + "-" + o.getLinkId()))), ArrayList::new))
                    .stream().map(item -> item.getAlarmFlowId() + "-" + item.getLinkId()).collect(Collectors.joining(Constants.COMMA));
        }
        return flowLinkIds;
    }
}


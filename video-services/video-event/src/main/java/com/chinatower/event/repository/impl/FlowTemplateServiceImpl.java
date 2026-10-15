package com.chinatower.event.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dataobject.AlarmFlowConfigDO;
import com.chinatower.event.dataobject.FlowLinkUser;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.*;
import com.chinatower.event.util.CommonUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AlarmFlowConfig;
import com.chinatower.event.dataobject.FlowTemplate;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * 流程视图模板接口业务实现
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 14:42
 */
@Slf4j
@Service
public class FlowTemplateServiceImpl implements IFlowTemplateService {
    @Autowired
    private FlowTemplateMapper flowTemplateMapper;
    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;
    @Autowired
    private FlowLinkUserMapper flowLinkUserMapper;
    @Autowired
    private AlarmFlowConfigMapper alarmFlowConfigMapper;
    @Autowired
    private ISysService sysService;
    @Autowired
    private SysMapper sysMapper;
    @Autowired
    private FlowLinkService flowLinkService;
    @Autowired
    private IAlarmFlowDeployService flowDeployService;
    @Autowired
    private FlowLinkConfigMapper flowLinkConfigMapper;
    @Autowired
    private VmAlarmFlowConfigMapper vmAlarmFlowConfigMapper;
    @Autowired
    private SystemFeignClient systemFeignClient;

    @Value("${tower.flow.alarm-default-flow}")
    private String alarmDefaultFlow;

    private static final String SPLITER = ",";

    /**
     * 功能描述：新增流程视图模板、流程环节配置
     * @author lyy
     * @param templateModel
     * @return Result<String>
     * @version v1.5
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addFlowTemplate(FlowTemplateModel templateModel) {
        return insertFlowTemplate(templateModel);

    }
    private Result<String> insertFlowTemplate(FlowTemplateModel templateModel) {
        setUserId(templateModel);
        //根据租户ID、模板名称校验流程模板是否已存在
        if (flowTemplateMapper.countSameFlowTemplate(templateModel) > 0) {
            return Result.fail("已存在相同名称的流程视图模板！");
        }
        //新增流程视图模板表：vm_flow_template
        flowTemplateMapper.addFlowTemplate(templateModel);
        //新增视图模板对应的流程模板环节信息：vm_flow_template_link
        int rows = this.addFlowTemplateLink(templateModel);
        if (rows > 0) {
            return Result.success("流程模板保存成功！");
        }
        return Result.fail("流程模板保存失败！");
    }
    /**
     * 功能描述：新增流程视图模板环节信息
     * @author lyy
     * @param templateModel
     * @return int
     * @date 2022/08/05
     */
    private int addFlowTemplateLink(FlowTemplateModel templateModel) {
        int rows = 0;
        //新增前：根据模板ID先删除该模板已存在的流程环节信息,以及对应的环节信息
        if (templateModel.getFlowId() != 0) {
            flowTemplateMapper.deleteFlowTemplateLink(templateModel.getFlowId());
            flowLinkConfigMapper.deleteFlowLinkConfigByTempId(templateModel.getFlowId());
        }
        if (StringUtils.isNotEmpty(templateModel.getFlowJson())) {
            List<VmFlowTemplateLinkModel> linkModelList = new ArrayList<>();
            VmFlowTemplateLinkModel linkModel;
            //1、将json字符串转为json对象，再分别获取到节点、节点连接关系的json数组
            JSONObject object = JSON.parseObject(templateModel.getFlowJson());
            JSONArray nodeList = object.getJSONArray(Constants.PARAM_NODES);
            JSONArray edgesList = object.getJSONArray("edges");
            //2、循环遍历节点连接关系json数组，获取所有节点的上游节点集合、下游节点集合
            Map<String, String> preLinkMap = new HashMap<>();
            Map<String, String> nextLinkMap = new HashMap<>();
            edgesList.forEach(edges -> {
                JSONObject edgesObj = (JSONObject) edges;
                if(StringUtils.isBlank(edgesObj.getString("source")) ||
                        StringUtils.isBlank(edgesObj.getString("target"))){
                    return;
                }
                String sourceStr = edgesObj.getString("source"); //相当于source为上一节点
                String targetStr = edgesObj.getString("target"); //相当于source为下一节点
                String preLink = preLinkMap.get(targetStr);
                if (StringUtils.isBlank(preLink)) {
                    preLink = sourceStr;
                } else {
                    preLink += "," + sourceStr;
                }
                preLinkMap.put(targetStr, preLink);

                String nextLink = nextLinkMap.get(sourceStr);
                if (StringUtils.isBlank(nextLink)) {
                    nextLink = targetStr;
                } else {
                    nextLink += "," + targetStr;
                }
                nextLinkMap.put(sourceStr, nextLink);
            });
            //3、根据node节点json数组来确定环节属性：1-开始环节、2-中间环节、3-结束环节
            Integer nodeSize = nodeList.size();
            List<FlowLinkConfigModel> linkConfigModels = new ArrayList<>();
            FlowLinkConfigModel linkConfigModel;
            for (int i = 0; i<nodeSize; i++) {
                linkModel = new VmFlowTemplateLinkModel();
                JSONObject nodeObj = JSON.parseObject(nodeList.get(i).toString());
                //获取当前环节的上下节点
                String preLinkId = preLinkMap.get(nodeObj.getString("id"));
                String nextLinkId = nextLinkMap.get(nodeObj.getString("id"));
                String linkType = null;
                //如果上节点为7、下节点不为空，则设置该节点为：1-研判开始环节,兼容开始节点为0-默认无用环节
                if (StringUtils.isBlank(preLinkId)) {
                    linkType = "0";
                }
                if ("7".equals(preLinkId) && StringUtils.isNotBlank(nextLinkId)) {
                    linkType = "1";
                }
                //如果上下节点都不为空，则设置该节点为：2-中间环节
                if (StringUtils.isNotBlank(preLinkId) && StringUtils.isNotBlank(nextLinkId) && !"7".equals(preLinkId)) {
                    linkType = "2";
                }
                //如果上节点不为空、下一节点为空，则设置该节点为：3-结束环节
                if (StringUtils.isNotBlank(preLinkId) && StringUtils.isBlank(nextLinkId)) {
                    linkType = "3";
                }
                linkModel.setTemplateId(templateModel.getFlowId());
                linkModel.setCreator(templateModel.getCreator());
                linkModel.setModifier(templateModel.getModifier());
                linkModel.setCurLink(nodeObj.getInteger("id")); //当前节点
                linkModel.setLinkType(linkType); //环节属性
                linkModel.setPreLink(preLinkId); //上一个环节
                linkModel.setNextLink(nextLinkId); //下一个环节
                linkModel.setRealLinkType(nodeObj.getLong("realLinkType"));
                linkModelList.add(linkModel);
                //4、新增流程环节信息,排除默认环节，新增租户环节
                linkConfigModel = new FlowLinkConfigModel();
                String[] linkIds = new String[]{"1","2","4","5","6","7"};
                if (!Arrays.asList(linkIds).contains(nodeObj.getString("id"))) {
                    linkConfigModel.setLinkId(nodeObj.getLong("id"));
                    linkConfigModel.setLinkName(nodeObj.getString(Constants.PARAM_LABEL));
                    linkConfigModel.setCreator(templateModel.getCreator());
                    linkConfigModel.setAppVerCode(templateModel.getAppVerCode());
                    linkConfigModel.setIndustryCode(templateModel.getIndustryCode());
                    linkConfigModel.setTenantId(templateModel.getTenantId());
                    linkConfigModel.setTemplateId(templateModel.getFlowId());
                    linkConfigModel.setRealLinkType(nodeObj.getLong("realLinkType"));
                    linkConfigModels.add(linkConfigModel);
                }
            }
            //批量新增流程模板环节信息：vm_flow_template_link
            rows = flowTemplateMapper.addFlowTemplateLink(linkModelList);
            //批量新增流程环节信息：vm_flow_link_config
            if (CollectionUtils.isNotEmpty(linkConfigModels)) {
                flowLinkConfigMapper.addFlowLinkConfigs(linkConfigModels);
            }
        }
        return rows;
    }

    /**
     * 功能描述：根据条件查询流程模板
     * @params [model]
     * @return com.chinatower.common.entity.Result<java.lang.Object>
     * @author su.tingning
     * @date 2022/6/13 16:35
     */
    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="tenant_id", industryCode="industry_code")
    public Result<Object> queryFlowTemplate(FlowTemplateModel model) {
        Result<Object> result;
        try {
            // 此流程为默认流程
            if (StringUtils.equals("10843", String.valueOf(model.getFlowId()))) {
                model.setTenantId(null);
            }
            /*if (StringUtils.isEmpty(model.getCreateDateOrder()) && StringUtils.isEmpty(model.getUpdateDateOrder())) {
                // 默认按照创建时间降序
                model.setCreateDateOrder("desc");
            }
            String orderSql = genDateOrderSql(model.getCreateDateOrder(), model.getUpdateDateOrder());
            model.setOrderSql(orderSql);*/
            model.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            if (model.isEnablePage()) {
                Integer pageNum = model.getPageNum();
                Integer pageSize = model.getPageSize();
                if (null == pageNum || pageNum <= 0) {
                    pageNum = 1;
                }
                if (null == pageSize || pageSize <= 0) {
                    pageSize = 15;
                }
                PageMethod.startPage(pageNum, pageSize);
                List<FlowTemplateModel> resultList =  getResultData(model);
                PageInfo<FlowTemplate> pageInfo = new PageInfo(resultList);
                result = Result.data(pageInfo);
            } else {
                List<FlowTemplateModel> resultList = getResultData(model);
                result = Result.data(resultList);
            }
        } catch (Exception e) {
            log.error("queryFlowTemplate-异常:"+e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_VIEW_EXCEPTION);
        }

        return result;
    }

    private List<FlowTemplateModel> getResultData(FlowTemplateModel model) {
        List<FlowTemplateModel> resultList = flowTemplateMapper.queryFlowTemplate(model);
        setName(resultList);
        //详情回显告警流程配置别名
        if (StringUtils.isNotEmpty(model.getAlarmFlowId()) && !Constants.ZERO.equals(model.getAlarmFlowId())){
            VmAlarmFlowConfigModel flowConfigModel = vmAlarmFlowConfigMapper.queryAlarmFlowConfigById(Long.valueOf(model.getAlarmFlowId()));
            setAliesName(resultList,flowConfigModel);
        }
        return resultList;
    }

    private String genDateOrderSql(String createDateOrder, String updateDateOrder) {
        StringBuffer orderSql = new StringBuffer();
        if (StringUtils.isNotEmpty(createDateOrder) && EnumUtil.Order.exist(createDateOrder)) {
            orderSql.append("create_time ").append(createDateOrder);
        } else if (StringUtils.isNotEmpty(updateDateOrder) && EnumUtil.Order.exist(updateDateOrder)) {
            orderSql.append("update_time ").append(updateDateOrder);
        }
        return orderSql.toString();
    }

    /**
     *
     *功能描述： 告警流程配置回显别名
     *
     * @param: templateList
     * @return: List<FlowTemplateModel>
     * @author: yang.Hb
     * @date: 2022-11-18
     * @version:
     */
    private List<FlowTemplateModel> setAliesName(List<FlowTemplateModel> templateList,VmAlarmFlowConfigModel flowConfigModel) {
        if (CollectionUtils.isNotEmpty(templateList)) {
            //1、回填别名,根据告警流程配置ID以及流程模板id获取流程环节属性
            List<VmFlowLinkAttrPageDTO> flowLinkAttrs =  vmAlarmFlowConfigMapper.queryFlowLinkAttrListByIds(flowConfigModel);
            //2、替换flowJson里面的原始环节名称，目的：回显告警流程管理里面的流程别名。逻辑：有别名则替换，无别名则还是原始环节名称
            templateList.forEach(item ->{
                if (item.getTemplateId().equals(flowConfigModel.getTemplateId())) {
                    flowLinkAttrs.forEach(item2 ->{
                        if (item.getTemplateId().equals(item2.getTemplateId())){
                            if(Constants.DEFAULT_FLOW.contains(String.valueOf(item2.getLinkId()))){
                                item2.setLinkName(item2.getLinkName()+"1");
                            }
                            String flowJson = item.getFlowJson();
                            JSONObject objectNew = JSON.parseObject(flowJson);
                            JSONArray nodeListNew = objectNew.getJSONArray(Constants.PARAM_NODES);
                            Integer nodeSize = nodeListNew.size();
                            for (int i = 0; i<nodeSize; i++) {
                                JSONObject nodeObj = JSON.parseObject(nodeListNew.get(i).toString());
                                if (nodeObj.get("id").toString().equals(item2.getLinkId().toString())){
                                    nodeObj.remove(Constants.PARAM_LABEL);
                                    nodeObj.put(Constants.PARAM_LABEL,StringUtils.isNotEmpty(item2.getLinkAlias())?item2.getLinkAlias():item2.getLinkName());
                                    nodeListNew.set(i,nodeObj);
                                }
                            }
                            objectNew.put(Constants.PARAM_NODES,nodeListNew);
                            item.setFlowJson(objectNew.toJSONString());
                        }
                    });
                }
            });
        }
        return templateList;
    }

    /**
     *
     *功能描述： 补充用户、租户名称
     *
     * @param: templateList
     * @return: List<FlowTemplateModel>
     * @author: zheng.yan
     * @date: 2022-06-14
     * @version:
     */
    private List<FlowTemplateModel> setName(List<FlowTemplateModel> templateList) {
        if (CollectionUtils.isNotEmpty(templateList)) {
            List<String> tenantIdList = templateList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getTenantId())).map(FlowTemplateModel::getTenantId).collect(Collectors.toList());
            Set<String> creatorList = templateList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getCreator())).map(FlowTemplateModel::getCreator).collect(Collectors.toSet());
            Set<String> modifierList = templateList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getModifier())).map(FlowTemplateModel::getModifier).collect(Collectors.toSet());
            List<String> userIdList = new LinkedList<>();
            userIdList.addAll(creatorList);
            userIdList.addAll(modifierList);

            Map<String, String> userMap = new HashMap<>(10);
            if (CollectionUtils.isNotEmpty(userIdList)) {
                List<SysUser> userList = alarmFlowConfigMapper.queryUsers(userIdList);
                Map<String, String> resultMap = userList.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName));
                userMap.putAll(resultMap);
            }

            Map<String, String> tenantMap = new HashMap<>(10);
            if (CollectionUtils.isNotEmpty(tenantIdList)) {
                List<SysTenantInfo> tenantInfoList = alarmFlowConfigMapper.queryTenantList(tenantIdList);
                Map<String, String> resultMap = tenantInfoList.stream().collect(Collectors.toMap(SysTenantInfo::getTenantId, SysTenantInfo::getTenantName));
                tenantMap.putAll(resultMap);
            }

            templateList.forEach(template -> {
                if (null != template.getCreator() && userMap.containsKey(template.getCreator())) {
                    template.setCreatorName(userMap.get(template.getCreator()));
                }
                if (null != template.getModifier() && userMap.containsKey(template.getModifier())) {
                    template.setModifierName(userMap.get(template.getModifier()));
                }
                if (null != template.getTenantId() && tenantMap.containsKey(template.getTenantId())) {
                    template.setTenantName(tenantMap.get(template.getTenantId()));
                }
            });
            String localNode="";
            //获取当前节点
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigKey(com.chinatower.event.constants.Constants.PLATFORM_CODE);
            AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
            if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
                if (sysConfigInfo != null) {
                    localNode = sysConfigInfo.getConfigValue();
                }
            }
            //行业应用名称
            for(FlowTemplateModel flowTemplate:templateList){
                if(!StringUtils.isEmpty(flowTemplate.getIndustryCode())){
                    SysIndustryConf sysIndustryConf = sysService.getByIndustryCode(flowTemplate.getIndustryCode(),localNode);
                    if (ObjectUtil.isNotNull(sysIndustryConf)){
                        flowTemplate.setIndustryName(sysIndustryConf.getIndustryName());
                    }
                }
            }
        }
        return templateList;
    }

    /**
     *
     *功能描述： 设置创建、修改人
     *
     * @param: flowTemplateModel
     * @return: FlowTemplateModel
     * @author: zheng.yan
     * @date: 2022-06-14
     * @version:
     */
    private FlowTemplateModel setUserId(FlowTemplateModel flowTemplateModel) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (currentUser != null) {
            String userId = String.valueOf(currentUser.getUserId());
            if (StringUtils.isEmpty(flowTemplateModel.getCreator())) {
                flowTemplateModel.setCreator(userId);
            }
            flowTemplateModel.setModifier(userId);
        }
        return flowTemplateModel;
    }

    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    public Result<FlowTemplateModel> queryFlowAndAttr(FlowTemplateModel model) {
        Result<FlowTemplateModel> result = null;
        Long flowId = model.getFlowId();
        String tenantId = model.getTenantId();
        String warningSource = model.getWarningSource();
        String alarmCode = model.getAlarmCode();
        if (null == flowId || flowId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        if (StringUtils.isEmpty(tenantId)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_TENANT_ID);
        }
        if (StringUtils.isEmpty(warningSource)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "事件来源不能为空");
        }
        if (StringUtils.isEmpty(alarmCode)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "告警编码不能为空");
        }
        try {
            FlowTemplateModel flowTemplateModel = new FlowTemplateModel();
            flowTemplateModel.setFlowId(flowId);
            List<FlowTemplateModel> flowList = flowTemplateMapper.queryFlowTemplate(flowTemplateModel);
            if (CollectionUtils.isEmpty(flowList)) {
                return Result.fail(ResultCode.FAILURE.getCode(), "根据flowId未查询到数据！");
            }
            FlowTemplateModel flowModel = flowList.get(0);
            //vm_flow_template表未保存warningSource

            if (null != flowModel) {
                flowModel.setWarningSource(warningSource);
                //先查询是否有配置的属性，没有的话查询该流程的默认属性
                AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
                alarmFlowConfigModel.setWarningSource(warningSource);
                alarmFlowConfigModel.setAlarmCode(model.getAlarmCode());
                alarmFlowConfigModel.setTenantId(tenantId);
                alarmFlowConfigModel.setFlowId(flowId);
                List<AlarmFlowConfig> alarmFlowConfigList = alarmFlowConfigMapper.queryAlarmFlowConfig(alarmFlowConfigModel);
                if (CollectionUtils.isNotEmpty(alarmFlowConfigList)) {
                    //查到配置的属性
                    Long alarmFlowId = alarmFlowConfigList.get(0).getAlarmFlowId();
                    FlowLinkAttrModel attrModel = new FlowLinkAttrModel();
                    attrModel.setAlarmFlowId(alarmFlowId);
                    attrModel.setAttrFlag("1");
                    List<FlowLinkAttrModel> attrList = flowLinkAttrMapper.queryFlowLinkAttr(attrModel);
                    if (CollectionUtils.isEmpty(attrList)) {
                        //使用默认属性
                        queryDefaultFlowAttr(flowModel);
                    } else {
                        for (FlowLinkAttrModel flowLinkAttrModel : attrList) {
                            //获取用户数据
                            if (flowLinkAttrModel.getUserType().equals("1")) {
                                FlowLinkUserModel userModel = new FlowLinkUserModel();
                                userModel.setFlowLinkId(flowLinkAttrModel.getFlowLinkId());
                                List<FlowLinkUser> userList = flowLinkUserMapper.queryFlowLinkUser(userModel);
                                List<SysInfo> userInfos = new ArrayList<>();
                                if (!CollectionUtils.isEmpty(userList)) {
                                    for (FlowLinkUser flowLinkUser : userList) {
                                        SysInfo sysInfo = new SysInfo();
                                        String userName = sysMapper.queryUserById(flowLinkUser.getUserId());
                                        sysInfo.setDeptId(flowLinkUser.getOrgId());
                                        sysInfo.setUserId(flowLinkUser.getUserId());
                                        sysInfo.setUserName(userName);
                                        userInfos.add(sysInfo);
                                    }
                                    flowLinkAttrModel.setUserInfos(userInfos);
                                }
                            }

                        }
                        flowModel.setAttrList(attrList);
                    }
                } else {
                    //使用默认属性
                    queryDefaultFlowAttr(flowModel);
                }

                result = Result.success("查询成功");
                result.setData(flowModel);
            } else {
                result = Result.fail("没查询到流程");
            }
        } catch (Exception e) {
            log.error(MessageConstants.FLOW_VIEW_EXCEPTION, e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_VIEW_EXCEPTION);
        }

        return result;
    }

    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    public Result<FlowTemplateModel> queryFlowAndAttrNew(FlowTemplateModel model) {
        Result<FlowTemplateModel> result = null;
        Long flowId = model.getFlowId();
        String tenantId = model.getTenantId();
        String warningSource = model.getWarningSource();
        String alarmCode = model.getAlarmCode();
        if (null == flowId || flowId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        if (StringUtils.isEmpty(tenantId)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_TENANT_ID);
        }
        if (StringUtils.isEmpty(warningSource)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "事件来源不能为空");
        }
        if (StringUtils.isEmpty(alarmCode)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "告警编码不能为空");
        }
        try {
            FlowTemplateModel flowTemplateModel = new FlowTemplateModel();
            flowTemplateModel.setFlowId(flowId);
            List<FlowTemplateModel> flowList = flowTemplateMapper.queryFlowTemplate(flowTemplateModel);
            if (CollectionUtils.isEmpty(flowList)) {
                return Result.fail(ResultCode.FAILURE.getCode(), "根据flowId未查询到数据！");
            }
            FlowTemplateModel flowModel = flowList.get(0);
            //vm_flow_template表未保存warningSource

            if (null != flowModel) {
                flowModel.setWarningSource(warningSource);
                AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
                alarmFlowConfigModel.setWarningSource(warningSource);
                alarmFlowConfigModel.setAlarmCode(model.getAlarmCode());
                alarmFlowConfigModel.setTenantId(tenantId);
                alarmFlowConfigModel.setFlowId(flowId);
                List<AlarmFlowConfig> alarmFlowConfigList = alarmFlowConfigMapper.queryAlarmFlowConfig(alarmFlowConfigModel);
                if (CollectionUtils.isNotEmpty(alarmFlowConfigList)) {
                    //查到配置的属性
                    List<FlowLinkAttrModel> attrList = getFlowLinkAttr(alarmFlowConfigList.get(0));
                    if (!CollectionUtils.isEmpty(attrList)) {
                        for (FlowLinkAttrModel flowLinkAttrModel : attrList) {
                            //获取用户数据
                            if (flowLinkAttrModel.getUserType().equals("1")) {
                                FlowLinkUserModel userModel = new FlowLinkUserModel();
                                userModel.setFlowLinkId(flowLinkAttrModel.getFlowLinkId());
                                List<FlowLinkUser> userList = flowLinkUserMapper.queryFlowLinkUser(userModel);
                                List<SysInfo> userInfos = new ArrayList<>();
                                if (!CollectionUtils.isEmpty(userList)) {
                                    for (FlowLinkUser flowLinkUser : userList) {
                                        SysInfo sysInfo = new SysInfo();
                                        String userName = sysMapper.queryUserById(flowLinkUser.getUserId());
                                        sysInfo.setDeptId(flowLinkUser.getOrgId());
                                        sysInfo.setUserId(flowLinkUser.getUserId());
                                        sysInfo.setUserName(userName);
                                        userInfos.add(sysInfo);
                                    }
                                    flowLinkAttrModel.setUserInfos(userInfos);
                                }
                            }

                        }
                        flowModel.setAttrList(attrList);
                    }
                } else {
                    //使用默认属性
                    queryDefaultFlowAttr(flowModel);
                }

                result = Result.success("查询成功");
                result.setData(flowModel);
            } else {
                result = Result.fail("没查询到流程");
            }
        } catch (Exception e) {
            log.error(MessageConstants.FLOW_VIEW_EXCEPTION, e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_VIEW_EXCEPTION);
        }

        return result;
    }

    /**
     * 获取流程环节信息
     * @param model
     * @return
     */
    private List<FlowLinkAttrModel> getFlowLinkAttr(AlarmFlowConfig model){
        AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
        configModel.setId(model.getAlarmFlowId());
        configModel.setTemplateId(model.getFlowId());
        configModel.setAppVerCode(model.getAppVerCode());
        configModel.setIndustryCode(model.getIndustryCode());
        configModel.setWarningSource(model.getWarningSource());
        List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
        return BeanUtil.copyToList(flowModelList,FlowLinkAttrModel.class);
    }

    private void queryDefaultFlowAttr(FlowTemplateModel flowModel) {
        //查询默认流程
        QueryWrapper<AlarmFlowConfigDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", flowModel.getFlowId());
        queryWrapper.eq("warning_source", flowModel.getWarningSource());
        queryWrapper.eq("tenant_id", flowModel.getTenantId());
        queryWrapper.eq("alarm_code", "-1");
        queryWrapper.eq("status", "1");
        AlarmFlowConfigDO alarmFlowConfigDO = alarmFlowConfigMapper.selectOne(queryWrapper);
        //查询默认属性
        if (null != alarmFlowConfigDO) {
            FlowLinkAttrModel flowLinkAttrModel = new FlowLinkAttrModel();
            flowLinkAttrModel.setAlarmFlowId(alarmFlowConfigDO.getAlarmFlowId());
            List<FlowLinkAttrModel> attrList = flowLinkAttrMapper.queryFlowLinkAttr4Temp(flowLinkAttrModel);
            flowModel.setAttrList(attrList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> modifyFlowTemplate(FlowModel model) {
        Long flowId = model.getFlowId();
        if (null == flowId || flowId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        FlowTemplateModel flowTemplateModel = new FlowTemplateModel();
        flowTemplateModel.setFlowId(flowId);
        flowTemplateModel.setFlowName(model.getFlowName());
        flowTemplateModel.setTenantId(model.getTenantId());
        flowTemplateModel.setFlowJson(model.getFlowJson());
        if (flowTemplateMapper.countSameFlowTemplate(flowTemplateModel) > 0) {
            return Result.fail("已存在相同名称的流程模板");
        }
        Result checkResult = checkExistAlarmFlow(flowId);
        if (checkResult.getCode() == ResultCode.FAILURE.getCode()) {
            return checkResult;
        }
        flowTemplateMapper.modifyFlowTemplate(flowTemplateModel);
        return Result.success("修改成功");
    }

    /**
     *
     *功能描述： 根据流程模板id判断是否有在用告警流程配置
     *
     * @param: flowId
     * @return: Result
     * @author: zheng.yan
     * @date: 2022-06-13
     * @version:
     */
    private Result checkExistAlarmFlow(Long flowId){
        // 判断流程模板是否在用
        AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
        alarmFlowConfigModel.setFlowId(flowId);
        alarmFlowConfigModel.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
        List<AlarmFlowConfig> alarmFlowConfigList = alarmFlowConfigMapper.queryAlarmFlowConfig(alarmFlowConfigModel);
        if (CollectionUtils.isNotEmpty(alarmFlowConfigList)) {
            return Result.fail(ResultCode.FAILURE.getCode(), "该流程正在使用中，不能编辑！");
        }
        return Result.success("未找到对应告警流程！");
    }

    /**
     * 功能描述：删除流程视图模板、流程模板环节
     * @author lyy
     * @param flowId
     * @return Result<String>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteFlowTemplate(Long flowId) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            return Result.fail(Constants.NO_MANAGE_PERMISSION);
        }
        if (null == flowId || flowId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result checkResult = checkExistAlarmFlow(flowId);
        if (checkResult.getCode() == ResultCode.FAILURE.getCode()) {
            checkResult.setMsg("该流程模板正在使用中，无法删除！");
            return checkResult;
        }
        Result<Object> flowInUse = flowDeployService.flowInUse(null, flowId,null);
        if (flowInUse!=null && flowInUse.getMsg()!=null && flowInUse.getMsg().equals(Constants.ALARM_FLOW_USE)) {
            return Result.fail("流程中有事件在流转，无法删除！");
        }

        //删除流程视图模板：vm_flow_template
        int row = flowTemplateMapper.deleteFlowTemplate(flowId);
        if (row <= 0) {
            return Result.fail(ResultCode.FAILURE.getCode(), "无法对该流程视图模板进行删除操作！");
        }
        //删除流程模板环节：vm_flow_template_link
        if (flowTemplateMapper.deleteFlowTemplateLink(flowId) > 0) {
            //删除流程模板环节对应租户自定义环节
            flowLinkConfigMapper.deleteFlowLinkConfigByTempId(flowId);
            return Result.success("删除成功！");
        }
        return Result.fail("删除失败！");
    }

    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<Map<String, Long>> genDefaultFlowInfo(FlowModel model) {
        String tenantId = model.getTenantId();
        if (StringUtils.isEmpty(tenantId)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_TENANT_ID);
        }

        Result<Map<String, Long>> result = null;
        result = Result.success("操作成功");
        Map<String, Long> flowIdMap = new HashMap<>(3);

        //判断是否已经存在默认流程,如果存在则不新增
        QueryWrapper<AlarmFlowConfigDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        //默认配置
        queryWrapper.eq("alarm_code", "-1");
        //状态：1生效
        queryWrapper.eq("status", "1");
        queryWrapper.orderByAsc("alarm_flow_id", "create_date");
        List<AlarmFlowConfigDO> alarmFlowConfigDOList = alarmFlowConfigMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(alarmFlowConfigDOList)) {
            //alarmFlowConfigDOList不为空，取最新数据
            alarmFlowConfigDOList.forEach(alarmFlowConfigDO -> {
                flowIdMap.put(tenantId + "_" + alarmFlowConfigDO.getWarningSource(), alarmFlowConfigDO.getAlarmFlowId());
            });
            result.setData(flowIdMap);
            return result;
        }

        // 获取默认流程及属性
        List<FlowTemplateModel> flowTemplates;
        List<FlowLinkAttrModel> flowAttrs;

        if (StringUtils.isEmpty(alarmDefaultFlow)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "默认告警流程配置不能为空");
        }
        List<Long> flowIdList = Arrays.asList(alarmDefaultFlow.split(SPLITER)).stream().map(flowId -> Long.valueOf(flowId)).collect(Collectors.toList());
        flowTemplates = flowTemplateMapper.queryByFlowIds(flowIdList);
        flowAttrs = flowLinkAttrMapper.queryByFlowIds(flowIdList);


        if (CollectionUtils.isNotEmpty(flowTemplates) && CollectionUtils.isNotEmpty(flowAttrs)) {
            Map<Long, List<FlowLinkAttrModel>> flowAttrMap = flowAttrs.stream().collect(Collectors.groupingBy(FlowLinkAttrModel::getFlowId));
            // 获取租户根组织
            SysDept sysDept = sysService.getTenantRootDept(tenantId);
            String orgId = "";
            if (null != sysDept) {
                orgId = sysDept.getDeptId();
            }
            for (FlowTemplateModel flowTemplate : flowTemplates) {
                Long flowId = flowTemplate.getFlowId();
                List<FlowLinkAttrModel> flowLinkAttrModels = flowAttrMap.get(flowId);
                flowTemplate.setTenantId(tenantId);
                flowTemplateMapper.addFlowTemplate(flowTemplate);
                Long dbFlowId = flowTemplate.getFlowId();

                AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
                alarmFlowConfigModel.setFlowId(dbFlowId);
                alarmFlowConfigModel.setTenantId(tenantId);
                //事件来源等于默认流程ID的绝对值
                String warnSource = String.valueOf(flowId).replace("-", "");
                alarmFlowConfigModel.setWarningSource(warnSource);
                alarmFlowConfigModel.setAlarmCode("-1");

                alarmFlowConfigMapper.addAlarmFlowConfig(alarmFlowConfigModel);
                Long alarmFlowId = alarmFlowConfigModel.getId();

                for (FlowLinkAttrModel obj : flowLinkAttrModels) {
                    obj.setFlowId(dbFlowId);
                    obj.setAttrFlag("1");
                    obj.setOrgId(orgId);
                    obj.setAlarmFlowId(alarmFlowId);
                }
                flowLinkAttrMapper.batchAddFlowLinkAttr(flowLinkAttrModels);

                flowIdMap.put(tenantId + "_" + warnSource, alarmFlowId);
            }
            result.setData(flowIdMap);
        } else {
            result = Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "没有查询到默认流程");
        }

        return result;
    }

    /**
     * 功能描述：修改流程视图模板、流程模板环节
     * @author lyy
     * @param templateModel
     * @return Result<String>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> modifyFlowTemplateNew(FlowTemplateModel templateModel) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            return Result.fail(Constants.NO_MANAGE_PERMISSION);
        }
        setUserId(templateModel);
        if (null == templateModel.getFlowId()) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "流程模板ID不能为空");
        }
        //根据租户ID、模板名称校验流程模板是否已存在
        if (flowTemplateMapper.countSameFlowTemplate(templateModel) > 0) {
            return Result.fail("已存在相同名称的流程视图模板！");
        }

        Result checkResult = checkExistAlarmFlow(templateModel.getFlowId());
        if (checkResult.getCode() == ResultCode.FAILURE.getCode()) {
            return checkResult;
        }
        Result<Object> flowInUse = flowDeployService.flowInUse(null, templateModel.getFlowId(),null);
        if (flowInUse!=null && flowInUse.getMsg()!=null && flowInUse.getMsg().equals("流程正在流转中")) {
            return Result.fail("流程中有事件在流转，无法修改！");
        }
        //修改流程视图模板：vm_flow_template,对没有在告警流程配置中使用的视图模板进行先删后增
        //删除流程视图模板：vm_flow_template
        int row = flowTemplateMapper.deleteFlowTemplate(templateModel.getFlowId());
        if (row <=0 ) {
            return Result.fail(ResultCode.FAILURE.getCode(), "无法对该流程视图模板进行修改操作！");
        }
        //删除流程模板环节：vm_flow_template_link
        flowTemplateMapper.deleteFlowTemplateLink(templateModel.getFlowId());
        //删除模板定义的流程环节，vm_flow_link_config
        flowLinkConfigMapper.deleteFlowLinkConfigByTempId(templateModel.getFlowId());

        //新增流程视图模板表：vm_flow_template
        flowTemplateMapper.addFlowTemplate(templateModel);
        //新增视图模板对应的流程模板环节信息：vm_flow_template_link
        int rows = this.addFlowTemplateLink(templateModel);
        if (rows <= 0) {
            return Result.fail("修改失败!");
        }
        return Result.success("修改成功!");
    }
}

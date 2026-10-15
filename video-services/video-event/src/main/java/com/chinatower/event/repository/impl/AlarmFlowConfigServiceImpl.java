package com.chinatower.event.repository.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dataobject.AlarmFlowConfigDO;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.dataobject.FlowLinkUser;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.IFlowTemplateLinkRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.UserTypeConstant;
import com.chinatower.event.dataobject.AlarmFlowConfig;
import com.chinatower.event.repository.IAlarmFlowConfigService;
import com.chinatower.event.repository.IAlarmTypeClassService;
import com.chinatower.event.repository.ISysService;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 流程环节用户处理业务接口实现
 * @author chmod
 * @version 1.0
 * @date 2022/2/14 14:10
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.PLATFORM)
public class AlarmFlowConfigServiceImpl implements IAlarmFlowConfigService {
    @Autowired
    private AlarmFlowConfigMapper alarmFlowConfigMapper;
    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;
    @Autowired
    private FlowLinkUserMapper flowLinkUserMapper;
    @Autowired
    private VmFlowLinkIndustryConfigMapper flowLinkIndustryConfigMapper;
    @Autowired
    private IAlarmTypeClassService alarmTypeClassService;
    @Autowired
    private ISysService sysService;
    @Autowired
    private FlowLinkService flowLinkService;
    @Autowired
    private IFlowTemplateLinkRepository flowTemplateLinkRepository;
    @Autowired
    private AlarmTypeLevelMapper alarmTypeLevelMapper;

    private static final String SPLITER = Constants.COMMA;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> addAlarmFlowConfig(List<AlarmFlowConfigModel> list) throws Exception {
        log.info("新增修改流程配置:" + JSON.toJSONString(list));

        Result<String> result = null;
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> flowLinkIdList = new ArrayList<>();
            for (AlarmFlowConfigModel model : list) {
                Long saveFlowId = null;
                Long alarmFlowId = null;
                Long flowId = model.getFlowId();
                if (flowId == null) {
                    throw new Exception(Constants.PROCESS_ID_CANNOT_BE_EMPTY);
                }

                //20220505sunxf
                // 租户+事件来源+ 告警编码, 查vm_alarm_flow_config这表
                AlarmFlowConfigModel queryModel = new AlarmFlowConfigModel();
                queryModel.setTenantId(model.getTenantId());
                queryModel.setAlarmCode(model.getAlarmCode());
                queryModel.setWarningSource(model.getWarningSource());
                //查询生效数据
                queryModel.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
                List<AlarmFlowConfig> alarmFlowConfigList = alarmFlowConfigMapper.queryAlarmFlowConfig(queryModel);
                //如果数据时，说明对应告警类型已经配置了流程属性，使用对应的alarmFlowId进行处理
                if (CollectionUtils.isNotEmpty(alarmFlowConfigList)) {
                    //一个告警类型和事件来源只会有一种流程配置
                    AlarmFlowConfig config = alarmFlowConfigList.get(0);
                    saveFlowId = config.getFlowId();
                    alarmFlowId = config.getAlarmFlowId();
                    //校验传入的id是否正确
                    checkAlarmParam(config, model);
                }

                //校验新增事件必要参数是否传入
                if (null == alarmFlowId || null == saveFlowId || String.valueOf(saveFlowId).equals(String.valueOf(flowId))) {
                    // 新增
                    String msg = checkParam(model);
                    if (msg != null) {
                        throw new Exception(msg);
                    }
                }

                //不存在，直接新增
                if (null == alarmFlowId) {
                    alarmFlowConfigMapper.addAlarmFlowConfig(model);
                }
                //存在数据库中，做修改
                else if(null != saveFlowId && String.valueOf(saveFlowId).equals(String.valueOf(flowId))) {
                    //告警来源和事件来源关联的流程模板相同则说明是修改环节数据，如果不相同，说明关联的流程配置变了则需要将原来的流程配置删除再新增进去，则需要将现在生效中的流程置为失效并且新增一条流程数据
                    flowLinkAttrMapper.deleteByAlarmFlowId(alarmFlowId);
                    if (CollectionUtils.isNotEmpty(flowLinkIdList)) {
                        flowLinkUserMapper.deleteByFlowLinkIds(flowLinkIdList);
                    }
                    //修改
                    alarmFlowConfigMapper.modifyAlarmFlowConfig(model);
                }
                else {
                    //先将里存在的置为失效
                    alarmFlowConfigMapper.updateById(buildFlowConfigDO(alarmFlowId));
                    model.setId(null);
                    alarmFlowConfigMapper.addAlarmFlowConfig(model);
                }

                // 批量添加流程环节属性和流程环节用户
                addFlowItems(model, flowId);
            }
            result = Result.success("操作成功");
        }
        else {
            result = Result.success("操作成功");
        }
        return result;
    }

    @Override
    public Result<List<AlarmFlowConfig>> queryAlarmFlowConfig(AlarmFlowConfigModel model) {
        Result<List<AlarmFlowConfig>> result = null;
        try {
            List<AlarmFlowConfig> resultList = alarmFlowConfigMapper.queryAlarmFlowConfig(model);
            result = Result.success(Constants.QUERY_WAS_SUCCESSFUL);
            result.setData(resultList);
        } catch (Exception e) {
            log.error(Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS, e);
            result = Result.fail(ResultCode.ERROR.getCode(), Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS);
        }

        return result;
    }

    @Override
    public Result<List<AlarmFlowConfig>> getAlarmFlowConfig(String tenantId, String alarmCode, List<String> warningSourceList) {
        Result<List<AlarmFlowConfig>> result = null;
        try {
            String msg = checkParam(tenantId, alarmCode);
            if (StringUtils.isNotEmpty(msg)) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
            }
            if (CollectionUtils.isEmpty(warningSourceList)) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.EVENT_SOURCE_CANNOT_BE_EMPTY);
            }
            List<AlarmFlowConfig> resultList = alarmFlowConfigMapper.getAlarmFlowConfig(tenantId, alarmCode, warningSourceList);
            //判断数据量是否完整
            List<String> sourceList = resultList.stream().map(AlarmFlowConfig::getWarningSource).collect(Collectors.toList());
            warningSourceList.removeAll(sourceList);
            //缺少的使用默认数据补充
            if (CollectionUtils.isNotEmpty(warningSourceList)) {
                for (String warningSource : warningSourceList) {
                    List<String> simpleWarningSourceList = new ArrayList<>();
                    simpleWarningSourceList.add(warningSource);
                    List<AlarmFlowConfig> defaultList = alarmFlowConfigMapper.getAlarmFlowConfig(tenantId, "-1", simpleWarningSourceList);
                    resultList.add(defaultList.get(0));
                }
            }

            for (AlarmFlowConfig config : resultList) {
                String orgId = config.getOrgId();
                if (StringUtils.isNotEmpty(orgId)) {
                    List<SysDept> deptList = sysService.queryDepts(Arrays.asList(orgId.split(SPLITER)));
                    if (CollectionUtils.isNotEmpty(deptList)) {
                        List<String> deptIdList = deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
                        List<String> deptNameList = deptList.stream().map(SysDept::getDeptName).collect(Collectors.toList());
                        config.setOrgId(String.join(Constants.COMMA, deptIdList));
                        config.setOrgName(String.join(Constants.COMMA, deptNameList));
                    }
                }
            }
            result = Result.success(Constants.QUERY_WAS_SUCCESSFUL);
            result.setData(resultList);
        } catch (Exception e) {
            log.error(Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS, e);
            result = Result.fail(ResultCode.ERROR.getCode(), Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS);
        }

        return result;
    }

    @Override
    public Result<List<AlarmFlowConfig>> getAlarmFlowConfigNew(String tenantId, String alarmCode, List<String> warningSourceList) {
        Result<List<AlarmFlowConfig>> result = null;
        try {
            String msg = checkParam(tenantId, alarmCode);
            if (StringUtils.isNotEmpty(msg)) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
            }
            if (CollectionUtils.isEmpty(warningSourceList)) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.EVENT_SOURCE_CANNOT_BE_EMPTY);
            }
            List<AlarmFlowConfig> resultList = alarmFlowConfigMapper.getAlarmFlowConfigNew(tenantId, alarmCode, warningSourceList);
            for (AlarmFlowConfig config : resultList) {
                String orgId = config.getOrgId();
                if (StringUtils.isNotEmpty(orgId)) {
                    List<SysDept> deptList = sysService.queryDepts(Arrays.asList(orgId.split(SPLITER)));
                    if (CollectionUtils.isNotEmpty(deptList)) {
                        List<String> deptIdList = deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
                        List<String> deptNameList = deptList.stream().map(SysDept::getDeptName).collect(Collectors.toList());
                        config.setOrgId(String.join(Constants.COMMA, deptIdList));
                        config.setOrgName(String.join(Constants.COMMA, deptNameList));
                    }
                }
            }
            result = Result.success(Constants.QUERY_WAS_SUCCESSFUL);
            result.setData(resultList);
        } catch (Exception e) {
            log.error(Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS, e);
            result = Result.fail(ResultCode.ERROR.getCode(), Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS);
        }

        return result;
    }

    @Override
    public Result<String> deleteAlarmFlowConfig(Long alarmFlowId) {
        if (alarmFlowId == null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result<String> result = null;
        try {
            alarmFlowConfigMapper.deleteAlarmFlowConfig(alarmFlowId);
            result = Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "删除告警流程配置异常");
        }
        return result;
    }

    /**
     * 功能描述：根据条件查询流程开始环节
     * @params [model]
     * @return com.chinatower.common.entity.Result<com.chinatower.event.dto.FlowModel>
     * @author su.tingning
     * @date 2022/6/10 16:21
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<FlowModel> queryFlowStartLink(AlarmFlowConfigModel model) {
        String msg = checkParam(model);
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        String warningSource = model.getWarningSource();
        if (!Constants.WARNING_SOURCE_1.equals(warningSource) && !Constants.WARNING_SOURCE_2.equals(warningSource) && !Constants.WARNING_SOURCE_3.equals(warningSource)) {
            model.setWarningSource(WarningSourceEnum.getFlowType(warningSource));
        }
        Result<FlowModel> result = null;
        try {
            model.setAppVerCode("");
            List<FlowModel> flowModelList = alarmFlowConfigMapper.queryFlowStartLink(model);
            if (CollectionUtils.isNotEmpty(flowModelList)) {
                result = getFlowLinkUser(flowModelList.get(0));
            } else {
                //根据事件来源、租户 查对应的流程
                model.setAlarmCode("");
                flowModelList = alarmFlowConfigMapper.queryFlowStartLink(model);
                if (CollectionUtils.isNotEmpty(flowModelList)) {
                    result = getFlowLinkUser(flowModelList.get(0));
                } else {
                    //根据租户ID、事件来源，alarmCode=-1查询默认流程
                    model.setAlarmCode("-1");
                    flowModelList = alarmFlowConfigMapper.queryFlowStartLink(model);
                    if (CollectionUtils.isNotEmpty(flowModelList)) {
                        result = getFlowLinkUser(flowModelList.get(0));
                    } else {
                        result = Result.fail("没有查询到流程起始环节");
                    }
                }
            }
        } catch (Exception e) {
            log.error(MessageConstants.FLOW_LINK_EXCEPTION, e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_LINK_EXCEPTION);
        }
        return result;
    }

    /**
     * 功能描述：根据条件查询流程开始环节
     * @params [model]
     * @return com.chinatower.common.entity.Result<com.chinatower.event.dto.FlowModel>
     * @author su.tingning
     * @date 2022/6/10 16:21
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<FlowModel> queryFlowStartLinkNew(AlarmFlowConfigModel model) {
        String msg = checkParam(model);
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        Result<FlowModel> result = null;
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser.getUser();
            //首先查询租户对模板是否进行过告警流程配置，如果配置过，则节点属性取租户告警流程配置的属性值；若租户没有对模板进行流程配置，则获取行业版本初始化时流程配置的节点属性
            FlowAlarmTemplateDto flowTemplate = flowLinkService.getFlowTemplate(currentUser.getAppVerCode(),currentUser.getIndustryCode(),model.getTenantId(), model.getWarningSource(), model.getAlarmCode());
            if (flowTemplate != null) {
                VmFlowTemplateLinkDto startFlowLink = flowTemplateLinkRepository.getStartFlowLinkByCondition(flowTemplate);
                if(startFlowLink != null){
                    model.setId(flowTemplate.getAlarmFlowId());
                    model.setAppVerCode(flowTemplate.getAppVerCode());
                    model.setIndustryCode(flowTemplate.getIndustryCode());
                    model.setTemplateId(flowTemplate.getTemplateId());
                    model.setLinkId(Long.valueOf(startFlowLink.getCurLink()));
                    List<FlowModel> flowLink = flowLinkService.getFlowLink(model);
                    result = getFlowLinkUser(flowLink.get(0));
                }else{
                    result = Result.fail("没有查询到流程开始环节");
                }
            }else{
                result = Result.fail("没有查询到流程模板");
            }
        } catch (Exception e) {
            log.error(MessageConstants.FLOW_LINK_EXCEPTION, e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_LINK_EXCEPTION);
        }
        return result;
    }

    private Result<FlowModel> getFlowLinkUser(FlowModel flowModel) {
        Long flowLinkId = flowModel.getFlowLinkId();
        FlowLinkUserModel flowLinkUserModel = new FlowLinkUserModel();
        flowLinkUserModel.setFlowLinkId(flowLinkId);
        List<FlowLinkUser> flowLinkUserList = flowLinkUserMapper.queryFlowLinkUser(flowLinkUserModel);
        flowModel.setLinkUserList(flowLinkUserList);
        Result<FlowModel> result = Result.success(Constants.QUERY_WAS_SUCCESSFUL);
        result.setData(flowModel);
        return result;
    }

    @Override
    public Result<List<FlowModel>> queryFlowLink(AlarmFlowConfigModel model) {
        //接口调用时传入的flowId  实际是alarmFlowId
        model.setId(model.getFlowId());
        model.setFlowId(null);
        Long alarmFlowId = model.getId();
        Long intercept = model.getIntercept();
        if (alarmFlowId == null && intercept == null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PROCESS_ID_CANNOT_BE_EMPTY);
        }
        Result<List<FlowModel>> result = null;
        try {
            //获取流程环节信息
            if(model.getIndustryCode() == null || model.getAppVerCode() == null){
                LoginUser loginUser = SecurityUtils.getLoginUser();
                SysUser currentUser = loginUser.getUser();
                model.setAppVerCode(currentUser.getAppVerCode());
                model.setIndustryCode(currentUser.getIndustryCode());
            }
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(model);
            //setUserInfo(flowModelList, model);
            //对流程信息进行排序返回
            Collections.sort(flowModelList, new Comparator<FlowModel>() {
                @Override
                public int compare(FlowModel o1, FlowModel o2) {
                    if(StringUtils.isBlank(o1.getPreLink()) || StringUtils.isBlank(o2.getNextLink())){
                        return -1;
                    }
                    else if (StringUtils.isBlank(o2.getPreLink()) || StringUtils.isBlank(o1.getNextLink())) {
                        return 1;
                    }
                    //if (String.valueOf(o1.getLinkId()).equals(o2.getNextLink())) {
                    else if (o2.getNextLink().contains(String.valueOf(o1.getLinkId()))) {
                        return 1;
                    }
                    //else if (String.valueOf(o2.getLinkId()).equals(o1.getNextLink())) {
                    else if (o1.getNextLink().contains(String.valueOf(o2.getLinkId()))) {
                        return -1;
                    }
                    /*else if (StringUtils.isBlank(o1.getPreLink()) || StringUtils.isBlank(o2.getNextLink())) {
                        return -1;
                    }
                    else if (StringUtils.isBlank(o2.getPreLink()) || StringUtils.isBlank(o1.getNextLink())) {
                        return 1;
                    }*/
                    return 0;
                }
            });
            result = Result.success("操作成功");
            result.setData(flowModelList);
        } catch (Exception e) {
            log.error("查询流程环节异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询流程环节异常");
        }
        return result;
    }

    @Override
    public Result<String> batchConfigFlow(FlowConfigModel flowConfigModel) throws Exception {
        AlarmTypeClassDTO alarmTypeClass = flowConfigModel.getAlarmTypeClass();
        if (alarmTypeClass != null && CollectionUtils.isNotEmpty(alarmTypeClass.getTypeList())) {
            alarmTypeClassService.updateType(alarmTypeClass);
        }
        return Result.success("告警类型关联标签成功!");
    }

    /**
     * 设置用户信息
     * @param flowModelList
     */
    private void setUserInfo(List<FlowModel> flowModelList, AlarmFlowConfigModel model) {
        if (StringUtils.isEmpty(model.getTenantId())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null != loginUser && null != loginUser.getUser()) {
                model.setTenantId(loginUser.getUser().getTenantId());
            }
        }
        if (StringUtils.isNotEmpty(model.getTenantId())) {
            String tenantId = model.getTenantId();
            String orgId = model.getOrgId();
            List<String> orgIdList = new LinkedList<>();
            if (StringUtils.isNotEmpty(orgId)) {
                orgIdList.add(orgId);
            }
            flowModelList.forEach(obj -> {
                String userType = obj.getUserType();
                if (UserTypeConstant.ROLE.getType().equals(userType)) {
                    String roleId = obj.getRoleId();
                    if (StringUtils.isNotEmpty(roleId)) {
                        List<String> roleIdList = Arrays.asList(roleId.split(SPLITER));
                        List<SysInfo> userInfos = sysService.queryUserInfoByRoleId(tenantId, roleIdList, orgIdList);
                        if (CollectionUtils.isNotEmpty(userInfos)) {
                            List<String> userIds = userInfos.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                            List<String> userNames = userInfos.stream().map(SysInfo::getUserName).collect(Collectors.toList());
                            List<String> nickNames = userInfos.stream().map(SysInfo::getNickName).collect(Collectors.toList());
                            obj.setUserId(String.join(Constants.COMMA, userIds));
                            obj.setUserName(String.join(Constants.COMMA, userNames));
                            obj.setNickName(String.join(Constants.COMMA, nickNames));
                        }
                    }
                }
                else {
                    String userIdStr = obj.getUserId();
                    if (StringUtils.isNotEmpty(userIdStr)) {
                        List<String> userIdList = Arrays.asList(userIdStr.split(SPLITER));
                        List<SysUser> userList = sysService.queryUsers(userIdList);
                        if (CollectionUtils.isNotEmpty(userList)) {
                            if (StringUtils.isNotEmpty(orgId)) {
                                // 只返回当前组织下的用户
                                userList = userList.stream().filter(sysUser -> orgId.equals(sysUser.getDeptId())).collect(Collectors.toList());
                            }
                            List<String> userIds = userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                            List<String> userNames = userList.stream().map(SysUser::getUserName).collect(Collectors.toList());
                            List<String> nickNames = userList.stream().map(SysUser::getNickName).collect(Collectors.toList());
                            obj.setUserId(String.join(Constants.COMMA, userIds));
                            obj.setUserName(String.join(Constants.COMMA, userNames));
                            obj.setNickName(String.join(Constants.COMMA, nickNames));
                        }
                    }
                }
            });
        }

    }

    private void addFlowItems(AlarmFlowConfigModel model, Long flowId) {
        List<FlowLinkAttrModel> dataList = model.getData();
        if (CollectionUtils.isNotEmpty(dataList)) {

            dataList.stream().forEach(obj -> {
                obj.setFlowId(flowId);
                obj.setAttrFlag("1");
                obj.setAlarmFlowId(model.getId());
                /*String userIdStr = obj.getUserId();*/
                String userType = obj.getUserType();
                List<SysInfo> userInfos = obj.getUserInfos();
                /*if (UserTypeConstant.LEADER.getType().equals(obj.getLeaderFlag())) {
                    userType = "3";
                }*/
                /*if (UserTypeConstant.ROLE.getType().equals(obj.getUserType())) {
                    //根据所选角色查询所属用户ID
                    String roleIdStr = obj.getRoleId();
                    if (StringUtils.isNotEmpty(roleIdStr)) {
                        List<String> roleIdList = Arrays.asList(roleIdStr.split(SPLITER));
                        List<String> orgIdList = Arrays.asList(obj.getOrgId().split(SPLITER));
                        userInfos = alarmFlowConfigMapper.queryUserInfoByRoleId(roleIdList, orgIdList);
                        *//*Set<String> userIdSet = userInfoList.stream().map(map -> map.get("userId")).collect(Collectors.toSet());
                        userIdStr = String.join(Constants.COMMA, userIdSet);*//*
                    }
                }*/
                if (StringUtils.isEmpty(obj.getTimeLimit())) {
                    obj.setTimeLimit("-1");// 设置没有时限限制
                }
                flowLinkAttrMapper.addFlowLinkAttr(obj);
                Long flowLinkId = obj.getFlowLinkId();
                //用户
                if (CollectionUtils.isNotEmpty(userInfos) && userType.equals("1")) {
                    List<FlowLinkUserModel> flowLinkUserModelList = new LinkedList<>();
                    for (SysInfo sysInfo : userInfos) {
                        FlowLinkUserModel flowLinkUserModel = new FlowLinkUserModel();
                        flowLinkUserModel.setFlowLinkId(flowLinkId);
                        flowLinkUserModel.setFlowId(flowId);
                        flowLinkUserModel.setUserId(String.valueOf(sysInfo.getUserId()));
                        //flowLinkUserModel.setUserType(userType);
                        flowLinkUserModel.setOrgId(String.valueOf(sysInfo.getDeptId()));
                        flowLinkUserModelList.add(flowLinkUserModel);
                    }
                    flowLinkUserMapper.batchAddFlowLinkUser(flowLinkUserModelList);
                }
                /*if (StringUtils.isNotEmpty(userIdStr)) {
                    String[] userIdArr = userIdStr.split(SPLITER);
                    for (String userId : userIdArr) {
                        FlowLinkUserModel flowLinkUserModel = new FlowLinkUserModel();
                        flowLinkUserModel.setFlowLinkId(flowLinkId);
                        flowLinkUserModel.setFlowId(flowId);
                        flowLinkUserModel.setUserId(userId);
                        flowLinkUserModel.setUserType(userType);
                        flowLinkUserModelList.add(flowLinkUserModel);
                        //flowLinkUserModel.setOrgId();
                    }
                }*/
            });
        }
    }

    private String checkParam(AlarmFlowConfigModel model) {
        String tenantId = model.getTenantId();
        String warningSource = model.getWarningSource();
        String alarmCode = model.getAlarmCode();
        String msg = null;
        if (StringUtils.isEmpty(warningSource)) {
            msg = Constants.EVENT_SOURCE_CANNOT_BE_EMPTY;
        }
        msg = checkParam(tenantId, alarmCode);
        return msg;
    }
    private String checkParam(String tenantId, String alarmCode) {
        if (StringUtils.isEmpty(alarmCode)) {
            return "告警编码不能为空";
        }
        if (tenantId == null) {
            return "租户ID不能为空";
        }
        return null;
    }

    /**
     * 校验传入的alarmFlowId与库里保存的id是否一致
     * @param config
     * @param model
     * @throws Exception
     */
    private void checkAlarmParam(AlarmFlowConfig config, AlarmFlowConfigModel model) throws Exception {
        if (null == config.getAlarmFlowId()) {
            throw new Exception(Constants.ALARM_FLOW_ID_CANNOT_BE_EMPTY);
        }
        if (!String.valueOf(config.getAlarmFlowId()).equals(String.valueOf(model.getId()))) {
            throw new Exception(Constants.ALARM_FLOW_ID_DOES_NOT_EXIST);
        }
    }

    /**
     * 构建修改配置表实体
     * @param alarmFlowId
     * @return
     */
    private AlarmFlowConfigDO buildFlowConfigDO(Long alarmFlowId) {
        AlarmFlowConfigDO configDO = new AlarmFlowConfigDO();
        configDO.setAlarmFlowId(alarmFlowId);
        configDO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_INVALID);
        configDO.setUpdateDate(new Date());
        return configDO;
    }
}

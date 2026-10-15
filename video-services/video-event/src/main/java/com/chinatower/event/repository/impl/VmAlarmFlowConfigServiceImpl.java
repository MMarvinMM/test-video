package com.chinatower.event.repository.impl;

import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.dataobject.VmAlarmFlowConfigDO;
import com.chinatower.event.dto.AlarmSoundDTO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.event.mapper.VmAlarmFlowConfigMapper;
import com.chinatower.event.repository.IAlarmFlowDeployService;
import com.chinatower.event.repository.IAlarmTypeRepository;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.repository.VmAlarmFlowConfigService;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警流程配置业务接口实现
 * @author lyy
 * @version v1.5
 * @date 2022/7/20
 */
@Slf4j
@Service
public class VmAlarmFlowConfigServiceImpl implements VmAlarmFlowConfigService {
    @Autowired
    private VmAlarmFlowConfigMapper vmAlarmFlowConfigMapper;
    @Autowired
    private ISysService sysService;

    @Autowired
    private IAlarmTypeRepository alarmTypeRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IAlarmFlowDeployService flowDeployService;

    /**
     * 功能描述：查询告警流程配置信息
     * @author lyy
     * @param vmAlarmFlowConfigDTO
     * @return Result<Object>
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="tenant_id", industryCode="industry_code")
    public Result<Object> getAlarmFlowConfigList(VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO) {
        PageInfo<VmAlarmFlowConfigDO> pageInfo;
        try {
            /*//设置排序规则
            if (StringUtils.isEmpty(vmAlarmFlowConfigDTO.getCreateDateOrder()) &&
                    StringUtils.isEmpty(vmAlarmFlowConfigDTO.getUpdateDateOrder())) {
                // 默认按照修改时间降序
                vmAlarmFlowConfigDTO.setCreateDateOrder("desc");
            }
            String orderSql = getDateOrderSql(vmAlarmFlowConfigDTO.getCreateDateOrder(), vmAlarmFlowConfigDTO.getUpdateDateOrder());
            vmAlarmFlowConfigDTO.setOrderSql(orderSql);*/
            //v1.6.00 提供给APP 新增关键字模糊搜索（告警流程名称/创建人姓名）,筛选出符合条件的告警流程配置
            if (!StringUtil.isEmpty(vmAlarmFlowConfigDTO.getSearchKeyword())) {
                vmAlarmFlowConfigDTO.setAlarmFlowName(vmAlarmFlowConfigDTO.getSearchKeyword());
                //根据用户名获取用户id集合
                List<SysUser> userList = vmAlarmFlowConfigMapper.queryUsers(null, vmAlarmFlowConfigDTO.getSearchKeyword());
                List<String> userIdList = userList.stream().filter(sysUser -> StringUtils.isNotEmpty(sysUser.getUserId())).map(SysUser :: getUserId).collect(Collectors.toList());
                vmAlarmFlowConfigDTO.setUserIdList(userIdList);
            }
            //设置分页参数
            Integer pageNum = vmAlarmFlowConfigDTO.getPageNum();
            Integer pageSize = vmAlarmFlowConfigDTO.getPageSize();
            if (null == pageNum || pageNum <= 0) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = 10;
            }
            PageMethod.startPage(pageNum, pageSize);
            vmAlarmFlowConfigDTO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            vmAlarmFlowConfigDTO.setDeleteFlag(Integer.valueOf(Constants.DELETE_FLAG));
            List<VmAlarmFlowConfigDO> resultList = vmAlarmFlowConfigMapper.getAlarmFlowConfigList(vmAlarmFlowConfigDTO);
            setName(resultList);
            pageInfo = new PageInfo(resultList);
        } catch (Exception e) {
            return Result.fail(ResultCode.ERROR.getCode(), Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS);
        }
        return Result.data(pageInfo, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 功能描述：提交校验，根据租户ID、告警来源、告警类型、行业应用版本、行业编码查询
     * @author lyy
     * @param vmAlarmFlowConfigDTO
     * @return Result<Object>
     */
    @Override
    public Result<Object> queryAlarmFlowConfigAndType(VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<Map<String, String>> alarmSourceAndTypeList = vmAlarmFlowConfigDTO.getAlarmSourceAndTypeList();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            throw new CustomException(Constants.NO_MANAGE_PERMISSION);
        }
        if (StringUtils.isEmpty(vmAlarmFlowConfigDTO.getTenantId())) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_TENANT_ID);
        }
        if(CollectionUtils.isEmpty(alarmSourceAndTypeList)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_ALARM_TYPE);
        }
        if(StringUtils.isEmpty(vmAlarmFlowConfigDTO.getAppVerCode())) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_APPVER_CODE);
        }
        if(StringUtils.isEmpty(vmAlarmFlowConfigDTO.getIndustryCode())) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        //拼接入参告警来源+告警类型
        List<String> combinedList = alarmSourceAndTypeList.stream()
                .map(item -> item.get(Constants.PARAM_ALARM_TYPE) + Constants.SEP + item.get(Constants.PARAM_WARNING_SOURCE))
                .collect(Collectors.toList());
        vmAlarmFlowConfigDTO.setSourceAndTypeList(combinedList);
        /**
         * 根据租户ID+告警来源+告警类型
         * 查询表vm_alarm_flow_config + vm_flow_template_alarm_type
         */
        List<VmAlarmFlowConfigDTO> vmAlarmFlowConfigList = vmAlarmFlowConfigMapper.queryAlarmFlowConfigAndType(vmAlarmFlowConfigDTO);
        //判断该告警流程配置告警来源+告警类型是否已有流程
        if (CollectionUtils.isNotEmpty(vmAlarmFlowConfigList)) {
            //如果已有流程，提示用户是否覆盖原流程（如果是，调用新增接口；如果否，页面保持原状）
            return Result.fail(ResultCode.FAILURE.getCode(), Constants.ALARM_FLOW_CONFIG_EXSITS_SOURCEANDTYPE);
        }
        return Result.success(ResultCode.SUCCESS.getCode() + Constants.COMMA +Constants.ALARM_FLOW_CONFIG_NOTEXSITS_SOURCEANDTYPE);
    }

    /**
     * 功能描述：查看详情、编辑回显
     * @author lyy
     * @param flowConfigModel
     * @return Result<Object>
     */
    @Override
    public Result<Object> queryAlarmFlowConfigDetail(VmAlarmFlowConfigModel flowConfigModel) {
        try {
            if (StringUtils.isEmpty(flowConfigModel.getConfigType()) || flowConfigModel.getConfigType().equals("1")) {
                //根据ID获取告警流程配置
                flowConfigModel = vmAlarmFlowConfigMapper.queryAlarmFlowConfigById(flowConfigModel.getId());
                //根据流程配置ID获取流程配置关联的告警来源、告警类型
                List<VmAlarmFlowTemplateTypeDO> alarmFlowTemplateTypeList = vmAlarmFlowConfigMapper.getAlarmTypeByFlowId(flowConfigModel.getId());
                flowConfigModel.setFlowTemplateTypeList(alarmFlowTemplateTypeList);
                //根据流程配置ID获取流程环节属性
                List<VmFlowLinkAttrPageDTO> flowLinkAttrs =  vmAlarmFlowConfigMapper.queryFlowLinkAttrList(flowConfigModel.getId());
                if (CollectionUtils.isNotEmpty(flowLinkAttrs)) {
                    //获取流程环节用户信息
                    List<VmFlowLinkUserDO> flowLinkUserList = vmAlarmFlowConfigMapper.queryFlowLinkUserListNew(flowLinkAttrs);
                    //获取流程环节角色信息
                    List<VmFlowLinkRoleDO> flowLinkRoleList = vmAlarmFlowConfigMapper.queryFlowLinkRoleListNew(flowLinkAttrs);
                    //获取环节处理超期提醒信息
                    List<VmFlowLinkOverdueRemindDO> overdueRemindList = vmAlarmFlowConfigMapper.queryFlowLinkOverdueRemind(flowLinkAttrs);
                    //获取所有用户信息
                    List<SysUser> userInfos = vmAlarmFlowConfigMapper.queryUsers(null, null);

                    for (VmFlowLinkAttrPageDTO attrDTO : flowLinkAttrs) {
                        List<VmFlowLinkUserDO> userList = new ArrayList<>();
                        List<VmFlowLinkRoleDO> roleList = new ArrayList<>();
                        List<VmFlowLinkOverdueRemindDO> remindDOList = new ArrayList<>();
                        for (VmFlowLinkUserDO linkUserDO : flowLinkUserList) {
                            if (linkUserDO.getAttrId().equals(attrDTO.getId())) {
                                userList.add(linkUserDO);
                            }
                        }
                        for (VmFlowLinkRoleDO linkRoleDO : flowLinkRoleList) {
                            if (linkRoleDO.getAttrId().equals(attrDTO.getId())) {
                                roleList.add(linkRoleDO);
                            }
                        }
                        //获取环节处理超期提醒信息
                        for (VmFlowLinkOverdueRemindDO remindDO : overdueRemindList) {
                            if (remindDO.getAttrId().equals(attrDTO.getId())) {
                                //超期提醒时间转换
                                if (remindDO.getTimeLimitRemind() != null && !org.springframework.util.StringUtils.isEmpty(remindDO.getTimeLimitRemindCompany())) {
                                    Integer timeLimitRemind = dealTimeLimit(remindDO.getTimeLimitRemind(),
                                            remindDO.getTimeLimitRemindCompany(), Constants.OPER_TYPE_QRU);
                                    remindDO.setTimeLimitRemind(timeLimitRemind);
                                }
                                remindDOList.add(remindDO);
                            }
                            //app 根据提醒人ID返回提醒人姓名
                            List<String> remindIdList = Arrays.asList(remindDO.getTimeLimitRemindName().split(","));
                            List<Map<String, String>> timeLimitRemindNames = new ArrayList<>();
                            remindIdList.forEach(e -> {
                                userInfos.forEach(item ->{
                                    if (e.equals(item.getUserId())) {
                                        Map<String, String> timeLimitRemindMap = new HashMap<>();
                                        timeLimitRemindMap.put("pid", item.getDeptId()); //对应的是deptId
                                        timeLimitRemindMap.put("remindId", item.getUserId()); //对应的是userId
                                        timeLimitRemindMap.put("remindName", item.getNickName());
                                        timeLimitRemindNames.add(timeLimitRemindMap);
                                    }
                                });
                            });
                            remindDO.setTimeLimitRemindNames(timeLimitRemindNames);
                        }
                        if (CollectionUtils.isNotEmpty(userList)) {
                            attrDTO.setFlowLinkUserList(userList);
                        }
                        if (CollectionUtils.isNotEmpty(roleList)) {
                            attrDTO.setFlowLinkRoleList(roleList);
                        }
                        if (CollectionUtils.isNotEmpty(remindDOList)) {
                            attrDTO.setFlowLinkOverdueRemindList(remindDOList);
                        }
                        if (attrDTO.getTimeLimit() != null && !org.springframework.util.StringUtils.isEmpty(attrDTO.getTimeLimitCompany())){
                            Integer timeLimit = dealTimeLimit(attrDTO.getTimeLimit(),
                                    attrDTO.getTimeLimitCompany(), Constants.OPER_TYPE_QRU);
                            attrDTO.setTimeLimit(timeLimit);
                        }
                        //受理时限转换
                        if (attrDTO.getTimeLimitAcceptance() != null && !org.springframework.util.StringUtils.isEmpty(attrDTO.getAcceptanceCompany())) {
                            Integer timeLimitAccept  = dealTimeLimit(attrDTO.getTimeLimitAcceptance(),
                                    attrDTO.getAcceptanceCompany(), Constants.OPER_TYPE_QRU);
                            attrDTO.setTimeLimitAcceptance(timeLimitAccept);
                        }
                    }
                    flowConfigModel.setFlowLinkAttrList(flowLinkAttrs);
                } else {
                    return Result.fail("未查到流程环节配置信息！");
                }
            } else if (StringUtils.isNotEmpty(flowConfigModel.getConfigType()) && flowConfigModel.getConfigType().equals("0")) {
                Map<String, String> param = new HashMap<>();
                param.put("alarmFlowId", flowConfigModel.getId().toString());
                param.put("appVerCode", flowConfigModel.getAppVerCode());
                param.put("industryCode", flowConfigModel.getIndustryCode());
                //如果流程类型为行业初始化的
                VmAlarmFlowConfigDO vmAlarmFlowConfigDO = vmAlarmFlowConfigMapper.selectIndustryDefaultFlow(flowConfigModel);
                BeanUtils.copyProperties(vmAlarmFlowConfigDO, flowConfigModel);
                // 查询租户已配置的告警类型
                flowConfigModel.setFlowTemplateTypeList(vmAlarmFlowConfigMapper.getTenantConfigAlarmType(flowConfigModel));
                //则根据流程配置ID、行业应用版本、行业编码去查询流程环节配置表vm_flow_link_industry_config默认的环节配置信息

                List<VmFlowLinkAttrPageDTO> linkIndustryConfigList = vmAlarmFlowConfigMapper.getIndustryAttr(param);
                // 处理环节配置里的roleList
                if (CollectionUtils.isNotEmpty(linkIndustryConfigList)) {

                    for (int i = 0; i < linkIndustryConfigList.size(); i++) {
                        List<VmFlowLinkRoleDO> roleList = new ArrayList<>();
                        String roleId = linkIndustryConfigList.get(i).getRoleId();
                        String gridRoleId = linkIndustryConfigList.get(i).getGridSignRole();
                        if (StringUtils.isNotEmpty(roleId)) {
                            roleList.addAll(getRoleIds(roleId,Constants.ZERO));
                        }
                        if (StringUtils.isNotEmpty(gridRoleId)) {
                            roleList.addAll(getRoleIds(gridRoleId,Constants.ONE));
                        }
                        linkIndustryConfigList.get(i).setFlowLinkRoleList(roleList);
                        if (linkIndustryConfigList.get(i).getTimeLimit() != null && !org.springframework.util.StringUtils.isEmpty(linkIndustryConfigList.get(i).getTimeLimitCompany())) {
                            Integer timeLimit = dealTimeLimit(linkIndustryConfigList.get(i).getTimeLimit(),
                                    linkIndustryConfigList.get(i).getTimeLimitCompany(), Constants.OPER_TYPE_QRU);
                            linkIndustryConfigList.get(i).setTimeLimit(timeLimit);
                        }
                    }
                    flowConfigModel.setFlowLinkAttrList(linkIndustryConfigList);
                } else {
                    return Result.fail("未查到流程环节配置信息！");
                }
            }
        } catch (Exception e) {
            log.error("告警流程查询异常：{}",e);
            return Result.fail(ResultCode.FAILURE.getCode(), Constants.ABNORMAL_CONFIGURATION_OF_QUERY_ALARM_PROCESS);
        }
        return Result.data(flowConfigModel, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 获取roleId
     * @param roleId
     * @return
     */
    public List<VmFlowLinkRoleDO> getRoleIds(String roleId,String gridSign){
        List<VmFlowLinkRoleDO> roleListNew = new ArrayList<>();
        if (StringUtils.isNotEmpty(roleId)) {
            String[] split = roleId.split(",");
            for (int j = 0; j < split.length; j++) {
                VmFlowLinkRoleDO role = new VmFlowLinkRoleDO();
                role.setRoleId(split[j]);
                role.setGridSign(gridSign);
                roleListNew.add(role);
            }
        }
        return roleListNew;
    }

    /**
     * 功能描述：获取默认名称“告警流程名称+数字（数字累加）”
     * @author lyy
     * @param configModel
     * @return Result<String>
     */
    @Override
    public Result<String> getAutoConfigName(VmAlarmFlowConfigModel configModel) {
        return Result.data(vmAlarmFlowConfigMapper.getAutoConfigName(configModel));
    }

    /**
     * 功能描述：校验告警流程配置名称是否唯一
     * @author lyy
     * @param configModel
     * @return Result<Object>
     */
    @Override
    public Result<Object> getConfigNameCount(VmAlarmFlowConfigModel configModel) {
        int rows = vmAlarmFlowConfigMapper.getConfigNameCount(configModel);
        if (rows > 0) {
            return Result.data(rows, Constants.ALARM_FLOW_CONFIG_NAME_Y);
        }
        return Result.data(rows);
    }

    /**
     * 功能描述：新增告警流程配置、告警类型、流程环节属性、用户或者角色信息
     * @author lyy
     * @param model
     * @return Result<String>
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> addVmAlarmFlowConfig(VmAlarmFlowConfigModel model) throws Exception {
        return insertAlarmFlowConfig(model);
    }

    public Result<String> insertAlarmFlowConfig(VmAlarmFlowConfigModel model) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || org.springframework.util.StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }
        String userId = null;
        if (StringUtils.isNotEmpty(model.getAlarmFlowName())) {
            /**
             * 新增前：先删除已存在告警类型的告警流程配置信息
             */
            deleteExistedAlarmType(model);
            //设置创建人、修改人属性值
            SysUser currentUser = loginUser.getUser();
            if (currentUser != null) {
                userId = String.valueOf(currentUser.getUserId());
                if (StringUtils.isEmpty(model.getCreator())) {
                    model.setCreator(userId);
                }
                model.setModifier(userId);
            }
            //判断是否是编辑操作，若不是则进行告警配置新增操作
            if (!Constants.OPER_TYPE_MOD.equals(model.getOperType())){
                /**
                 * 新增告警流程配置表
                 */
                Integer alarmFlowConfigRow = vmAlarmFlowConfigMapper.addVmAlarmFlowConfig(model);
                if (alarmFlowConfigRow <= 0) {
                    return Result.fail(Constants.ALARM_FLOW_CONFIG_SAVE_FAIL);
                }
            }else {
                /**
                 * 更新告警流程配置表
                 */
                vmAlarmFlowConfigMapper.modifierAlarmFlowConfigById(model);
            }

            //添加告警类型表
            List<VmAlarmFlowTemplateTypeDO> vmAlarmFlowTemplateTypeList = model.getFlowTemplateTypeList();
            if (CollectionUtils.isNotEmpty(vmAlarmFlowTemplateTypeList)) {
                for (VmAlarmFlowTemplateTypeDO vmAlarmFlowTemplateTypeDO : vmAlarmFlowTemplateTypeList) {
                    vmAlarmFlowTemplateTypeDO.setTemplateId(model.getTemplateId());
                    vmAlarmFlowTemplateTypeDO.setTenantId(model.getTenantId());
                    vmAlarmFlowTemplateTypeDO.setAlarmFlowId(model.getId()); //告警流程配置id
                    vmAlarmFlowTemplateTypeDO.setCreator(userId);
                    vmAlarmFlowTemplateTypeDO.setModifier(userId);
                    vmAlarmFlowTemplateTypeDO.setIndustryCode(model.getIndustryCode());
                    vmAlarmFlowTemplateTypeDO.setAppVerCode(model.getAppVerCode());
                }
                /**
                 * 批量新增流程配置关联的告警类型表
                 */
                vmAlarmFlowConfigMapper.batchAddFlowTemplateType(vmAlarmFlowTemplateTypeList);
            }
            //添加流程环节属性表
            List<VmFlowLinkAttrPageDTO> flowLinkAttrList = model.getFlowLinkAttrList();
            if (CollectionUtils.isNotEmpty(flowLinkAttrList)) {
                // 完结节点
                VmFlowLinkAttrPageDTO end = new VmFlowLinkAttrPageDTO();
                end.setLinkId(6);
                end.setUserType("1");// 表userType字段不可为空
                end.setRealLinkType("6");
                flowLinkAttrList.add(end);
                // 开始节点
                VmFlowLinkAttrPageDTO start = new VmFlowLinkAttrPageDTO();
                start.setLinkId(7);
                start.setUserType("1");
                start.setRealLinkType("7");
                flowLinkAttrList.add(start);
                /**
                 * 批量新增流程环节属性表
                 */
                for (VmFlowLinkAttrPageDTO vmFlowLinkAttrDTO : flowLinkAttrList) {
                    vmFlowLinkAttrDTO.setTemplateId(model.getTemplateId());
                    vmFlowLinkAttrDTO.setTenantId(model.getTenantId());
                    vmFlowLinkAttrDTO.setAlarmFlowId(model.getId()); //告警流程配置id
                    vmFlowLinkAttrDTO.setCreator(userId);
                    vmFlowLinkAttrDTO.setModifier(userId);
                    vmFlowLinkAttrDTO.setAppVerCode(model.getAppVerCode());
                    vmFlowLinkAttrDTO.setIndustryCode(model.getIndustryCode());
                    //兼容告警工单，转换处理单位为秒
                    if (vmFlowLinkAttrDTO.getTimeLimit() != null && !org.springframework.util.StringUtils.isEmpty(vmFlowLinkAttrDTO.getTimeLimitCompany())) {
                        Integer timeLimit = dealTimeLimit(vmFlowLinkAttrDTO.getTimeLimit(),
                                vmFlowLinkAttrDTO.getTimeLimitCompany(), Constants.OPER_TYPE_MOD);
                        vmFlowLinkAttrDTO.setTimeLimit(timeLimit);
                    }
                    if(vmFlowLinkAttrDTO.getTimeLimitAcceptance() != null && !org.springframework.util.StringUtils.isEmpty(vmFlowLinkAttrDTO.getAcceptanceCompany())) {
                       Integer timeLimitAccept  = dealTimeLimit(vmFlowLinkAttrDTO.getTimeLimitAcceptance(),
                               vmFlowLinkAttrDTO.getAcceptanceCompany(), Constants.OPER_TYPE_MOD);
                       vmFlowLinkAttrDTO.setTimeLimitAcceptance(timeLimitAccept);
                    }
                }
                vmAlarmFlowConfigMapper.batchAddFlowLinkAttr(flowLinkAttrList);
                /**
                 * 调用方法：批量新增用户、角色以及环节处理超期提醒配置
                 */
                insertBatchFlowUserOrRole(flowLinkAttrList, userId);
            }
        }
        return Result.success(Constants.ALARM_FLOW_CONFIG_SAVE_SUCCESS);
    }

    /**
     * 根据秒值和单位转换为对应单位的值
     * @param timeLimit
     * @param timeLimitCompany
     * @param operType
     * @return
     */
    private Integer dealTimeLimit(Integer timeLimit, String timeLimitCompany, String operType) {
        Integer resultTime = null;
        try {
            Integer limitCompany = 1;
            // 节处理时限单位, 0: 秒，1：分钟，2：小时，3：天,统一转换为秒存储。
            switch (timeLimitCompany){
                case Constants.ZERO:
                    limitCompany = 1 ;
                    break;
                case Constants.ONE:
                    limitCompany = 60 ;
                    break;
                case Constants.TWO:
                    limitCompany = 3600 ;
                    break;
                case Constants.THREE:
                    limitCompany = 86400 ;
                    break;
                default:
            }
            BigDecimal timeDecimal = Constants.OPER_TYPE_MOD.equals(operType) ? new BigDecimal(timeLimit).multiply(new BigDecimal(limitCompany)):new BigDecimal(timeLimit).divide(new BigDecimal(limitCompany));
            resultTime = Integer.parseInt(timeDecimal.toString());
        }catch (Exception e){
            log.error("告警处理权限转换单位异常：",e);
        }
        return resultTime;
    }

    /**
     * 批量新增用户表、角色表、超期提醒表
     * @author lyy
     * @param attrLists
     * @param userId
     */
    private void insertBatchFlowUserOrRole(List<VmFlowLinkAttrPageDTO> attrLists, String userId) {
        List<VmFlowLinkUserDO> userList = new ArrayList<>();
        List<VmFlowLinkRoleDO> roleList = new ArrayList<>();
        List<VmFlowLinkOverdueRemindDO> overdueRemindList = new ArrayList<>();
        for (VmFlowLinkAttrPageDTO vmFlowLinkAttrDTO : attrLists){
            Long templateId = vmFlowLinkAttrDTO.getTemplateId(); //模板ID
            String tenantId = vmFlowLinkAttrDTO.getTenantId(); //租户ID
            Integer linkId = vmFlowLinkAttrDTO.getLinkId(); //环节ID
            Long attrId = vmFlowLinkAttrDTO.getId(); //流程ID
            //判断所选择的用户类型（1：用户，2：角色,3 网格员）
            List<VmFlowLinkUserDO> flowLinkUserList = vmFlowLinkAttrDTO.getFlowLinkUserList();
            if (CollectionUtils.isNotEmpty(flowLinkUserList)) {
                for (VmFlowLinkUserDO vmFlowLinkUserDO : flowLinkUserList) {
                    vmFlowLinkUserDO.setTemplateId(templateId);
                    vmFlowLinkUserDO.setTenantId(tenantId);
                    vmFlowLinkUserDO.setLinkId(linkId);
                    vmFlowLinkUserDO.setAttrId(attrId);
                    vmFlowLinkUserDO.setCreator(userId);
                    vmFlowLinkUserDO.setModifier(userId);
                    userList.add(vmFlowLinkUserDO);
                }
            }
            List<VmFlowLinkRoleDO> flowLinkRoleList = vmFlowLinkAttrDTO.getFlowLinkRoleList();
            if (CollectionUtils.isNotEmpty(flowLinkRoleList)) {
                for (VmFlowLinkRoleDO vmFlowLinkRoleDO : flowLinkRoleList) {
                    vmFlowLinkRoleDO.setTemplateId(templateId);
                    vmFlowLinkRoleDO.setTenantId(tenantId);
                    vmFlowLinkRoleDO.setLinkId(linkId);
                    vmFlowLinkRoleDO.setAttrId(attrId);
                    vmFlowLinkRoleDO.setCreator(userId);
                    vmFlowLinkRoleDO.setModifier(userId);
                    roleList.add(vmFlowLinkRoleDO);
                }
            }
            //20230320 lyy 设置超期提醒属性
            List<VmFlowLinkOverdueRemindDO> flowLinkOverdueRemindList = vmFlowLinkAttrDTO.getFlowLinkOverdueRemindList();
            if (!CollectionUtils.isEmpty(flowLinkOverdueRemindList)) {
                for (VmFlowLinkOverdueRemindDO overdueRemindDO : flowLinkOverdueRemindList) {
                    overdueRemindDO.setAttrId(attrId);
                    overdueRemindDO.setCreator(userId);
                    overdueRemindDO.setModifier(userId);
                    //超期提醒时间转换为秒
                    if (overdueRemindDO.getTimeLimitRemind() != null && !org.springframework.util.StringUtils.isEmpty(overdueRemindDO.getTimeLimitRemindCompany())) {
                        Integer timeLimitRemind = dealTimeLimit(overdueRemindDO.getTimeLimitRemind(),
                                overdueRemindDO.getTimeLimitRemindCompany(), Constants.OPER_TYPE_MOD);
                        overdueRemindDO.setTimeLimitRemind(timeLimitRemind);
                    }
                    overdueRemindList.add(overdueRemindDO);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(userList)){
            /**
             * 批量新增流程环节用户表
             */
            vmAlarmFlowConfigMapper.batchAddFlowLinkUser(userList);
        }
        if(CollectionUtils.isNotEmpty(roleList)){
            /**
             * 批量新增流程环节用户表
             */
            vmAlarmFlowConfigMapper.batchAddFlowLinkRole(roleList);
        }
        if(CollectionUtils.isNotEmpty(overdueRemindList)){
            /**
             * 批量新增流程环节处理超期提醒表
             */
            vmAlarmFlowConfigMapper.batchAddFlowLinkOverdueRemind(overdueRemindList);
        }
    }

    /**
     * 功能描述：编辑修改告警流程配置
     * @author lyy
     * @param model
     * @return Result<String>
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> updateAlarmFlowConfig(VmAlarmFlowConfigModel model) throws Exception {
        //根据流程配置ID+租户ID 删除告警流程配置、关联告警类型、流程环节、用户、角色以及环节处理超期提醒配置
        delAlarmFlowConfig(model.getId(), model.getTenantId(),Constants.OPER_TYPE_MOD);
        model.setOperType(Constants.OPER_TYPE_MOD);
        Result<String> result= insertAlarmFlowConfig(model);
        if (ResultCode.SUCCESS.equals(result.getCode())){
            result.setMsg(Constants.ALARM_FLOW_CONFIG_MOD_SUCCESS);
        }
        return result;
    }

    /**
     * 功能描述：新增配置时有重复告警类型则删除之前的告警类型配置
     * @author lyy
     * @param model
     * @return int
     */
    @Override
    public int deleteExistedAlarmType(VmAlarmFlowConfigModel model) {
        List<VmAlarmFlowTemplateTypeDO> templateTypeList = model.getFlowTemplateTypeList();
        if (!CollectionUtils.isEmpty(templateTypeList)) {
            List<String> combinedList = templateTypeList.stream()
                    .map(item -> item.getAlarmType() + Constants.SEP + item.getWarningSource())
                    .collect(Collectors.toList());
            model.setSourceAndTypeList(combinedList);
        }
        //根据租户ID、告警类型id查询出对应的告警流程配置id
        List<String> alarmFlowIds = vmAlarmFlowConfigMapper.getConfigIdByAlarmType(model);
        //逻辑删除流程配置对应的告警类型信息：表vm_flow_template_alarm_type
        int i = vmAlarmFlowConfigMapper.deleteAlarmTypes(model);
        if (i > 0) {
            // 查出删除告警类型后没有告警类型的告警流程配置
            List<Long> emptyConfigIds = vmAlarmFlowConfigMapper.selectEmptyConfigIds(model, alarmFlowIds);
            if (emptyConfigIds != null && emptyConfigIds.size() > 0) {
                // 删除或者改config为关闭状态，因为配置中没有可用的告警类型
                deleteFlowConfigs(emptyConfigIds, model.getTenantId(),Constants.OPER_TYPE_DEL);
            }
        }
        return i;
    }

    /**
     * 删除告警流程配置的除config表以外的其他配置
     * 编辑告警流程配置时 对其他配置采用先删后加的操作
     * @author lyy
     * @param alarmFlowIds
     * @param tenantId
     * @return int
     */
    @Override
    public int deleteConfigAnotherInfo(List<Map> alarmFlowIds, String tenantId) {
        Map param = new HashMap();
        param.put(Constants.PARAM_TENANT_ID, tenantId);
        List ids = new ArrayList();
        for (int i = 0; i < alarmFlowIds.size(); i++) {
            ids.add(alarmFlowIds.get(i).get("alarmType"));
        }
        param.put("alarmFlowIds", ids);
        vmAlarmFlowConfigMapper.deleteAlarmTypeByConfigId(param);
        vmAlarmFlowConfigMapper.deleteRoleByConfigId(param);
        vmAlarmFlowConfigMapper.deleteUserByConfigId(param);
        vmAlarmFlowConfigMapper.deleteAttrByConfigId(param);
        return 1;
    }

    /**
     * 功能描述：删除告警流程配置、告警类型、流程环节、环节用户或角色信息
     * @author lyy
     * @param alarmFlowIds
     * @param tenantId
     * @return int
     */
    @Override
    public int deleteFlowConfigs(List<Long> alarmFlowIds, String tenantId,String operType) {
        Map param = new HashMap();
        param.put(Constants.PARAM_TENANT_ID, tenantId);
        param.put("alarmFlowIds", alarmFlowIds);
        int i = 0;
        //逻辑删除告警流程关联的告警类型表：vm_flow_template_alarm_type
        vmAlarmFlowConfigMapper.deleteAlarmTypeByConfigId(param);
        //修改进行物理删除attr,flowUser,flowRole
        if(Constants.OPER_TYPE_MOD.equals(operType)){
            //物理删除流程环节角色表：vm_flow_link_role
            vmAlarmFlowConfigMapper.deleteRealRoleByConfigId(param);
            //物理删除流程环节用户表：vm_flow_link_user
            vmAlarmFlowConfigMapper.deleteRealUserByConfigId(param);
            //20230320 物理删除流程环节超期提醒表：vm_flow_link_overdue_remind
            vmAlarmFlowConfigMapper.deleteRealRemindByConfigId(param);
            //物理删除流程环节表：vm_flow_link_attr
            i = vmAlarmFlowConfigMapper.deleteRealAttrByConfigId(param);
        }else {
            //逻辑删除流程环节角色表：vm_flow_link_role
            vmAlarmFlowConfigMapper.deleteRoleByConfigId(param);
            //逻辑删除流程环节用户表：vm_flow_link_user
            vmAlarmFlowConfigMapper.deleteUserByConfigId(param);
            //20230320 逻辑删除流程环节超期提醒表：vm_flow_link_overdue_remind
            vmAlarmFlowConfigMapper.deleteRemindByConfigId(param);
            //逻辑删除流程环节表：vm_flow_link_attr
            vmAlarmFlowConfigMapper.deleteAttrByConfigId(param);
            //逻辑删除告警流程配置表：vm_alarm_flow_config
            i = vmAlarmFlowConfigMapper.deleteFlowConfig(param);
        }

        return i;
    }

    /**
     * 功能描述：删除告警流程配置
     * @author lyy
     * @param alarmFlowId
     * @param tenantId
     * @return Result<String>
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> deleteFlowConfig(Long alarmFlowId, String tenantId,String operType) {
        return delAlarmFlowConfig(alarmFlowId, tenantId,operType);
    }
    private Result<String> delAlarmFlowConfig(Long alarmFlowId, String tenantId,String operType) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            return Result.fail(ResultCode.ERROR.getCode(), Constants.NO_MANAGE_PERMISSION);
        }
        if (alarmFlowId == null || tenantId == null || tenantId.length() <= 0) {
            return Result.fail("流程id或租户编码不可为空");
        }
        List<Long> alarmFlowIds = new ArrayList<>();
        alarmFlowIds.add(alarmFlowId);
        int i = deleteFlowConfigs(alarmFlowIds, tenantId,operType);
        if (i > 0) {
            return Result.success("告警流程配置删除成功");
        } else {
            return Result.fail("告警流程配置删除失败");
        }
    }

    /**
     * 功能描述：给sql拼接排序规则
     * @author lyy
     * @param createDateOrder
     * @param updateDateOrder
     * @return String
     */
    private String getDateOrderSql(String createDateOrder, String updateDateOrder) {
        StringBuffer orderSql = new StringBuffer();
        if (StringUtils.isNotEmpty(createDateOrder) && EnumUtil.Order.exist(createDateOrder)) {
            orderSql.append("create_time ").append(createDateOrder);
        } else if (StringUtils.isNotEmpty(updateDateOrder) && EnumUtil.Order.exist(updateDateOrder)) {
            orderSql.append("update_time ").append(updateDateOrder);
        }
        return orderSql.toString();
    }

    /**
     * 功能描述：补充创建人、修改人、租户名称
     * @author lyy
     * @param flowConfigList
     * @return List<VmAlarmFlowConfigDO>
     */
    private List<VmAlarmFlowConfigDO> setName(List<VmAlarmFlowConfigDO> flowConfigList) {
        if (CollectionUtils.isNotEmpty(flowConfigList)) {
            List<String> tenantIdList = flowConfigList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getTenantId())).map(VmAlarmFlowConfigDO::getTenantId).collect(Collectors.toList());
            Set<String> creatorList = flowConfigList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getCreator())).map(VmAlarmFlowConfigDO::getCreator).collect(Collectors.toSet());
            Set<String> modifierList = flowConfigList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getModifier())).map(VmAlarmFlowConfigDO::getModifier).collect(Collectors.toSet());
            List<String> userIdList = new LinkedList<>();
            userIdList.addAll(creatorList);
            userIdList.addAll(modifierList);

            Map<String, String> userMap = new HashMap<>(10);
            if (CollectionUtils.isNotEmpty(userIdList)) {
                List<SysUser> userList = vmAlarmFlowConfigMapper.queryUsers(userIdList, null);
                Map<String, String> resultMap = userList.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName));
                userMap.putAll(resultMap);
            }

            Map<String, String> tenantMap = new HashMap<>(10);
            if (CollectionUtils.isNotEmpty(tenantIdList)) {
                List<SysTenantInfo> tenantInfoList = vmAlarmFlowConfigMapper.queryTenantList(tenantIdList);
                Map<String, String> resultMap = tenantInfoList.stream().collect(Collectors.toMap(SysTenantInfo::getTenantId, SysTenantInfo::getTenantName));
                tenantMap.putAll(resultMap);
            }

            flowConfigList.forEach(alarmFlowConfig -> {
                if (null != alarmFlowConfig.getCreator() && userMap.containsKey(alarmFlowConfig.getCreator())) {
                    alarmFlowConfig.setCreatorName(userMap.get(alarmFlowConfig.getCreator()));
                }
                if (null != alarmFlowConfig.getModifier() && userMap.containsKey(alarmFlowConfig.getModifier())) {
                    alarmFlowConfig.setModifierName(userMap.get(alarmFlowConfig.getModifier()));
                }
                if (null != alarmFlowConfig.getTenantId() && tenantMap.containsKey(alarmFlowConfig.getTenantId())) {
                    alarmFlowConfig.setTenantName(tenantMap.get(alarmFlowConfig.getTenantId()));
                }
            });
        }
        return flowConfigList;
    }


    /**
     * 功能描述：
     * @author yhb
     * @return Result<Object>
     */
    @Override
    public Result<Map> selectAlarmSoundOperation(AlarmSoundDTO model) {
        // 获取当前的用户
        Map dataMap = new HashMap();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            throw new CustomException(Constants.NO_MANAGE_PERMISSION);
        }
        if (model.getAlarmSoundId() == null) {
            return Result.fail(ResultCode.ERROR.getCode(), Constants.ALARM_SOUND);
        }
        dataMap.put(Constants.SOUND_OPERATION,Constants.ZERO);
        VmFlowLinkAttrPageDTO vmFlowLinkAttrPageDTO = new VmFlowLinkAttrPageDTO();
        vmFlowLinkAttrPageDTO.setVoiceAlram(String.valueOf(model.getAlarmSoundId()));
        //根据流程配置ID获取流程环节属性,判断是否被使用
        List<VmFlowLinkAttrPageDTO> flowLinkAttrs =  vmAlarmFlowConfigMapper.queryFlowLinkAttrListByVoice(vmFlowLinkAttrPageDTO);
        if (CollectionUtils.isNotEmpty(flowLinkAttrs)) {
            //如果可以查到，则返回1，不能修改
            dataMap.put(Constants.SOUND_OPERATION,Constants.ONE);
        }
        return Result.data(dataMap);
    }

    /**
     * 根据用户名称查询用户信息
     * @param sysUser
     * @return
     */
    @Override
    public List<Map> getUserInfoByName(SysUser sysUser) {
        PageMethod.startPage(sysUser.getPageNum(), sysUser.getPageSize());
        return vmAlarmFlowConfigMapper.getUserInfoByName(sysUser);
    }

    /**
     * 查询所有的告警来源（含第三方告警来源）
     * @return
     */
    @Override
    public Result<List<SysDictDataResponse>> findAlarmSourceAll() {
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        //获取告警来源枚举值
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON);
        List<SysDictDataResponse> dictList = res.getData();
        //过滤掉字典配置中的‘第三方告警’字典值
        dictList = dictList.stream().filter(sysDictDataResponse -> !Constants.WARNING_SOURCE_11.equals(sysDictDataResponse.getDictValue())).collect(Collectors.toList());
        //查询第三方告警来源
        List<AlarmTypeVO> alarmSourceList = vmAlarmFlowConfigMapper.getThirdPartyAlarmTypes(loginUser.getTenantId(), loginUser.getIndustryCode());
        if (!CollectionUtils.isEmpty(alarmSourceList)) {
            SysDictDataResponse dataResponse = new SysDictDataResponse();
            dataResponse.setDictValue(alarmSourceList.get(0).getAlarmSource());
            dataResponse.setDictLabel(alarmSourceList.get(0).getAlarmSourceName());
            dataResponse.setDictSort(Long.valueOf(alarmSourceList.get(0).getAlarmSource()));
            dictList.add(dataResponse);
        }
        return Result.data(dictList);
    }

    /**
     * 查询该租户行业下所有的第三方告警类型
     * @param subTypeDTO
     * @return
     */
    @Override
    public List<AlarmSourceSubTypeVO> getThirdPartyAlarmTypes(AlarmSourceSubTypeDTO subTypeDTO) {
        if (StringUtils.isEmpty(subTypeDTO.getTenantId())) {
            throw new CustomException("客户ID不能为空");
        }
        if (StringUtils.isEmpty(subTypeDTO.getIndustryCode())) {
            throw new CustomException("行业编码不能为空");
        }
        List<AlarmTypeVO> alarmTypeVOList = vmAlarmFlowConfigMapper.getThirdPartyAlarmTypes(subTypeDTO.getTenantId(), subTypeDTO.getIndustryCode());
        List<AlarmSourceSubTypeVO> alarmSourceSubTypeVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(alarmTypeVOList)){
            AlarmSourceSubTypeVO subTypeVO = new AlarmSourceSubTypeVO();
            AlarmTypeVO firstAlarmType = alarmTypeVOList.get(0);
            subTypeVO.setAlarmSource(firstAlarmType.getAlarmSource());
            subTypeVO.setAlarmSourceName(firstAlarmType.getAlarmSourceName());
            //查询告警来源名称
            AlarmTypeDTO alarmTypeDTO = new AlarmTypeDTO();
            alarmTypeDTO.setTenantId(subTypeDTO.getTenantId());
            alarmTypeDTO.setIndustryCode(subTypeDTO.getIndustryCode());
            alarmTypeDTO.setAppVerCode(subTypeDTO.getAppVerCode());
            List<AlarmTypeDO> orderAlarm = alarmTypeRepository.findOrderAlarmAll(alarmTypeDTO);
            List<AlarmTypeVO> processedAlarmTypeVOList = alarmTypeVOList.stream()
                    .map(alarmType -> processAlarmType(alarmType, orderAlarm))
                    .collect(Collectors.toList());

            subTypeVO.setAlarmTypeVOList(processedAlarmTypeVOList);
            alarmSourceSubTypeVOList.add(subTypeVO);
        }
        return alarmSourceSubTypeVOList;
    }

    private AlarmTypeVO processAlarmType(AlarmTypeVO alarmType, List<AlarmTypeDO> orderAlarm) {
        List<Map<String, Object>> alarmTypeList = orderAlarm.stream()
                .filter(type -> type.getTypeValue().equals(alarmType.getTypeValue()))
                .map(type -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constants.PARAM_ALARM_TYPE_ID, type.getAlarmTypeId());
                    map.put(Constants.PARAM_TYPE_VALUE_, type.getTypeValue());
                    map.put(Constants.PARAM_TYPENAME, type.getTypeName());
                    map.put(Constants.PARAM_ICON, type.getIcon());
                    return map;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(alarmTypeList)) {
            Map<String, Object> item = alarmTypeList.get(0);
            alarmType.setAlarmTypeId(Long.valueOf(item.get(Constants.PARAM_ALARM_TYPE_ID).toString()));
            alarmType.setTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
            alarmType.setIcon(String.valueOf(item.get(Constants.PARAM_ICON)));
        }
        return alarmType;
    }

}

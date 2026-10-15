package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dataobject.FlowTemplate;
import com.chinatower.event.dataobject.FlowTemplateIndustryDO;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.FlowLinkIndustryConfigDO;
import com.chinatower.event.entity.dto.FlowTemplateIndustryDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;
import com.chinatower.event.entity.dto.VmAlarmFlowTemplateTypeDO;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.IAlarmFlowDeployService;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认告警流程配置视图接口业务实现
 * @author lyy
 * @version 1.0
 * @date 2022/7/24
 */
@Slf4j
@Service
public class AlarmFlowDeployServiceImpl implements IAlarmFlowDeployService {
    @Autowired
    private AlarmFlowDeployMapper alarmFlowDeployMapper;
    @Autowired
    private VmAlarmFlowConfigMapper alarmFlowConfigMapper;
    @Autowired
    private ISysService sysService;

    /** 日期时间格式化-到毫秒 **/
    private static final String DATE_TIME_FORMAT_HM = "yyyyMMddHHmmssSSS";

    private static final String PARAM_DELETE_FLAG = "deleteFlag";

    /**
     * 功能描述：根据条件查询默认的告警视图模板信息
     * @author lyy
     * @params [model]
     * @return com.chinatower.common.entity.Result<java.lang.Object>
     */
    @Override
    @DS(EbikeDataBaseConstant.PLATFORM)
    public Result<Object> getFlowTemplateList(FlowTemplateDTO flowTemplateDTO) {
        PageInfo<FlowTemplate> pageInfo;
        try {
            if (StringUtils.isEmpty(flowTemplateDTO.getFlowType())) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_FLOW_TYPE);
            }
            /*if (StringUtils.isEmpty(flowTemplateDTO.getCreateDateOrder()) && StringUtils.isEmpty(flowTemplateDTO.getUpdateDateOrder())) {
                // 默认按照修改时间降序
                flowTemplateDTO.setUpdateDateOrder("desc");
            }
            String orderSql = genDateOrderSql(flowTemplateDTO.getCreateDateOrder(), flowTemplateDTO.getUpdateDateOrder());
            flowTemplateDTO.setOrderSql(orderSql);*/
            Integer pageNum = flowTemplateDTO.getPageNum();
            Integer pageSize = flowTemplateDTO.getPageSize();
            if (null == pageNum || pageNum <= 0) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = 15;
            }
            PageMethod.startPage(pageNum, pageSize);
            flowTemplateDTO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            flowTemplateDTO.setDeleteFlag(Integer.valueOf(Constants.DELETE_FLAG));
            List<FlowTemplateDTO> resultList = alarmFlowDeployMapper.getFlowTemplateList(flowTemplateDTO);
            setName(resultList);
            pageInfo = new PageInfo(resultList);
        } catch (Exception e) {
            log.error(Constants.OUERY_DEFAULT_FLOW_VIEW_ERROR, e);
            return Result.fail(ResultCode.ERROR.getCode(), Constants.OUERY_DEFAULT_FLOW_VIEW_ERROR);
        }
        return Result.data(pageInfo, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 功能描述：查询行业、流程、环节配置信息
     * @author lyy
     * @param params
     * @return Result<Object>
     */
    @Override
    public Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustry(Map<String, String> params) {
        if(StringUtils.isEmpty(params.get(Constants.PARAM_APP_VER_CODE))){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_APPVER_CODE);
        }
        if(StringUtils.isEmpty(params.get(Constants.PARAM_INDUSTRY_CODE))){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        FlowTemplateIndustryDTO flowTemplateIndustryDTO = new FlowTemplateIndustryDTO();
        //获取选择的所有模板的ID
        flowTemplateIndustryDTO.setTemplateId(alarmFlowDeployMapper.getFlowTemplateIds(params));
        List<FlowTemplateIndustryDO> flowTemplateIndustryList = alarmFlowDeployMapper.queryFlowTemplateIndustry(params);
        if(CollectionUtils.isNotEmpty(flowTemplateIndustryList)){
            //筛选出状态status为：1-默认流程模板 的模板信息
            List<FlowTemplateIndustryDO> industryTemplateIds = flowTemplateIndustryList.stream().filter(s -> s.getStatus().equals("1")).collect(Collectors.toList());
            flowTemplateIndustryDTO.setId(industryTemplateIds.get(0).getId());
            //获取所选的默认模板ID放入industryTemplateId属性中
            flowTemplateIndustryDTO.setIndustryTemplateId(industryTemplateIds.get(0).getTemplateId().toString());
            flowTemplateIndustryDTO.setWarningSource(industryTemplateIds.get(0).getWarningSource());
            flowTemplateIndustryDTO.setAppVerCode(industryTemplateIds.get(0).getAppVerCode());
            flowTemplateIndustryDTO.setIndustryCode(industryTemplateIds.get(0).getIndustryCode());
        }
        /**
         * 根据行业应用版本、行业编码查询流程环节配置信息
         */
         params.put("includeEndLink", "N");
         List<FlowLinkIndustryConfigDO> industryConfigList = alarmFlowDeployMapper.queryFlowLinkIndustryConfig(params);
         if (CollectionUtils.isNotEmpty(industryConfigList)) {
             industryConfigList.forEach(item ->{
                 if(item.getTimeLimit() != null){
                     dealTimeLimt(item,Constants.OPER_TYPE_QRU);
                 }
             });
             flowTemplateIndustryDTO.setConfigList(industryConfigList);
         }
        return Result.data(flowTemplateIndustryDTO, Constants.QUERY_WAS_SUCCESSFUL);
    }

    @Override
    public Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustryLocalVersion(Map<String, String> params) {

        if(StringUtils.isEmpty(params.get(Constants.PARAM_APP_VER_CODE))){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_APPVER_CODE);
        }
        if(StringUtils.isEmpty(params.get(Constants.PARAM_INDUSTRY_CODE))){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        FlowTemplateIndustryDTO flowTemplateIndustryDTO = new FlowTemplateIndustryDTO();
        //获取选择的所有模板的ID
        flowTemplateIndustryDTO.setTemplateId(alarmFlowDeployMapper.getFlowTemplateIdsLocalVersion(params));
        List<FlowTemplateIndustryDO> flowTemplateIndustryList = alarmFlowDeployMapper.queryFlowTemplateIndustryLocalVersion(params);
        if(CollectionUtils.isNotEmpty(flowTemplateIndustryList)){
            //筛选出状态status为：1-默认流程模板 的模板信息
            List<FlowTemplateIndustryDO> industryTemplateIds = flowTemplateIndustryList.stream().filter(s -> s.getStatus().equals("1")).collect(Collectors.toList());
            flowTemplateIndustryDTO.setId(industryTemplateIds.get(0).getId());
            //获取所选的默认模板ID放入industryTemplateId属性中
            flowTemplateIndustryDTO.setIndustryTemplateId(industryTemplateIds.get(0).getTemplateId().toString());
            flowTemplateIndustryDTO.setWarningSource(industryTemplateIds.get(0).getWarningSource());
            flowTemplateIndustryDTO.setAppVerCode(industryTemplateIds.get(0).getAppVerCode());
            flowTemplateIndustryDTO.setIndustryCode(industryTemplateIds.get(0).getIndustryCode());
        }
        /**
         * 根据行业应用版本、行业编码查询流程环节配置信息
         */
        params.put("includeEndLink", "N");
        List<FlowLinkIndustryConfigDO> industryConfigList = alarmFlowDeployMapper.queryFlowLinkIndustryConfigLocalVersion(params);
        if (CollectionUtils.isNotEmpty(industryConfigList)) {
            industryConfigList.forEach(item ->{
                if(item.getTimeLimit() != null){
                    dealTimeLimt(item,Constants.OPER_TYPE_QRU);
                }
            });
            flowTemplateIndustryDTO.setConfigList(industryConfigList);
        }
        return Result.data(flowTemplateIndustryDTO, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 功能描述：新增行业、流程、环节配置信息
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<Object> addFlowLinkConfigIndustry(FlowTemplateIndustryDTO templateIndustryDTO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || org.springframework.util.StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }
        String userId = sysService.getLoginUserId();
        List<FlowTemplateIndustryDO> industryDOList = new ArrayList<>();
        //将以逗号分割的字符串templateId转换成list
        List<String> templateIds = Arrays.asList(StringUtils.split(templateIndustryDTO.getTemplateId(), Constants.COMMA));
        /**
         * 先删除该行业下已存在的流程配置信息
         */
        this.deleteIndustrys(templateIndustryDTO);
        for (String templateId : templateIds) {
            FlowTemplateIndustryDO industryDO = new FlowTemplateIndustryDO();
            industryDO.setTemplateId(Long.valueOf(templateId));
            industryDO.setWarningSource(templateIndustryDTO.getWarningSource());
            industryDO.setCreator(userId);
            industryDO.setAppVerCode(templateIndustryDTO.getAppVerCode());
            industryDO.setIndustryCode(templateIndustryDTO.getIndustryCode());
            industryDO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_INVALID);
            //获取到所选择的默认流程模板ID,将对应的该流程模板信息状态改为1：默认初始化模板
            if (templateId.equals(templateIndustryDTO.getIndustryTemplateId().toString())) {
                industryDO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            }
            industryDOList.add(industryDO);
        }
        Integer rows = alarmFlowDeployMapper.addFlowTemplateIndustry(industryDOList);
        if(rows <= 0) {
            return Result.fail("流程模板信息保存失败！");
        }
        /**
         * 批量新增流程环节配置信息
         */
        List<FlowLinkIndustryConfigDO> configList = templateIndustryDTO.getConfigList();
        // 完结节点
        FlowLinkIndustryConfigDO end = new FlowLinkIndustryConfigDO();
        end.setLinkId(6L);
        end.setLinkName("办结");
        configList.add(end);
        if(CollectionUtils.isNotEmpty(configList)){
            for (FlowLinkIndustryConfigDO industryDO : configList) {
                industryDO.setCreator(userId);
                industryDO.setAppVerCode(templateIndustryDTO.getAppVerCode());
                industryDO.setIndustryCode(templateIndustryDTO.getIndustryCode());
                if(industryDO.getTimeLimit() != null){
                    dealTimeLimt(industryDO,Constants.OPER_TYPE_MOD);
                }
            }
            Integer row = alarmFlowDeployMapper.addFlowLinkIndustryConfig(configList);
            if(row <= 0){
                return Result.fail("流程环节配置信息保存失败！");
            }
        }
        return Result.success("保存成功！");
    }

    @Override
    public Result<Object> addFlowLinkConfigIndustryLocalVersion(FlowTemplateIndustryDTO templateIndustryDTO) {
        log.info("addFlowLinkConfigIndustryLocalVersion-req:{}", JSON.toJSONString(templateIndustryDTO));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || org.springframework.util.StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }
        String userId = sysService.getLoginUserId();
        List<FlowTemplateIndustryDO> industryDOList = new ArrayList<>();
        //将以逗号分割的字符串templateId转换成list
        List<String> templateIds = Arrays.asList(StringUtils.split(templateIndustryDTO.getTemplateId(), Constants.COMMA));
        /**
         * 先删除该行业下已存在的流程配置信息
         */
        this.deleteIndustrysLocalVersion(templateIndustryDTO);
        for (String templateId : templateIds) {
            FlowTemplateIndustryDO industryDO = new FlowTemplateIndustryDO();
            industryDO.setTemplateId(Long.valueOf(templateId));
            industryDO.setWarningSource(templateIndustryDTO.getWarningSource());
            industryDO.setCreator(userId);
            industryDO.setAppVerCode(templateIndustryDTO.getAppVerCode());
            industryDO.setIndustryCode(templateIndustryDTO.getIndustryCode());
            industryDO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_INVALID);
            //获取到所选择的默认流程模板ID,将对应的该流程模板信息状态改为1：默认初始化模板
            if (templateId.equals(templateIndustryDTO.getIndustryTemplateId().toString())) {
                industryDO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            }
            industryDO.setLocalVersion(templateIndustryDTO.getLocalVersion());
            industryDOList.add(industryDO);
        }
        Integer rows = alarmFlowDeployMapper.addFlowTemplateIndustryLocalVersion(industryDOList);
        if(rows <= 0) {
            return Result.fail("流程模板信息保存失败！");
        }
        /**
         * 批量新增流程环节配置信息
         */
        List<FlowLinkIndustryConfigDO> configList = templateIndustryDTO.getConfigList();
        // 完结节点
        FlowLinkIndustryConfigDO end = new FlowLinkIndustryConfigDO();
        end.setLinkId(6L);
        end.setLinkName("办结");
        configList.add(end);
        if(CollectionUtils.isNotEmpty(configList)){
            for (FlowLinkIndustryConfigDO industryDO : configList) {
                industryDO.setCreator(userId);
                industryDO.setAppVerCode(templateIndustryDTO.getAppVerCode());
                industryDO.setIndustryCode(templateIndustryDTO.getIndustryCode());
                industryDO.setLocalVersion(templateIndustryDTO.getLocalVersion());
                if(industryDO.getTimeLimit() != null){
                    dealTimeLimt(industryDO,Constants.OPER_TYPE_MOD);
                }
            }
            Integer row = alarmFlowDeployMapper.addFlowLinkIndustryConfigLocalVersion(configList);
            if(row <= 0){
                return Result.fail("流程环节配置信息保存失败！");
            }
        }
        return Result.success("保存成功！");
    }

    /**
     * 根据秒值和单位转换为对应单位的值
     * @param vmFlowLinkAttrDTO
     */
    public void dealTimeLimt(FlowLinkIndustryConfigDO vmFlowLinkAttrDTO, String operType){
        try {
            String timeLimitCompany = vmFlowLinkAttrDTO.getTimeLimitCompany();
            Integer timeLimit = vmFlowLinkAttrDTO.getTimeLimit();
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
            BigDecimal resultTime = Constants.OPER_TYPE_MOD.equals(operType) ? new BigDecimal(timeLimit).multiply(new BigDecimal(limitCompany)):new BigDecimal(timeLimit).divide(new BigDecimal(limitCompany));
            vmFlowLinkAttrDTO.setTimeLimit(Integer.parseInt(resultTime.toString()));
        }catch (Exception e){
            log.error("告警环节处理权限转换单位异常：",e);
        }

    }

    /**
     * 功能描述：订购初始化流程配置
     * @author lyy
     * @param flowConfigModel
     * @return Result<Object>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<Object> addAlarmFlowTypeAndConfig(VmAlarmFlowConfigModel flowConfigModel){
        if(StringUtils.isEmpty(flowConfigModel.getAppVerCode())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_APPVER_CODE);
        }
        if(StringUtils.isEmpty(flowConfigModel.getIndustryCode())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        //获取前端传过来告警类型信息
        List<VmAlarmFlowTemplateTypeDO> templateTypeList = flowConfigModel.getFlowTemplateTypeList();
        if (CollectionUtils.isEmpty(templateTypeList)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "告警类型不能为空");
        }
        List<String> combinedList = templateTypeList.stream()
                .map(item -> item.getAlarmType() + Constants.SEP + item.getWarningSource())
                .collect(Collectors.toList());
        flowConfigModel.setSourceAndTypeList(combinedList);
        // 校验alarmType是否已存在
        List<String> existAlarmType = alarmFlowConfigMapper.getExistAlarmType(flowConfigModel);
        if (CollectionUtils.isNotEmpty(existAlarmType)&&existAlarmType.size()==templateTypeList.size()) {
            return Result.success("告警类型已存在，不需重复添加");
        } else {
            // 保留不存在的
            templateTypeList = templateTypeList.stream().filter(type -> existAlarmType.indexOf(type.getAlarmType()+'-'+type.getWarningSource()) < 0).collect(Collectors.toList());
        }

        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAM_APP_VER_CODE, flowConfigModel.getAppVerCode());
        params.put(Constants.PARAM_INDUSTRY_CODE, flowConfigModel.getIndustryCode());
        //初始化：先设置一个默认的告警流程配置名称（如：订购_20220815163431801）
        String timeStr = new SimpleDateFormat(DATE_TIME_FORMAT_HM).format(new Date());
        flowConfigModel.setAlarmFlowName(Constants.ORDER_INDUSTRY_ALARM_FLOW_CONFIG + timeStr);
        //根据行业应用版本、行业编码获取默认流程的模板ID
        List<FlowTemplateIndustryDO> industryDOS = alarmFlowDeployMapper.queryFlowTemplateIndustry(params);
        if (CollectionUtils.isNotEmpty(industryDOS)){
            flowConfigModel.setTemplateId(industryDOS.get(0).getTemplateId());
            FlowTemplateDTO templateDTO = new FlowTemplateDTO();
            templateDTO.setTemplateId(industryDOS.get(0).getTemplateId());
            templateDTO.setDeleteFlag(Integer.valueOf(Constants.DELETE_FLAG));
            templateDTO.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            //根据模板ID从vm_flow_template表获取模板名称
            List<FlowTemplateDTO> templateList = alarmFlowDeployMapper.getFlowTemplateList(templateDTO);
            if(CollectionUtils.isNotEmpty(templateList)){
                flowConfigModel.setFlowName(templateList.get(0).getFlowName());
                /**
                 * 新增到告警流程配置表：vm_alarm_flow_config
                 */
                Integer rows = alarmFlowConfigMapper.addVmAlarmFlowConfig(flowConfigModel);
                if (rows <= 0) {
                    return Result.fail(Constants.ALARM_FLOW_CONFIG_SAVE_FAIL);
                }

                if (CollectionUtils.isNotEmpty(templateTypeList)) {
                    for (VmAlarmFlowTemplateTypeDO vmAlarmFlowTemplateTypeDO : templateTypeList) {
                        //设置templateId属性值为：表vm_flow_template_industry的id
                        vmAlarmFlowTemplateTypeDO.setTemplateId(industryDOS.get(0).getTemplateId());
                        vmAlarmFlowTemplateTypeDO.setTenantId(flowConfigModel.getTenantId());
                        vmAlarmFlowTemplateTypeDO.setAppVerCode(flowConfigModel.getAppVerCode());
                        vmAlarmFlowTemplateTypeDO.setIndustryCode(flowConfigModel.getIndustryCode());
                        //alarmFlowId对应告警流程配置表vm_alarm_flow_config的id
                        vmAlarmFlowTemplateTypeDO.setAlarmFlowId(flowConfigModel.getId());
                        vmAlarmFlowTemplateTypeDO.setCreator(flowConfigModel.getCreator());
                        vmAlarmFlowTemplateTypeDO.setModifier(flowConfigModel.getModifier());
                    }
                    /**
                     * 批量新增流程配置关联的告警类型表：vm_flow_template_alarm_type
                     */
                    alarmFlowConfigMapper.batchAddFlowTemplateType(templateTypeList);
                }
            }
        } else {
            return Result.fail("未查询默认模板信息！");
        }
        return Result.success(Constants.ALARM_FLOW_CONFIG_SAVE_SUCCESS);
    }

    /**
     * 功能描述： 判断角色是否已经被流程使用
     * @author liusq
     * @param param roleId,tenantId, industryCode, appVerCode
     * @return
     */
    @Override
    public Result<Object> getFlowsByRole(Map param) {
        if (param==null || param.get("roleId")==null) {
            return Result.fail("角色ID不能为空");
        }
        param.put(PARAM_DELETE_FLAG, Constants.DELETE_FLAG);
        List<Map> flowsExist = alarmFlowConfigMapper.getFlowByRoleId(param);
        if (CollectionUtils.isNotEmpty(flowsExist)) {
            return Result.data(flowsExist, "角色已关联流程模板");
        }
        param.remove(PARAM_DELETE_FLAG);
        List<Map> industryConfigs = alarmFlowConfigMapper.getIndustryConfigByRoleId(param);
        if (CollectionUtils.isNotEmpty(industryConfigs)) {
            Map<String, Object> industry = new HashMap<>();
            industry.put("id", 0);
            industry.put("alarmFlowName", "行业初始化流程");
            List<Map> res = new ArrayList<>();
            res.add(industry);
            return Result.data(res, "角色已关联默认流程模板");
        }
        List<Map> flowsHistory = alarmFlowConfigMapper.getFlowByRoleId(param);
        return this.flowInUse(flowsHistory, null,null);
    }

    /**
     * 功能描述： 判断用户是否已经被流程使用
     * @author liusq
     * @param param userId,tenantId, industryCode, appVerCode
     * @return
     */
    @Override
    public Result<Object> getFlowsByUser(Map param) {
        if (param==null || param.get("userId")==null) {
            return Result.fail("用户ID不能为空");
        }
        param.remove(PARAM_DELETE_FLAG);
        List<Map> flowsHistory = alarmFlowConfigMapper.getFlowByUserId(param);
        Result<Object> result = this.flowInUse(flowsHistory, null, String.valueOf(param.get("userId")));
        if (!result.getMsg().equals(Constants.NOT_ASSOCIATED)){
            return Result.data(Constants.ALARM_FLOW_USE_STATUS,result.getData(),Constants.ALARM_FLOW_USE);
        } else {
            param.put(PARAM_DELETE_FLAG, Constants.DELETE_FLAG);
            List<Map> flowsExist = alarmFlowConfigMapper.getFlowByUserId(param);
            if (CollectionUtils.isNotEmpty(flowsExist)) {
                return Result.data(Constants.PROCESS_CONFIGURATION_STATUS,flowsExist, Constants.PROCESS_CONFIGURATION);
            }else {
                return Result.success(Constants.NOT_ASSOCIATED);
            }
        }
    }

    /**
     * 功能描述：判断工单表中是否有正在流转的事件
     * @author lyy
     * @param flowsHistory 告警流程配置list  两个参数有一个就行
     * @param templateId 流程模板id
     * @return
     */
    @Override
    public Result<Object> flowInUse(List<Map> flowsHistory, Long templateId,String userId) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String industryCode = loginUser.getUser().getIndustryCode();
        String tenantId = loginUser.getUser().getTenantId();
        if (CollectionUtils.isNotEmpty(flowsHistory)||templateId!=null||StringUtils.isNotEmpty(userId)) {
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < 6; i++) {
                // 获取表名
                String tableName = TableUtil.getTableName(calendar.getTime(), TableConstant.WARN_ORDER_TEBLE);
                List<Map> flowInUse = alarmFlowConfigMapper.getFlowInUse(flowsHistory, tableName, templateId,userId,industryCode,tenantId);
                if (CollectionUtils.isNotEmpty(flowInUse)) {
                        return Result.data(Constants.ALARM_FLOW_USE_STATUS,flowInUse, Constants.ALARM_FLOW_USE);
                }
                calendar.add(Calendar.MONTH, -1);
            }
        }
        return Result.success(Constants.NOT_ASSOCIATED);
    }

    /**
     * 功能描述：新增前，先删除该行业下的已存在的流程配置
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    private Result<Object> deleteIndustrys(FlowTemplateIndustryDTO templateIndustryDTO) {
        if(StringUtils.isEmpty(templateIndustryDTO.getAppVerCode())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_APPVER_CODE);
        }
        if(StringUtils.isEmpty(templateIndustryDTO.getIndustryCode())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        //根据行业应用版本、行业编码删除已存在的流程模板关联的告警类型信息
        // 表：vm_flow_template_industry
        alarmFlowDeployMapper.delTemplateIndustry(templateIndustryDTO);
        //逻辑删除流程环节配置表信息
        // 表：vm_flow_link_industry_config
        int i = alarmFlowDeployMapper.delIndustryConfig(templateIndustryDTO);
        if (i > 0){
            return Result.success("删除成功！");
        } else {
            return Result.fail("删除失败！");
        }
    }

    /**
     * 功能描述：新增前，先删除该行业下的已存在的流程配置
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    private Result<Object> deleteIndustrysLocalVersion(FlowTemplateIndustryDTO templateIndustryDTO) {
        if(StringUtils.isEmpty(templateIndustryDTO.getAppVerCode())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_APPVER_CODE);
        }
        if(StringUtils.isEmpty(templateIndustryDTO.getIndustryCode())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        //根据行业应用版本、行业编码删除已存在的流程模板关联的告警类型信息
        // 表：vm_flow_template_industry
        alarmFlowDeployMapper.delTemplateIndustryLocalVersion(templateIndustryDTO);
        //逻辑删除流程环节配置表信息
        // 表：vm_flow_link_industry_config
        int i = alarmFlowDeployMapper.delIndustryConfigLocalVersion(templateIndustryDTO);
        if (i > 0){
            return Result.success("删除成功！");
        } else {
            return Result.fail("删除失败！");
        }
    }

    /**
     * 功能描述：SQL拼接排序
     * @author lyy
     * @param createDateOrder
     * @param updateDateOrder
     * @return String
     */
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
     *功能描述： 补充用户、租户名称
     * @author lyy
     * @param: templateList
     * @return: List<FlowTemplateDTO>
     * @version: v1.5
     */
    private List<FlowTemplateDTO> setName(List<FlowTemplateDTO> templateList) {
        if (CollectionUtils.isNotEmpty(templateList)) {
            List<String> tenantIdList = templateList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getTenantId())).map(FlowTemplateDTO::getTenantId).collect(Collectors.toList());
            Set<String> creatorList = templateList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getCreator())).map(FlowTemplateDTO::getCreator).collect(Collectors.toSet());
            Set<String> modifierList = templateList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getModifier())).map(FlowTemplateDTO::getModifier).collect(Collectors.toSet());
            List<String> userIdList = new LinkedList<>();
            userIdList.addAll(creatorList);
            userIdList.addAll(modifierList);

            Map<String, String> userMap = new HashMap<>(10);
            if (CollectionUtils.isNotEmpty(userIdList)) {
                List<SysUser> userList = alarmFlowConfigMapper.queryUsers(userIdList, null);
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
        }
        return templateList;
    }

}

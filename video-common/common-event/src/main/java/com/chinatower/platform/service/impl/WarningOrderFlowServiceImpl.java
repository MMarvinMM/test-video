package com.chinatower.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.entity.VmAlarmOrderRecordDTO;
import com.chinatower.platform.entity.VmAlarmIOrderRecordVO;
import com.chinatower.platform.entity.IIVmFlowTemplateLinkDto;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.IOrderFileMapper;
import com.chinatower.platform.mapper.IOrderFlowRecordMapper;
import com.chinatower.platform.mapper.IOverdueRemindMapper;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.service.*;
import com.chinatower.platform.util.PKUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 11:16
 */

@Service
@Slf4j
public class WarningOrderFlowServiceImpl implements WarningOrderFlowService {

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private VmDeviceRepository vmDeviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private IOrderFlowRecordMapper IOrderFlowRecordMapper;

    @Autowired
    private SysService sysService;

    @Autowired
    private IOrderFileMapper IOrderFileMapper;

    @Autowired
    private IOverdueRemindMapper IOverdueRemindMapper;

    @Override
    public List<Map> createRecord(List<ReceiveAlarmVO> customers) {
        log.info("创建流程信息入参：{}", JSON.toJSONString(customers));
        List<Map> customerList = new ArrayList<>();
        String yearMonth = DateUtils.date2Str(new Date(), "yyyyMM");
        List<VmAlarmIOrderRecordVO> recordList = new ArrayList<>();
        VmAlarmOrderRecordDTO alarmOrderRecordVODB = new VmAlarmOrderRecordDTO();
        for (ReceiveAlarmVO customer : customers) {
            String tenantId = customer.getTenantId();
            String warningOrderId = customer.getWarningOrderId();
            String alarmType = String.valueOf(customer.getAlarmType());
            String warningSource = customer.getWarningSource();
            String alarmTime = customer.getAlarmTime();
            String appVerCode = customer.getAppVerCode();
            String industryCode = customer.getIndustryCode();
            //是否组装过流程环节人信息
            boolean buildProcessFlag = false;
            // 如果没有流程信息获取流程信息，创建流程， 如果有流程信息，不调用getAlarmFlowConfig接口
            if (StringUtils.isEmpty(customer.getFlowId())) {
                // 调用流程接口获取流程信息
                AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
                alarmFlowConfigModel.setTenantId(tenantId);
                alarmFlowConfigModel.setWarningSource(warningSource);
                alarmFlowConfigModel.setAlarmCode(alarmType);
                IIVmFlowTemplateLinkDto startFlowLink = flowLinkService.getStartFlowLink(appVerCode, industryCode, tenantId, warningSource, alarmType);
                this.setLinkInfo(startFlowLink, customer);
                String userType=startFlowLink.getUserType();
                if(StringUtils.isEmpty(userType)){
                    userType=customer.getUserType();
                }
                // 组装环节处理人 processDTOList
                //this.dealProcessUserAndRole(customer, startFlowLink);
                this.getProcessInfoNew(userType,customer,startFlowLink.getDataFilterPermiss());
                buildProcessFlag = true;
            }
            String flowId = customer.getFlowId();
            String orderStatus = customer.getOrderStatus();
            String flowName = customer.getFlowName();
            String orgId = customer.getOrgId();
            String linkName = customer.getLinkName();
            String userId = customer.getUserId();
            String userName = customer.getUserName();
            String flowNodeTime = customer.getFlowNodeTime();
            String linkId = customer.getLinkId();

            alarmOrderRecordVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), "vm_order_flow_record_t_"));

            Map customerMap = new HashMap();
            customerMap.put(Constants.PARAM_TENANT_ID, tenantId);
            log.info("保存事件，租户id： {}", tenantId);
            // 2021-06-21 start
            // 流程处理参数
            /*String processId = null;
            String processName = null;*/
            String groupId = orgId;
            String processOrderId = null;
            String roleId = customer.getRoleId();
            String userType = customer.getUserType();
            String msgNotifyType = customer.getMsgNotifyType();
            String notifyAdminFlag = customer.getNotifyAdminFlag();

            List<ProcessDTO> processDTOList = customer.getProcessDTOList();
            VmFlowLinkAttrDto vmFlowLinkAttrDto = flowLinkService.getFlowLinkAttrByAlarmFlowId(tenantId, null, Long.parseLong(flowId), Integer.parseInt(linkId));
            // 流程处理人信息
            if (!buildProcessFlag && CollectionUtils.isEmpty(processDTOList)) {
                processDTOList = new ArrayList<>();
                String dataFilterPermiss=null;
                 ////vmFlowLinkDto 为空说明flowId是模板id而非告警流程实例id
                if (vmFlowLinkAttrDto == null) {
                    IIVmFlowTemplateLinkDto flowTemplateLinkDto = flowLinkService.getFlowTemplateLink(Long.valueOf(flowId), null,null, Integer.parseInt(linkId),industryCode,appVerCode);
                    userType = flowTemplateLinkDto.getUserType();
                    dataFilterPermiss=flowTemplateLinkDto.getDataFilterPermiss();
                    /*attrId = flowTemplateLinkDto.getAttrId();
                    processRoleId = flowTemplateLinkDto.getRoleId();*/
                }
                else {
                    userType = vmFlowLinkAttrDto.getUserType();
                    dataFilterPermiss=vmFlowLinkAttrDto.getDataFilterPermiss();
                    /*attrId = vmFlowLinkAttrDto.getAttrId();
                    processRoleId = "roleId";*/
                }
                //this.getProcessInfo(userType, attrId, tenantId, processRoleId, processDTOList);
                this.getProcessInfoNew(userType,customer,dataFilterPermiss);
            }
            customerMap.put(Constants.VM_FLOW_LINK_ATTR_DTO, vmFlowLinkAttrDto);
            //组装流程记录开始环节信息
            recordList.add(this.buildStartAlarmOrderRecord(tenantId, userId, warningOrderId, yearMonth, linkName, userName, flowNodeTime,customer.getRealLinkType(),customer.getFlowLinkType()));
            //组装流程记录下一环节信息
            recordList.add(this.buildNextAlarmOrderRecord(customer, warningOrderId, flowNodeTime, processOrderId, tenantId, yearMonth,customer.getRealLinkType(),customer.getFlowLinkType(),customer.getEnableAcceptanceMode()));


            customerMap.put(Constants.PARAM_FLOW, flowId);
            customerMap.put(Constants.PARAM_ORDER_STATUS, orderStatus);
            customerMap.put(Constants.PARAM_LINK_ID, linkId);
            customerMap.put(Constants.PARAM_LINK_NAME, flowName);
            customerMap.put(Constants.PARAM_FLOW_NODE_TIME, flowNodeTime);
            /*customerMap.put(Constants.RES_PROCESS_ID, processId);
            customerMap.put(Constants.PARAM_PROCESS_NAME, processName);*/
            customerMap.put(Constants.RES_GROUP_ID, groupId);
            customerMap.put(Constants.PARAM_ROLE_ID, Constants.COMMA + roleId + Constants.COMMA);
            customerMap.put(Constants.PARAM_TIME_LIMIT, customer.getTimeLimit());

            customerMap.put(Constants.PARAM_USER_TYPE, userType);
            customerMap.put(Constants.PARAM_MSG_NOTIFY_TYPE, msgNotifyType);
            customerMap.put(Constants.PARAM_NOTIFY_ADMIN_TYPE, notifyAdminFlag);
            customerMap.put(Constants.PARAM_APP_VER_CODE, appVerCode);
            customerMap.put(Constants.PARAM_INDUSTRY_CODE, industryCode);
            customerMap.put(Constants.PARAM_ALARM_TYPE, alarmType);
            customerMap.put(Constants.PARAM_REAL_LINK_TYPE, customer.getRealLinkType());
            customerList.add(customerMap);
            alarmOrderRecordVODB.setList(recordList);
        }
        if (!CollectionUtils.isEmpty(recordList)) {
            IOrderFlowRecordMapper.insertAlarmOrderRecordList(alarmOrderRecordVODB);
            //保存上传的带水印附件信息
            this.addWaterMarkFile(customers, recordList);
        }

        return customerList;
    }


    private Result addWaterMarkFile(List<ReceiveAlarmVO> alarmVOList, List<VmAlarmIOrderRecordVO> recordList) {
        try {
            //取出开始环节信息
            VmAlarmIOrderRecordVO recordVO = recordList.stream()
                    .filter(item -> Constants.FIRST_LINK.equals(item.getLinkId()))
                    .findFirst()
                    .orElse(null);
            if(!CollectionUtils.isEmpty(alarmVOList)){
                for(ReceiveAlarmVO alarmVO : alarmVOList){
                    List<OrderFileDO> orderFileDTOList = alarmVO.getOrderFileList();
                    if (!CollectionUtils.isEmpty(orderFileDTOList)) {
                        // 添加附件
                        for (OrderFileDO orderFileDO : orderFileDTOList) {
                            orderFileDO.setOrderFileId(
                                    PKUtil.getOrderFileId(alarmVO.getTenantId(), alarmVO.getWarningOrderId().substring(0, 6)));
                            orderFileDO.setCreateBy(sysService.getLoginUserId());
                            orderFileDO.setCreateDate(new Date());
                            orderFileDO.setWarningOrderId(alarmVO.getWarningOrderId());
                            orderFileDO.setOrderFlowId(recordVO.getOrderFlowId());
                            orderFileDO.setLinkId(recordVO.getLinkId());
                            orderFileDO.setProcessId(recordVO.getProcessId());
                            orderFileDO.setProcessName(recordVO.getProcessName());
                            orderFileDO.setDeleteFlag(Constants.DELETE_FLAG);
                            IOrderFileMapper.insert(orderFileDO);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            log.error(Constants.LOG_FILE_INFO + e.getMessage());
            return Result.fail(Constants.LOG_FILE_INFO + e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }


    /**
     * 组装默认的开始流程记录信息
     * @param tenantId
     * @param userId
     * @param warningOrderId
     * @param yearMonth
     * @param linkName
     * @param userName
     * @param flowNodeTime
     * @return 流程记录开始环节信息
     */
    private VmAlarmIOrderRecordVO buildStartAlarmOrderRecord(String tenantId, String userId, String warningOrderId, String yearMonth, String linkName, String userName, String flowNodeTime, String realiknType, String flowLinkType) {
        VmAlarmIOrderRecordVO start = new VmAlarmIOrderRecordVO();
        start.setOrderFlowId(PKUtil.getFlowOrderId(tenantId, yearMonth));
        start.setDeleteFlag(Constants.DELETE_FLAG);
        start.setFlowRecordName("");
        start.setIfTimeout(Constants.ZERO);
        start.setLinkId("-1");
        start.setLinkName(linkName);
        start.setProcessId(userId);
        start.setProcessName(userName);
        start.setOrderId(warningOrderId);
        start.setIfCurrentLink(Constants.ZERO);
        start.setFlowNodeTime(DateUtils.string2Date(flowNodeTime));
        start.setFlowEndTime(DateUtils.string2Date(flowNodeTime));
        start.setRealLinkType("-1");
        start.setFlowLinkType(flowLinkType);
        start.setEnableAcceptanceMode(Constants.ZERO);
        return start;
    }


    /**
     * 组装流程环节记录表下一环节信息
     * @param customer
     * @param warningOrderId
     * @param flowNodeTime
     * @param processOrderId
     * @param tenantId
     * @param yearMonth
     * @return
     */
    private VmAlarmIOrderRecordVO buildNextAlarmOrderRecord(ReceiveAlarmVO customer, String warningOrderId, String flowNodeTime, String processOrderId, String tenantId, String yearMonth, String realiknType, String flowLinkType, String enableAcceptanceMode) {
        // 下一环节需要通过接口判断
        VmAlarmIOrderRecordVO next = new VmAlarmIOrderRecordVO();

        next.setDeleteFlag(Constants.DELETE_FLAG);
        next.setFlowRecordName("");
        next.setIfTimeout(Constants.ZERO);
        // 2021-06-21 start
        if (StringUtils.isEmpty(customer.getCurrentLinkId())) {
            //后续考虑删除，下一环节id为必传字段
            next.setLinkId(EnumUtil.FlowNode.PENDING_JUDGE.getCode());
            next.setLinkName(EnumUtil.FlowNode.PENDING_JUDGE.getName());
        } else {
            next.setLinkId(customer.getCurrentLinkId());
            next.setLinkName(customer.getCurrentLinkName());
        }
        // 2021-06-21 end
        next.setOrderId(warningOrderId);
        next.setIfCurrentLink(Constants.ONE);
        if (!StringUtils.isEmpty(customer.getTimeLimit()) && !Constants.PARAM_NULL.equals(customer.getTimeLimit())) {
            next.setTimeLimit(Integer.valueOf(customer.getTimeLimit()));
        }

        next.setFlowNodeTime(DateUtils.string2Date(flowNodeTime));
        next.setProcessOrderId(processOrderId);
        // 2021-06-21 end
        next.setOrderFlowId(PKUtil.getFlowOrderId(tenantId, yearMonth));
        next.setRealLinkType(realiknType);
        next.setFlowLinkType(flowLinkType);
        next.setEnableAcceptanceMode(enableAcceptanceMode);
        //开启受理模式时，下一环节为当前环节
        if(Constants.ONE.equals(enableAcceptanceMode)){
            next.setNextLinkId(next.getLinkId());
            next.setNextLinkName(next.getLinkName());
        }
        return next;
    }

    /**
     * 组装环节信息，需要适配，有些地方调用设置了信息，这里就不需要重新设置
     * @param startFlowLink
     * @param customer
     */
    private void setLinkInfo(IIVmFlowTemplateLinkDto startFlowLink, ReceiveAlarmVO customer) {
        customer.setRoleId(startFlowLink.getRoleId());
        customer.setRealLinkType(String.valueOf(startFlowLink.getRealLinkType()));
        customer.setFlowAttrId(startFlowLink.getFlowAttrId());
        if (StringUtils.isEmpty(customer.getFlowId())) {
            Long flowId = null != startFlowLink.getAlarmFlowId() ? startFlowLink.getAlarmFlowId() : startFlowLink.getTemplateId();
            customer.setFlowId(flowId == null? null:String.valueOf(flowId));
        }
        customer.setFlowTemplate(String.valueOf(startFlowLink.getTemplateId()));
        if (StringUtils.isEmpty(customer.getFlowName())) {
            customer.setFlowName(startFlowLink.getLinkName());
        }
        if (StringUtils.isEmpty(customer.getNotifyAdminFlag())) {
            customer.setNotifyAdminFlag(startFlowLink.getNotifyAdminFlag());
        }
        if (StringUtils.isEmpty(customer.getCurrentLinkId())) {
            customer.setCurrentLinkId(startFlowLink.getCurLink() == null?null:String.valueOf(startFlowLink.getCurLink()));
        }
        if (StringUtils.isEmpty(customer.getCurrentLinkName())) {
            customer.setCurrentLinkName(startFlowLink.getLinkName());
        }
        if (StringUtils.isEmpty(customer.getLinkId())) {
            customer.setLinkId(startFlowLink.getCurLink() == null?null:String.valueOf(startFlowLink.getCurLink()));
        }
        if (StringUtils.isEmpty(customer.getOrderStatus())) {
            customer.setOrderStatus(startFlowLink.getCurLink() == null?null:String.valueOf(startFlowLink.getCurLink()));
        }
        if (StringUtils.isEmpty(customer.getMsgNotifyType())) {
            customer.setMsgNotifyType(startFlowLink.getMsgNotifyType());
        }
        if (StringUtils.isEmpty(customer.getTimeLimit())) {
            customer.setTimeLimit(StringUtils.isEmpty(startFlowLink.getTimeLimit())?null:startFlowLink.getTimeLimit());
        }
        if (StringUtils.isEmpty(customer.getTimeLimitAcceptance())) {
            customer.setTimeLimitAcceptance(StringUtils.isEmpty(startFlowLink.getTimeLimitAcceptance())?null:startFlowLink.getTimeLimitAcceptance());
        }
        if (StringUtils.isEmpty(customer.getEnableAcceptanceMode())) {
            customer.setEnableAcceptanceMode(startFlowLink.getEnableAcceptanceMode());
        }
        if (StringUtils.isEmpty(customer.getUserType())) {
            if (StringUtils.isEmpty(startFlowLink.getUserType())) {
                customer.setUserType(Constants.USER_TYPE_2);
            }
            else {
                customer.setUserType(startFlowLink.getUserType());
            }
        }
    }



    /**
     * 组装环节处理人新
     */
    private void getProcessInfoNew(String userType, ReceiveAlarmVO customer, String dataFilterPermiss) {
        //查询流程中是否要网格相关配置标记
        String gridFlag = null;
        //网格员userId
        List<String> gridUserList = customer.getGridUserList();
        if (StringUtils.isEmpty(customer.getGridUserList()) && !StringUtils.isEmpty(customer.getGridId())) {
            String userList = vmDeviceRepository.getGridUserByGridId(customer.getGridId());
            if (!StringUtils.isEmpty(userList)) {
                gridUserList = Arrays.asList(userList.split(Constants.COMMA));
            }
        }
        //真实返回的配置用户
        List<String> resultUserList = new ArrayList<>();
        if (!StringUtils.isEmpty(userType)) {
            if (userType.contains(Constants.THREE) && !CollectionUtils.isEmpty(gridUserList)) {
                gridFlag = Constants.ONE;
            }
            //将所有配置人员返回，并将工单便指定人标记为置为1
            AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
            configModel.setId(customer.getFlowId() == null ? null : Long.valueOf(customer.getFlowId()));
            configModel.setLinkId(Long.valueOf(customer.getLinkId()));
            configModel.setGridFlag(gridFlag);
            configModel.setTemplateId(customer.getFlowTemplate() == null ? null : Long.valueOf(customer.getFlowTemplate()));
            configModel.setAppVerCode(customer.getAppVerCode());
            configModel.setIndustryCode(customer.getIndustryCode());
            configModel.setWarningSource(customer.getWarningSource());
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
            FlowModel flowLinkAttrModel = flowModelList.get(0);
            String flowUserList = "";
            String flowRoleList = "";
            //用户
            if (!StringUtils.isEmpty(flowLinkAttrModel.getUserId())) {
                flowUserList = flowLinkAttrModel.getUserId();
            }
            //角色
            if (!StringUtils.isEmpty(flowLinkAttrModel.getRoleId())) {
                flowRoleList = flowLinkAttrModel.getRoleId();
            }
            //网格员
            if (userType.contains(Constants.THREE) && gridFlag != null) {
                if (StringUtils.isEmpty(flowUserList)) {
                    flowUserList = String.join(Constants.COMMA, gridUserList);
                } else {
                    flowUserList += Constants.COMMA + String.join(Constants.COMMA, gridUserList);
                }
            }
            List<String> userIdList = new ArrayList<>();
            List<String> userIdRoleList = new ArrayList<>();
            //流程返回有角色配置时，根据租户和角色查到对应的userId
            if (!StringUtils.isEmpty(flowRoleList)) {
                List<String> roleIdList = Arrays.asList(flowRoleList.split(Constants.COMMA));
                List<Long> roles = new ArrayList<>();
                for (String role:roleIdList) {
                    if(!StringUtils.isEmpty(role)){
                        roles.add(Long.parseLong(role));
                    }
                }
                List<SysUser> userRolerList = vmDeviceRepository.getUserInfoByLongRoleIdsAndIndustryCode(roles, customer.getTenantId(),customer.getIndustryCode());
                userIdRoleList = userRolerList.stream().map(CommonFeignEntity::getUserId).collect(Collectors.toList());
            }
            if (!StringUtils.isEmpty(flowUserList)) {
                userIdList = new ArrayList<>(Arrays.asList(flowUserList.split(Constants.COMMA)));
            }
            userIdList.addAll(userIdRoleList);
            userIdList = userIdList.stream().distinct().collect(Collectors.toList());
            //需要过滤数据权限
            if (Constants.ONE.equals(dataFilterPermiss)) {
                if (!CollectionUtils.isEmpty(userIdList)) {
                    String flag = Constants.ONE;
                    try {
                        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
                        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                            flag = suppressDictData.getData().get(0).getDictValue();
                        }
                        //调用华苏接口过滤网格员数据权限
                        if (Constants.ONE.equals(flag) && !StringUtils.isEmpty(customer.getWarningSource())) {
                            //调用接口获取有摄像机权限的客户
                            Result<List<SysUser>> reponse = commonRepository.getUserByDeptAndDeviceScope(userIdList, customer.getTenantId(), customer.getDeviceCode(),
                                    customer.getIndustryCode(), customer.getAppVerCode(), customer.getWarningSource(),
                                    customer.getCityCode(), customer.getCountyCode(), customer.getProvinceCode(), customer.getAlarmType());
                            if (reponse != null && ResultCode.SUCCESS.getCode() == reponse.getCode() && reponse.getData() != null) {
                                JSONArray arrayList = JSON.parseArray(JSON.toJSONString(reponse.getData()));
                                for (Object object : arrayList) {
                                    SysUser user = (JSON.parseObject(JSON.toJSONString(object), SysUser.class));
                                    resultUserList.add(user.getUserId());
                                }
                            }
                        } else {
                            resultUserList = userIdList;
                        }
                    } catch (Exception e) {
                        log.error("getProcessInfoNew根据设备编号获取有权限的用户列表error,返回原有用户,错误信息:" + e);
                        resultUserList = userIdList;
                    }
                }
            } else {
                resultUserList = userIdList;
            }
            String userList = String.join(Constants.COMMA, resultUserList);
            customer.setProcessId(userList);
            customer.setBeforeDispatchFlag(Constants.ONE);
        }
    }

    @Override
    public void setWarningLinkInfo(List<ReceiveAlarmVO> alarms, List<Map> recordList) {
        if (!CollectionUtils.isEmpty(recordList)) {
            alarms = alarms.stream().map(item -> {
                recordList.stream().filter(record -> Objects.equals(item.getTenantId(), record.get(Constants.PARAM_TENANT_ID))
                        && Objects.equals(item.getAlarmType(), record.get(Constants.PARAM_ALARM_TYPE)) && Objects.equals(item.getIndustryCode(), record.get(Constants.PARAM_INDUSTRY_CODE))).forEach(record -> {
                    item.setLinkId((String) record.get(Constants.PARAM_LINK_ID));
                    item.setLinkName((String) record.get(Constants.PARAM_LINK_NAME));
                    item.setFlowNodeTime((String) record.get(Constants.PARAM_FLOW_NODE_TIME));
                    item.setGroupId((String) record.get(Constants.RES_GROUP_ID));
                    item.setFlowId((String) record.get(Constants.PARAM_FLOW));
                    if (!StringUtils.isEmpty(record.get(Constants.PARAM_TIME_LIMIT))) {
                        item.setTimeLimit(String.valueOf(record.get(Constants.PARAM_TIME_LIMIT)));
                    }
                    item.setOrderStatus(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)));
                    if (!StringUtils.isEmpty(record.get(Constants.PARAM_ROLE_ID))) {
                        item.setRoleId(String.valueOf(record.get(Constants.PARAM_ROLE_ID)));
                    }
                    item.setUserType(String.valueOf(record.get(Constants.PARAM_USER_TYPE)));
                    item.setMsgNotifyType(String.valueOf(record.get(Constants.PARAM_MSG_NOTIFY_TYPE)));
                    item.setNotifyAdminFlag(String.valueOf(record.get(Constants.PARAM_NOTIFY_ADMIN_TYPE)));
                    item.setRealLinkType(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)));
                    item.setVmFlowLinkAttrDto((VmFlowLinkAttrDto) record.get(Constants.VM_FLOW_LINK_ATTR_DTO));
                });
                return item;
            }).collect(Collectors.toList());
        }
        //插入环节超期/受理超期提醒表
        this.saveOverdueRemindData(alarms);
    }


    /**
     * 插入环节/受理超期提醒表
     * @param alarms
     */

    public void saveOverdueRemindData(List<ReceiveAlarmVO> alarms){
        try{
            log.info("saveOverdueRemindData-插入环节/受理超期提醒表-req:{}",JSON.toJSONString(alarms));
            List<OverdueRemindDto> overdueRemindDtoList = new ArrayList<>();
            for (ReceiveAlarmVO vo:alarms) {
                if(!StringUtils.isEmpty(vo.getFlowAttrId())){
                    //查询该流程环节配置的所有超期提醒
                    log.info("查询该流程环节配置的所有超期提醒req:{}",vo.getFlowAttrId());
                    List<FlowOverdueRemind> flowOverdueRemindList = flowLinkService.getFlowOverdueRemindByAttrId(vo.getFlowAttrId());
                    //如果不是待受理环节，只需要查询环节超期推送相关信息
                    if(!Constants.ONE.equals(vo.getEnableAcceptanceMode())){
                        flowOverdueRemindList =flowOverdueRemindList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getOverdueRemindSign())).collect(Collectors.toList());
                    }else{
                        flowOverdueRemindList =flowOverdueRemindList.stream().filter(item -> Objects.equals(Constants.ONE, item.getOverdueRemindSign())).collect(Collectors.toList());
                    }
                    log.info("查询该流程环节配置的所有超期提醒resp:{}",JSON.toJSONString(flowOverdueRemindList));
                    for (FlowOverdueRemind overdueRemind:flowOverdueRemindList) {
                        if(!StringUtils.isEmpty(overdueRemind.getTimeLimitRemind())){
                            OverdueRemindDto overdueRemindDto = new OverdueRemindDto();
                            String timeLimit = "";
                            //环节超期提醒
                            if(Constants.ZERO.equals(overdueRemind.getOverdueRemindSign()) && !StringUtils.isEmpty(vo.getTimeLimit())){
                                timeLimit = String.valueOf(Integer.parseInt(vo.getTimeLimit())+Integer.parseInt(overdueRemind.getTimeLimitRemind()));
                                overdueRemindDto.setOverdueRemindSign(Constants.ZERO);
                            }
                            //受理超期提醒
                            if(Constants.ONE.equals(overdueRemind.getOverdueRemindSign()) && Constants.ONE.equals(vo.getEnableAcceptanceMode()) && !StringUtils.isEmpty(vo.getTimeLimitAcceptance())){
                                timeLimit = String.valueOf(Integer.parseInt(vo.getTimeLimitAcceptance())+Integer.parseInt(overdueRemind.getTimeLimitRemind()));
                                overdueRemindDto.setOverdueRemindSign(Constants.ONE);
                            }
                            String remindTime = DateUtils.getExceedTime(new Date(),timeLimit);
                            BeanUtil.copyProperties(vo,overdueRemindDto);
                            overdueRemindDto.setAlarmTime(vo.getAlarmTime());
                            overdueRemindDto.setRemindTime(remindTime);
                            overdueRemindDto.setRemindProcessId(overdueRemind.getTimeLimitRemindName());
                            overdueRemindDto.setCreateTime(new Date());
                            overdueRemindDtoList.add(overdueRemindDto);
                        }
                    }
                }
            }
            if(!CollectionUtils.isEmpty(overdueRemindDtoList)){
                //插入超期提醒表
                IOverdueRemindMapper.insertOverdueRemindList(overdueRemindDtoList);
            }
        }catch (Exception e){
            log.error("saveOverdueRemindData插入超期表异常:",e);
        }
    }
}

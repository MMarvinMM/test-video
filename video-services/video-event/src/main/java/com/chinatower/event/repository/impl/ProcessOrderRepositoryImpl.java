package com.chinatower.event.repository.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.FlowRecordVO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.UserInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dataobject.ProcessOrderDO;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.vo.AlarmOrderRecordVO;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Service
@Slf4j
public class ProcessOrderRepositoryImpl extends ServiceImpl<ProcessOrderMapper, ProcessOrderDO> implements IProcessOrderRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private ProcessOrderMapper processOrderMapper;

    @Autowired
    private ProcessApproveMapper processApproveMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private ILinkUserDealService linkUserDealService;

    @Autowired
    private IAlarmFlowConfigService alarmFlowConfigService;
    @Autowired
    private VmAlarmFlowConfigMapper vmAlarmFlowConfigMapper;
    @Autowired
    private ISysService sysService;
    @Autowired
    private SysMapper sysMapper;

    /**
     * 告警事件处理(处置)
     *
     * @param dto
     * @return
     */
    @Override
    public Result submitProcessOrder(SendAlarmOrderDTO dto) {
        log.info("============告警事件处置开始============{}",JSON.toJSONString(dto));
        //兼容未更新版本的行业默认流程
        if(StringUtils.isEmpty(dto.getRealLinkType()) && Constants.REAL_LINK_TYPE_ALL.contains(dto.getLinkId())){
            dto.setRealLinkType(dto.getLinkId());
        }
        dto.setFlowLinkType(Constants.ZERO);
        if(StringUtils.isEmpty(dto.getRealLinkType())){
            return Result.fail(Constants.MESS_4);
        }
        //校验工单表：校验工单表中当面工单是否为待处置状态
        String orderId = dto.getOrderId();
        String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + orderId.substring(0, 6);

        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setOrderId(orderId);
        alarmOrderVO.setOrderStatus(EnumUtil.FlowNode.PENDING_HANDLE.getCode());//待处理中
        alarmOrderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));;
        // 20210621 start
        alarmOrderVO.setTableName(alarmTableName);
        WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderVO);
        if (null == warnOrderStatusDTO) {
            return Result.fail("告警工单状态异常或不存在!");
        }
        // 20210621 end
        // 校验环节表:判断当前工单是否在处置环节
        AlarmOrderRecordVO alarmOrderRecordVO = new AlarmOrderRecordVO();
        alarmOrderRecordVO.setOrderId(orderId);
        alarmOrderRecordVO.setIfCurrentLink(Constants.YES_FLAG);
        alarmOrderRecordVO.setRealLinkType(EnumUtil.FlowNode.PENDING_HANDLE.getCode());//处理环节
        alarmOrderRecordVO.setTableName(recordTableName);
        int num3 = orderFlowRecordMapper.getAlarmOrderRecordCount(alarmOrderRecordVO);
        if (num3 == 0) {
            return Result.fail("告警工单环节异常!");
        }
        if (!StringUtils.isEmpty(warnOrderStatusDTO.getFlowId())) {
            dto.setFlowId(warnOrderStatusDTO.getFlowId());
        }
        //当前环节ID
        dto.setCurrentLink(dto.getLinkId());
        //下一环节ID
        dto.setOrderStatus(warnOrderStatusDTO.getOrderStatus());
        // 20210621 start
        dto.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
        dto.setTenantId(warnOrderStatusDTO.getTenantId());
        // 20210621 end
        dto.setAlarmBody(warnOrderStatusDTO.getAlarmBody());
        dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
        dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
        if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
            dto.setOrgRecord(warnOrderStatusDTO.getOrgRecord());
        }
        return warningOrderRepository.updateAlarmOrder(dto);
    }

    public void getNextLinkProcessNameList(List<FlowRecordVO> result){
        for (FlowRecordVO vo:result) {
            if(!org.springframework.util.StringUtils.isEmpty(vo.getDispatchProcessName())){
                List<String> thisUserIdList = Arrays.asList(vo.getDispatchProcessName().split(","));
                List<UserInfoDto> userList =sysMapper.queryUserListInfo(thisUserIdList);
                for (UserInfoDto user:userList) {
                    if(org.springframework.util.StringUtils.isEmpty(user.getAvatar())){
                        user.setAvatar("");
                    }
                }
                //返回该环节所有选择处理人信息
                vo.setUserInfoList(userList);
                String nextLinkProcessName ="";
                for (UserInfoDto user:userList) {
                    if(org.springframework.util.StringUtils.isEmpty(nextLinkProcessName)){
                        nextLinkProcessName = user.getNickName();
                    }else{
                        nextLinkProcessName += ","+user.getNickName();
                    }
                }
                vo.setNextLinkProcessName(nextLinkProcessName);
            }
        }
    }

    public void getProcessInfoByProcessId(String processId,Map processInfo){
        List<String> userIdList = new ArrayList<>();
        userIdList.add(processId);
        List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
        if (CollectionUtils.isNotEmpty(sysUserVOList)) {
            processInfo.put("processName", sysUserVOList.get(0).getNickName());
            processInfo.put("mobilePhone", sysUserVOList.get(0).getPhonenumber());
            if (!StringUtils.isEmpty(sysUserVOList.get(0).getDeptId())) {
                List<String> deptIdList = new ArrayList<>();
                deptIdList.add(sysUserVOList.get(0).getDeptId());
                List<SysDept> sysDeptList = deviceRepository.getSysDeptByDeptIds(deptIdList);
                if (CollectionUtils.isNotEmpty(sysDeptList)) {
                    processInfo.put("orgName", sysDeptList.get(0).getDeptName());
                }
            }
        }
    }

    public void getProcessInfoByAccpetProcessId(String accpetProcessId,Map processInfo){
        List<String> userIdList = new ArrayList<>();
        userIdList.add(accpetProcessId);
        List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
        if (CollectionUtils.isNotEmpty(sysUserVOList)) {
            processInfo.put(Constants.RES_ACCEPT_PROCESS_NAME, sysUserVOList.get(0).getNickName());
            processInfo.put(Constants.RES_ACCEPT_PROCESS_PHONE,sysUserVOList.get(0).getPhonenumber());

        }
    }

    /**
     * 获取处置单详情
     *
     * @param dto
     * @return
     */
    @Override
    public Result getProcessDetail(ProcessOrderDTO dto) {

        Map map = new HashMap();
        if (!StringUtils.isEmpty(dto.getProcessOrderId())) {
            Map processInfo = processOrderMapper.getProcessInfo(dto.getProcessOrderId());

            if (null != processInfo) {
                if(!ObjectUtils.isEmpty(processInfo.get(Constants.RES_PROCESS_ID))){
                    this.getProcessInfoByProcessId(String.valueOf(processInfo.get(Constants.RES_PROCESS_ID)),processInfo);
                    /*List<String> userIdList = new ArrayList<>();
                    userIdList.add(String.valueOf(processInfo.get("processId")));
                    List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
                    if (CollectionUtils.isNotEmpty(sysUserVOList)) {
                        processInfo.put("processName", sysUserVOList.get(0).getNickName());
                        processInfo.put("mobilePhone", sysUserVOList.get(0).getPhonenumber());
                        if (!StringUtils.isEmpty(sysUserVOList.get(0).getDeptId())) {
                            List<String> deptIdList = new ArrayList<>();
                            deptIdList.add(sysUserVOList.get(0).getDeptId());
                            List<SysDept> sysDeptList = deviceRepository.getSysDeptByDeptIds(deptIdList);
                            if (CollectionUtils.isNotEmpty(sysDeptList)) {
                                processInfo.put("orgName", sysDeptList.get(0).getDeptName());
                            }
                        }
                    }*/
                }
                if(!StringUtils.isEmpty((String)processInfo.get(Constants.PARAM_ORDER_ID))){
                    String tableName = TableUtil.getTableName(DateUtils.stringToDateYM(processInfo.get(Constants.PARAM_ORDER_ID).toString().substring(0, 6)),
                            TableConstant.WARN_ORDER_PROCESS_TEBLE);
                    // 处置过程
                    OrderFlowRecordDTO flowRecordDTO = new OrderFlowRecordDTO();
                    flowRecordDTO.setOrderId((String)processInfo.get(Constants.PARAM_ORDER_ID));
                    flowRecordDTO.setTableName(tableName);
                    flowRecordDTO.setProcessOrderId(dto.getProcessOrderId());
                    List<FlowRecordVO> list = warningOrderMapper.getPendingHandle(flowRecordDTO);
                    if(!CollectionUtils.isEmpty(list)){
                        this.getNextLinkProcessNameList(list);
                        processInfo.put("nextLinkId",list.get(0).getNextLinkId());
                        processInfo.put("nextLinkName",list.get(0).getNextLinkName());
                        processInfo.put("userInfoList",list.get(0).getUserInfoList());
                        processInfo.put("nextLinkProcessName",list.get(0).getNextLinkProcessName());
                        processInfo.put("flowLinkType",list.get(0).getFlowLinkType());
                        processInfo.put("enableAcceptanceMode",list.get(0).getEnableAcceptanceMode());
                        processInfo.put("flowNodeTime",list.get(0).getFlowNodeTime());
                        processInfo.put("remark",list.get(0).getRemark());
                        processInfo.put("flowEndTime",list.get(0).getFlowEndTime());
                        processInfo.put("closeRemark",list.get(0).getCloseRemark());
                        processInfo.put("voice", list.get(0).getVoice());
                        if(!StringUtils.isEmpty(list.get(0).getAcceptProcessId())){
                            this.getProcessInfoByAccpetProcessId(list.get(0).getAcceptProcessId(),processInfo);
                        }
                        if(ObjectUtils.isEmpty(processInfo.get(Constants.RES_PROCESS_ID))){
                            this.getProcessInfoByProcessId(list.get(0).getProcessId(),processInfo);
                            /*List<String> userIdList = new ArrayList<>();
                            userIdList.add(list.get(0).getProcessId());
                            List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
                            if (CollectionUtils.isNotEmpty(sysUserVOList)) {
                                processInfo.put("processName", sysUserVOList.get(0).getNickName());
                                processInfo.put("mobilePhone", sysUserVOList.get(0).getPhonenumber());
                                if (!StringUtils.isEmpty(sysUserVOList.get(0).getDeptId())) {
                                    List<String> deptIdList = new ArrayList<>();
                                    deptIdList.add(sysUserVOList.get(0).getDeptId());
                                    List<SysDept> sysDeptList = deviceRepository.getSysDeptByDeptIds(deptIdList);
                                    if (CollectionUtils.isNotEmpty(sysDeptList)) {
                                        processInfo.put("orgName", sysDeptList.get(0).getDeptName());
                                    }
                                }
                            }*/
                        }
                    }
                }
            }
            map.put("process", processInfo);
            // 判断当前是否会签，如果是选择当前处理人数据
            Map approveInfo = new HashMap();
            List<Map> approveInfoList = processApproveMapper.getApproveInfo(dto.getProcessOrderId());
            if (approveInfoList.size() == 1) {
                approveInfo = approveInfoList.get(0);
            }
            else if (approveInfoList.size() > 1) {
                String  loginUserId = sysService.getLoginUserId();
                for (Map infoMap : approveInfoList) {
                    if (loginUserId.equals(String.valueOf(infoMap.get(Constants.APPROVE_PERSON)))) {
                        approveInfo = infoMap;
                        break;
                    }
                }
            }
            if (!ObjectUtils.isEmpty(approveInfo.get(Constants.APPROVE_PERSON))) {
                List<String> userIdList = new ArrayList<>();
                userIdList.add(String.valueOf(approveInfo.get(Constants.APPROVE_PERSON)));
                List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
                if (CollectionUtils.isNotEmpty(sysUserVOList)) {
                    approveInfo.put("approvePersonName", sysUserVOList.get(0).getNickName());
                }
            }
            map.put("approve", approveInfo);

            map.put("files", processOrderMapper.getPhotos(dto.getProcessOrderId()));
        }
        return Result.data(ResultCode.SUCCESS.getCode(), map, "");
    }

    /**
     * 流程回退
     *
     * @param dto
     * @return
     */
    @Override
    public Result rollBack(SendAlarmOrderDTO dto) {
        log.info("**************回退**************：" + JSON.toJSONString(dto));
        //兼容未更新版本的行业默认流程
        if(StringUtils.isEmpty(dto.getRealLinkType()) && Constants.REAL_LINK_TYPE_ALL.contains(dto.getLinkId())){
            dto.setRealLinkType(dto.getLinkId());
        }
        dto.setFlowLinkType(Constants.TWO);
        if(StringUtils.isEmpty(dto.getRealLinkType())){
            return Result.fail(Constants.MESS_4);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        //校验工单表：校验工单表中当面工单是否为待抢单状态
        String orderId = dto.getOrderId();
        String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + orderId.substring(0, 6);

        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setOrderId(orderId);
        alarmOrderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));;
        alarmOrderVO.setTableName(alarmTableName);
        WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderVO);
        if (null == warnOrderStatusDTO) {
            return Result.fail(Constants.MESS_3);
        }
        if(StringUtils.isEmpty(dto.getFlowTemplate())){
            dto.setFlowTemplate(warnOrderStatusDTO.getFlowTemplate());
        }
        if (StringUtils.isEmpty(dto.getFlowId())) {
            dto.setFlowId(warnOrderStatusDTO.getFlowId());
        }
        //流程回退标识
        dto.setRollBack(Constants.ONE);
        dto.setCurrentLink(warnOrderStatusDTO.getLinkId());

        //租户ID
        dto.setTenantId(warnOrderStatusDTO.getTenantId());

        //查询回退环节处理人
        LinkUserDealModel model = new LinkUserDealModel();
        model.setTableSuffix(orderId.substring(0, 6));
        model.setWarningOrderId(orderId);
        model.setLinkId(Long.valueOf(dto.getLinkId()));
        //1：流转下个环节  0：不流转
        dto.setIsNextLink(Constants.YES_FLAG);
        dto.setWarningSource(warnOrderStatusDTO.getWarningSource());
        List<ProcessDTO> processDTOList = new ArrayList<>();
        //获取处理人信息
        this.getProcessInfo(dto,processDTOList);
//        Result<List<LinkUserDeal>> result = linkUserDealService.queryLinkUserDeal(model);
//        if (ResultCode.SUCCESS.getCode() == result.getCode()) {
//            List<ProcessDTO> processDTOList = new ArrayList<>();
//            List<LinkUserDeal> linkUserDeals = result.getData();
//            List<String> userIdList = new ArrayList<>();
//            for (LinkUserDeal linkUserDeal : linkUserDeals) {
//                ProcessDTO processDTO = new ProcessDTO();
//                processDTO.setProcessId(linkUserDeal.getUserId());
//                processDTOList.add(processDTO);
//                userIdList.add(linkUserDeal.getUserId());
//            }
//            List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
//            for (ProcessDTO processDTO : processDTOList) {
//                for (SysUserVO vo : sysUserVOList) {
//                    if (processDTO.getProcessId().equals(String.valueOf(vo.getUserId()))) {
//                        processDTO.setProcessName(vo.getNickName());
//                        break;
//                    }
//                }
//            }
//            dto.setProcessDTOList(processDTOList);
//        }
        //补充处理人id
        if(!StringUtils.isEmpty(dto.getUserId())){
            dto.setProcessId(dto.getUserId());
        }
        //当前工单所有处理过的人传过去
        dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
        dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
        dto.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
        dto.setIndustryCode(warnOrderStatusDTO.getIndustryCode());
        dto.setAppVerCode(warnOrderStatusDTO.getTradeVerCode());
        if(StringUtils.isEmpty(dto.getAddress())){
            dto.setAddress(warnOrderStatusDTO.getAddress());
        }
        return warningOrderRepository.updateAlarmOrder(dto);
    }

  /**
   * 功能说明：获取流程处理人信息
   *
   * @author: zhang.xionghui
   * @param: alarmOrderVO
   * @param: processDTOList
   * @return:
   * @date: 2022/8/9 9:47
   * @requestID  需求单号及需求名称
   */
    private void getProcessInfo(SendAlarmOrderDTO alarmOrderVO, List<ProcessDTO> processDTOList){
        //获取对应流程配置信息
        AlarmFlowConfigModel model = new AlarmFlowConfigModel();
        model.setFlowId(Long.valueOf(alarmOrderVO.getFlowId()));
        model.setLinkId(Long.valueOf(alarmOrderVO.getLinkId()));
        model.setTemplateId(Long.valueOf(alarmOrderVO.getFlowTemplate()));
        model.setWarningSource(alarmOrderVO.getWarningSource());
        Result<List<FlowModel>> result = alarmFlowConfigService.queryFlowLink(model);
        if (ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
            FlowModel flowModel = result.getData().get(0);
            if (null != flowModel) {
                //根据用户类型去获取对应的处理人信息
                if (Constants.USER_TYPE_1.equals(flowModel.getUserType())) {
                    List<VmFlowLinkUserDO> linkUserList = vmAlarmFlowConfigMapper.queryFlowLinkUserListByParam(flowModel.getAlarmFlowId(),Long.valueOf(alarmOrderVO.getLinkId()));
                    List<String> userIdList = new ArrayList<>();

                    for (VmFlowLinkUserDO linkUser : linkUserList) {
                        userIdList.add(linkUser.getUserId());
                    }
                    if (!org.springframework.util.CollectionUtils.isEmpty(userIdList)) {
                        List<SysUser> sysUserList = sysService.queryUsers(userIdList);
                        for (SysUser sysUser : sysUserList) {
                            ProcessDTO processDTO = new ProcessDTO();
                            processDTO.setProcessId(String.valueOf(sysUser.getUserId()));
                            processDTO.setProcessName(sysUser.getUserName());
                            processDTOList.add(processDTO);
                        }
                    }
                } else if (Constants.USER_TYPE_2.equals(flowModel.getUserType())) {
                    List<String> roleIdList = new ArrayList<>();
                    List<VmFlowLinkRoleDO> linkRoleList = vmAlarmFlowConfigMapper.queryFlowLinkRoleListByParam(flowModel.getAlarmFlowId(),Long.valueOf(alarmOrderVO.getLinkId()));
                    roleIdList = linkRoleList.stream().distinct().map(VmFlowLinkRoleDO::getRoleId).distinct().collect(Collectors.toList());
                    if (!org.springframework.util.CollectionUtils.isEmpty(roleIdList)) {
                        List<Long> roles = new ArrayList<>();
                        for (String role:roleIdList) {
                            if(!StringUtils.isEmpty(role)){
                                roles.add(Long.parseLong(role));
                            }
                        }
                        List<SysUser> sysUserList = deviceRepository.getUserInfoByLongRoleIds(roles, alarmOrderVO.getTenantId());
                        if (!org.springframework.util.CollectionUtils.isEmpty(sysUserList)) {
                            for (SysUser sysUser : sysUserList) {
                                ProcessDTO processDTO = new ProcessDTO();
                                processDTO.setProcessId(String.valueOf(sysUser.getUserId()));
                                processDTO.setProcessName(sysUser.getUserName());
                                processDTOList.add(processDTO);
                            }
                        }
                    }
                }
            }
        }
    }
}

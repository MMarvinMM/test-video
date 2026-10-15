package com.chinatower.event.repository.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.asyncTask.AsyncCommon;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.BatchEndWarningOrderDTO;
import com.chinatower.event.dto.BatchJudgeWarningOrderDTO;
import com.chinatower.event.dto.EndWarningOrderDTO;
import com.chinatower.event.dto.JudgeWarningOrderDTO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.service.OperateService;
import com.chinatower.event.service.OrderDelayMessageService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.vo.AlarmOrderRecordVO;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.repository.IFlowLinkAttrService;
import com.chinatower.event.repository.IWarningOrderBatchRepository;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：
 * <p>
 * 类名称: WarningOrderBatchRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/29 16:08
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class WarningOrderBatchRepositoryImpl implements IWarningOrderBatchRepository {

    @Autowired
    private WarningOrderBatchMapper warningOrderBatchMapper;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IFlowLinkAttrService flowLinkAttrService;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private ISysService sysService;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ICommonRepository iCommonRepository;

    @Autowired
    private OrderFileMapper orderFileMapper;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private OverdueRemindMapper overdueRemindMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private OrderDelayMessageService orderDelayMessageService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private IReceiveRepository receiveRepository;

    @Autowired
    private ProcessOrderMapper processOrderMapper;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    /**
     * 批量删除告警数据
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchDelWarningOrder(BatchDelWarningOrderDTO dto) {
        log.info("批量删除告警数据：入参：{}", JSON.toJSONString(dto));
        try {
            //获取登入信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || (loginUser!=null && loginUser.getUser() == null)) {
                return Result.fail(Constants.NO_USER_INFO);
            }
            SysUser sysUser = loginUser.getUser();
            String tenantId = sysUser.getTenantId();
            if (CollectionUtils.isEmpty(dto.getWarningOrderIdList())) {
                return Result.fail("warningOrderIdList不能为空");
            }

            //参数处理
            List<String> warningOrderIdList = dto.getWarningOrderIdList();
            Map<String, List<String>> map = new HashMap<>();
            for (String warningOrderId : warningOrderIdList) {
                String yearMonth = warningOrderId.substring(0, 6);
                if (CollectionUtils.isEmpty(map.get(yearMonth))) {
                    List<String> list = new ArrayList<>();
                    list.add(warningOrderId);
                    map.put(yearMonth, list);
                } else {
                    List<String> list = map.get(yearMonth);
                    list.add(warningOrderId);
                    map.put(yearMonth, list);
                }
            }
            //按分表月份循环批量删除数据
            for (String key : map.keySet()) {
                if (!CollectionUtils.isEmpty(map.get(key))) {
                    //告警表按月份表批量删除
                    String tableName = TableUtil.getTableNameByString(key, TableConstant.WARN_ORDER_TEBLE);
                    BatchDelWarningOrderDTO orderDTO = new BatchDelWarningOrderDTO();
                    orderDTO.setTableName(tableName);
                    orderDTO.setTenantId(tenantId);
                    orderDTO.setWarningOrderIdList(map.get(key));
                    orderDTO.setUserId(sysUser.getUserId());
                    orderDTO.setDeleteRemark(dto.getDeleteRemark());
                    warningOrderBatchMapper.batchUpdateWarningOrder(orderDTO);
                    //v1.6.60 同步批量删除领导批示表
                    warningOrderBatchMapper.batchDeleteLeaderAudit(orderDTO);
                    //批量插入回收站表，先删除后新增
//                    warningOrderBatchMapper.batchDeleteIntercept(orderDTO);
//                    warningOrderBatchMapper.batchAddIntercept(orderDTO);
                    //流程表按月份表批量删除
                    /*String flowRecordTableName = TableUtil.getTableNameByString(key, TableConstant.WARN_ORDER_PROCESS_TEBLE);
                    orderDTO.setTableName(flowRecordTableName);
                    warningOrderBatchMapper.batchUpdateFlowRecord(orderDTO);*/
                }
            }
            //更新研判表
            this.batchUpdateAlarmOperate(dto.getWarningOrderIdList(), Constants.OPER_TYPE_DEL, loginUser);
        } catch (Exception e) {
            log.error("批量删除告警数据失败：{}", e.getMessage());
            return Result.fail(ResultCode.ERROR, e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 批量更新研判表
     * @param warningOrderIdList
     * @param operTypeDel
     * @param loginUser
     */
    private void batchUpdateAlarmOperate(List<String> warningOrderIdList, String operTypeDel, LoginUser loginUser) {
        log.info("batchUpdateAlarmOperate-批量更改研判表:{}",JSON.toJSONString(warningOrderIdList));
        //查询运营研判表，告警事件为并行且待处理状态
        List<AlarmOperateDTO> alarmOperateList = operateService.getOperateListByOrderIds(warningOrderIdList);
        if (!CollectionUtils.isEmpty(alarmOperateList)) {
            Map<String, Object> param = new HashMap();
            List<String> warningOrderIds = alarmOperateList.stream().map(AlarmOperateDTO::getWarningOrderId).collect(Collectors.toList());
            param.put(Constants.PARAM_WARNING_ORDER_IDS, warningOrderIds);
            param.put(Constants.PARAM_CREATE_USER, sysService.getLoginUserId());
            param.put(Constants.PARAM_CREATE_USER_NAME, loginUser.getUsername());
            // 来源：客户
            param.put(Constants.PARAM_SOURCE, Constants.YES_FLAG);
            //在并行研判模式下产生的告警事件如果被关闭，将运营研判-并行研判列表中该条告警的处理状态更新为已处理，处理结果更新为关闭or删除
            if (StringUtil.isNotEmpty(operTypeDel)) {
                if(Constants.OPER_TYPE_DEL.equals(operTypeDel)){
                    param.put(Constants.PARAM_JUDGE_RESULT, Constants.JUDGE_RESULT_5);
                }else{
                    param.put(Constants.PARAM_JUDGE_RESULT, operTypeDel);
                }
            } else {
                param.put(Constants.PARAM_JUDGE_RESULT, Constants.JUDGE_RESULT_4);
            }
            //更新mysql
            alarmOperateMapper.updateAlarmOperate(param);
            //更新mongo
            try{
                List<OperateMongo> operateMongoList = new ArrayList<>();
                //多线程
                for (String warningOrderId:warningOrderIds) {
                    OperateMongo mongo = JSON.parseObject(JSON.toJSONString(param),OperateMongo.class);
                    mongo.setWarningOrderId(warningOrderId);
                    mongo.setModifierTime(DateUtils.currentDateToString());
                    operateMongoList.add(mongo);
                    operateMongoMapper.updateOperate(operateMongoList);
                }
            }catch (Exception e){
                log.error("batchUpdateAlarmOperate");
            }


        }
    }

    /**
     * 批量关闭告警
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchEndWarningOrder(BatchEndWarningOrderDTO dto) {
        log.info("批量关闭告警：入参：{}", JSON.toJSONString(dto));
        try {
            //获取登入信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || (loginUser!=null && loginUser.getUser() == null) ) {
                return Result.fail(Constants.NO_USER_INFO);
            }
            String tenantId = loginUser.getUser().getTenantId();
            if (SecurityUtils.isSwitch()) {
                loginUser = loginUser.getSwitchdLoginUser();
            }
            SysUser sysUser = loginUser.getUser();

            if (!tenantId.equals(dto.getTenantId())) {
                return Result.fail("请求租户不是当前登入租户，批量关闭告警失败！");
            }
            List<String> warningOrderIds = new ArrayList<>();
            List<EndWarningOrderDTO> warningOrderIdList = dto.getWarningOrderList();

            List<AlarmOrderRecordDTO> alarmOrderRecordList = new ArrayList<>();
            List<AlarmOrderRecordVO> updateRecoredList = new ArrayList<>();
            List<SendAlarmOrderDTO> updateAlarmOrderList = new ArrayList<>();
            List<DelayMessageDto> updatDdelayMessageList = new ArrayList<>();
            List<LeaderAuditDto> updateLeaderAuditList = new ArrayList<>();
            List<ProcessOrderDTO> processOrderDTOS = new ArrayList<>();
            if (!CollectionUtils.isEmpty(warningOrderIdList)) {
                List<WarnOrderStatusDTO> warnOrderStatusList = this.getWarnOrderStatusList(warningOrderIdList);
                for (EndWarningOrderDTO warningOrderDTO : warningOrderIdList) {
                    String warningOrderId = warningOrderDTO.getWarningOrderId();
                    warningOrderIds.add(warningOrderId);
                    WarnOrderStatusDTO warnOrderStatusDTO = this.findWarnOrder(warnOrderStatusList, warningOrderId);
                    if (null == warnOrderStatusDTO) {
                        return Result.fail(warningOrderId + Constants.MESS_3);
                    }
                    JudgeAlarmOrderDTO sendAlarmOrderDTO = new JudgeAlarmOrderDTO();
                    sendAlarmOrderDTO.setOrderId(warningOrderDTO.getWarningOrderId());
                    sendAlarmOrderDTO.setWarningOrderId(warningOrderId);
                    sendAlarmOrderDTO.setFlowId(warnOrderStatusDTO.getFlowId());
                    sendAlarmOrderDTO.setLinkId(EnumUtil.FlowNode.END.getCode());
                    sendAlarmOrderDTO.setLinkName(EnumUtil.FlowNode.END.getName());
                    sendAlarmOrderDTO.setOrderStatus(EnumUtil.FlowNode.END.getCode());
                    sendAlarmOrderDTO.setRealLinkType(EnumUtil.FlowNode.END.getCode());
                    sendAlarmOrderDTO.setFlowLinkType(Constants.ZERO);
                    sendAlarmOrderDTO.setIsNextLink(Constants.YES_FLAG);
                    sendAlarmOrderDTO.setTenantId(tenantId);
                    sendAlarmOrderDTO.setCurrentLink(warningOrderDTO.getCurrentLink());
                    sendAlarmOrderDTO.setRemark("关闭告警");
                    sendAlarmOrderDTO.setCloseRemark(dto.getCloseRemark());
                    sendAlarmOrderDTO.setAddress(dto.getAddress());
                    sendAlarmOrderDTO.setLongitude(dto.getLongitude());
                    sendAlarmOrderDTO.setLatitude(dto.getLatitude());
                    sendAlarmOrderDTO.setGridId(dto.getGridId());
                    sendAlarmOrderDTO.setGridName(dto.getGridName());
                    sendAlarmOrderDTO.setCheckGridFlag(dto.getCheckGridFlag());
                    sendAlarmOrderDTO.setWarningTypeId(dto.getWarningTypeId());
                    sendAlarmOrderDTO.setImportanceLevel(dto.getImportanceLevel());
                    sendAlarmOrderDTO.setHoriAzimuthAngle(dto.getHoriAzimuthAngle());
                    //跳过会签判断
                    sendAlarmOrderDTO.setRollBack(Constants.YES_FLAG);
                    sendAlarmOrderDTO.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
                    sendAlarmOrderDTO.setAlarmReason(dto.getAlarmReason());
                    sendAlarmOrderDTO.setDeviceHorizontal(dto.getDeviceHorizontal());
                    sendAlarmOrderDTO.setDevicePitch(dto.getDevicePitch());
                    //兼容代码
                    if(StringUtils.isEmpty(sendAlarmOrderDTO.getNextEnableAcceptanceMode())){
                        sendAlarmOrderDTO.setNextEnableAcceptanceMode(Constants.ZERO);
                    }
                    if(StringUtils.isEmpty(sendAlarmOrderDTO.getEnableAcceptanceMode())){
                        sendAlarmOrderDTO.setEnableAcceptanceMode(Constants.ZERO);
                    }
                    //查看入参是否有指定环节处理人，有则记录标记位为后续流转表记录指定人合集
                    if(sendAlarmOrderDTO.getProcessDTOList() != null && sendAlarmOrderDTO.getProcessDTOList().size() > 0){
                        sendAlarmOrderDTO.setBeforeDispatchFlag(Constants.BEFORE_DISPATCH_FLAG_1);
                    }else{
                        sendAlarmOrderDTO.setBeforeDispatchFlag(Constants.BEFORE_DISPATCH_FLAG_0);
                    }
                    sendAlarmOrderDTO.setFlowNodeTime(DateUtils.currentDateToString());
                    //已处理人
                    String userRecord = sendAlarmOrderDTO.getUserRecord();
                    if (!StringUtils.isEmpty(userRecord)) {
                        userRecord = userRecord + Constants.COMMA + sysUser.getUserId();
                    }
                    else {
                        userRecord =  sysUser.getUserId();
                    }
                    sendAlarmOrderDTO.setUserRecord(userRecord);
                    long start = System.currentTimeMillis();
                    this.updateAlarmOrder(loginUser,sendAlarmOrderDTO,alarmOrderRecordList,updateRecoredList,updateAlarmOrderList,updatDdelayMessageList,updateLeaderAuditList,processOrderDTOS,new ArrayList<>());
                    log.info("批量关闭-更新工单耗时：{}", (System.currentTimeMillis() - start));
                }
                warningOrderMapper.updateByPrimaryKeySelectiveByList(updateAlarmOrderList);
                //异步更新
                long end = System.currentTimeMillis();
                LoginUser finalLoginUser = loginUser;
                CompletableFuture.runAsync(() -> {
                    try {
                        this.batchUpdateAlarmOperate(warningOrderIds, null, finalLoginUser);
                        orderFlowRecordMapper.updateAlarmOrderRecordByList(updateRecoredList);
                        orderFlowRecordMapper.insertAlarmOrderRecordListByList(alarmOrderRecordList);
                        if (!CollectionUtils.isEmpty(updateLeaderAuditList)) {
                            warningOrderMapper.updateLeaderAuditByOrderId(updateLeaderAuditList);
                        }
                        orderDelayMessageService.updateOrderDelayList(updatDdelayMessageList);
                        overdueRemindMapper.deleteOverdueRemindByOrders(warningOrderIds);
                        warningOrderBatchMapper.batchUpdateWarningOrderCollection(warningOrderIds, dto.getAlarmReason());
                        log.info("批量关闭-异步更新耗时：{}", (System.currentTimeMillis() - end));
                    } catch (Exception e) {
                        log.error("批量关闭-异步更新失败：{}", e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("批量关闭告警失败：{}", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(ResultCode.ERROR, e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    public void updateAlarmOrder(LoginUser loginUser, JudgeAlarmOrderDTO alarmOrderVO, List<AlarmOrderRecordDTO> alarmOrderRecordList, List<AlarmOrderRecordVO> updateRecoredList, List<SendAlarmOrderDTO> updateAlarmOrderList, List<DelayMessageDto> updatDdelayMessageList, List<LeaderAuditDto> updateLeaderAuditList, List<ProcessOrderDTO> processOrderDTOS,List<ReceiveAlarmVO> alarms) {
        try {

            String warningOrderId = alarmOrderVO.getOrderId();
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
            String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderId.substring(0, 6);

            String userId = loginUser.getUser().getUserId();
            String userName = loginUser.getUsername();
            String orgId = loginUser.getUser().getDeptId();

            StringBuffer processIds = new StringBuffer();
            StringBuffer processNames = new StringBuffer();
            alarmOrderVO.setFlowNodeTime(DateUtils.currentDateToString());
            alarmOrderVO.setTableName(alarmTableName);
            //已处理人
            String userRecord = alarmOrderVO.getUserRecord();
            if (!StringUtils.isEmpty(userRecord)) {
                userRecord = userRecord + Constants.COMMA + userId;
            } else {
                userRecord = userId;
            }
            alarmOrderVO.setUserRecord(userRecord);
            //已处理组织
            String orgRecord = alarmOrderVO.getOrgRecord();
            if (!StringUtils.isEmpty(orgRecord) && !StringUtils.isEmpty(orgId)) {
                orgRecord = orgRecord + Constants.COMMA + orgId;
            } else {
                orgRecord = orgId;
            }
            alarmOrderVO.setOrgRecord(orgRecord);

            //插入流程环节表下一环节数据
            AlarmOrderRecordVO insertRecored = new AlarmOrderRecordVO();
            this.insertRecoredHandle(alarmOrderVO, insertRecored);

            //更新流程环节表当前环节数据
            AlarmOrderRecordVO updateRecored = new AlarmOrderRecordVO();
            //调度环节新增update流程表写入指定得所有处理人
            if (Constants.BEFORE_DISPATCH_FLAG_1.equals(alarmOrderVO.getBeforeDispatchFlag())) {
                updateRecored.setProcessNameList(alarmOrderVO.getProcessId());
            }
            this.updateRecoredHandle(alarmOrderVO, updateRecored, loginUser);

            List<AlarmOrderRecordVO> insertRecoredList = new ArrayList<>();

            AlarmOrderRecordDTO alarmOrderRecordDTO = new AlarmOrderRecordDTO();
            alarmOrderRecordDTO.setTableName(recordTableName);
            List<String> processOrderIds = new ArrayList<>();
            if (EnumUtil.FlowNode.PENDING_HANDLE.getCode().equals(alarmOrderVO.getRealLinkType())) {
                if (!StringUtils.isEmpty(alarmOrderVO.getSendRemark())) {
                    updateRecored.setRemark(alarmOrderVO.getSendRemark());
                }
                if (!StringUtils.isEmpty(alarmOrderVO.getCloseRemark())) {
                    updateRecored.setCloseRemark(alarmOrderVO.getCloseRemark());
                }
                List<ProcessDTO> processDTOS = alarmOrderVO.getProcessDTOList();
                // 插入处理单表
                ProcessOrderDTO processOrderVO = new ProcessOrderDTO();
                processOrderVO.setProcessOrderId(warningInterceptMapper.getUUID());
                processOrderVO.setCurrenAreaId("");
                processOrderVO.setDeleteFlag(Constants.VALID_STATE);
                processOrderVO.setOrderId(alarmOrderVO.getOrderId());
                processOrderVO.setWarningOrderId(alarmOrderVO.getWarningOrderId());
                processOrderVO.setOrderStatus(Constants.VALID_STATE);
                processOrderVO.setSendType(Constants.VALID_STATE);
                processOrderDTOS.add(processOrderVO);
                processOrderIds.add(processOrderVO.getProcessOrderId());
                if (!CollectionUtils.isEmpty(processOrderDTOS)) {
                    insertRecored.setProcessOrderId(processOrderVO.getProcessOrderId());
                }
            }
            updateRecoredList.add(updateRecored);
            //更新vm_order_flow_record_t_YYYYMM表对应处理人流程记录
            if (EnumUtil.FlowNode.END.getCode().equals(alarmOrderVO.getLinkId()) || EnumUtil.FlowNode.MISINFORMATION.getCode().equals(alarmOrderVO.getLinkId()) || EnumUtil.FlowNode.DUPLICATION.getCode().equals(alarmOrderVO.getLinkId())) {
                insertRecored.setProcessId(userId);
                insertRecored.setProcessName(userName);
            }
            //插入下一个环节得受理状态
            insertRecored.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
            insertRecored.setOrderFlowId(PKUtils.getFlowOrderId(alarmOrderVO.getTenantId(), alarmOrderVO.getWarningOrderId().substring(0, 6)));
            insertRecoredList.add(insertRecored);
            alarmOrderRecordDTO.setList(insertRecoredList);
            alarmOrderRecordList.add(alarmOrderRecordDTO);

            alarmOrderVO.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
            updateAlarmOrderList.add(alarmOrderVO);
            //同步更新领导批示表告警原因
            if(alarmOrderVO.getAlarmReason() != null){
                LeaderAuditDto leaderAuditDto = new LeaderAuditDto();
                leaderAuditDto.setOrderId(warningOrderId);
                leaderAuditDto.setAlarmReason(alarmOrderVO.getAlarmReason());
                updateLeaderAuditList.add(leaderAuditDto);
            }
            try {
                List<DelayMessageDto> delayMessageDtoList = new ArrayList<>();
                DelayMessageDto delayMessageDto = new DelayMessageDto();
                delayMessageDto.setOrderId(alarmOrderVO.getOrderId());
                //delayMessageDto.setIndustryCode(alarmOrderVO.getIndustryCode());
                delayMessageDtoList.add(delayMessageDto);
                log.info("流程流转更新延迟消息表入参:" + JSON.toJSONString(delayMessageDtoList));
                updatDdelayMessageList.addAll(delayMessageDtoList);
                orderDelayMessageService.updateOrderDelayList(delayMessageDtoList);
                if(!StringUtils.isEmpty(alarmOrderVO.getAttrId())){
                    ReceiveAlarmVO vo = new ReceiveAlarmVO();
                    BeanUtil.copyProperties(alarmOrderVO,vo);
                    vo.setFlowAttrId(alarmOrderVO.getAttrId());
                    alarms.add(vo);
                }
            } catch (Exception e) {
                log.error("流程流转更改未处理消息异常:" + e);
            }
        } catch (Exception e) {
            log.error("更新工单状态异常", e);
        }
    }
    /**
     * 功能描述：更新流程环节表当前环节数据
     * @params [alarmOrderVO, updateRecored, loginUser]
     * @return void
     * @author su.tingning
     * @date 2022/7/5 14:42
     */
    private void updateRecoredHandle(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO updateRecored, LoginUser loginUser) {
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + alarmOrderVO.getWarningOrderId().substring(0, 6);
        updateRecored.setTableName(recordTableName);
        updateRecored.setOrderId(alarmOrderVO.getWarningOrderId());
        updateRecored.setProcessId(loginUser.getUser().getUserId());
        updateRecored.setProcessName(loginUser.getUsername());
        updateRecored.setGroupId(alarmOrderVO.getGroupId() == null ? "" : alarmOrderVO.getGroupId());
        updateRecored.setIfCurrentLink(Constants.VALID_STATE);
        updateRecored.setDeleteFlag(Constants.VALID_STATE);
        updateRecored.setFlowEndTime(DateUtils.string2Date(alarmOrderVO.getFlowNodeTime()));
        // 20210721 增加语音
        updateRecored.setVoice(alarmOrderVO.getVoice());
        updateRecored.setRemark(alarmOrderVO.getRemark());
        // 20240103 增加关闭说明
        updateRecored.setCloseRemark(alarmOrderVO.getCloseRemark());
        //流转类型
        updateRecored.setFlowLinkType(alarmOrderVO.getFlowLinkType());
        updateRecored.setNextLinkId(alarmOrderVO.getLinkId());
        updateRecored.setNextLinkName(alarmOrderVO.getLinkName());
        if (!StringUtils.isEmpty(alarmOrderVO.getSendRemark())) {
            updateRecored.setRemark(alarmOrderVO.getSendRemark());
        }
    }

    /**
     * 功能描述：插入流程环节表下一环节数据
     * @params [alarmOrderVO, insertRecored]
     * @return void
     * @author su.tingning
     * @date 2022/7/5 14:35
     */
    private void insertRecoredHandle(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO insertRecored) {
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + alarmOrderVO.getWarningOrderId().substring(0, 6);
        insertRecored.setTableName(recordTableName);
        insertRecored.setOrderId(alarmOrderVO.getWarningOrderId());
        insertRecored.setCloseRemark(alarmOrderVO.getCloseRemark());
        insertRecored.setDeleteFlag(Constants.VALID_STATE);
        insertRecored.setIfTimeout(Constants.VALID_STATE);
        insertRecored.setIfCurrentLink(Constants.YES_FLAG);
        // 2021-06-21 获取下一环节处置时限
        Integer timeLimit = alarmOrderVO.getTimeLimit();
        // 2021-06-21 end
        insertRecored.setTimeLimit(timeLimit);
        insertRecored.setFlowNodeTime(DateUtils.string2Date(alarmOrderVO.getFlowNodeTime()));
        //环节ID
        insertRecored.setLinkId(alarmOrderVO.getLinkId());
        //处理环节
        insertRecored.setLinkName(alarmOrderVO.getLinkName());
        insertRecored.setFlowRecordName("");
        insertRecored.setRealLinkType(alarmOrderVO.getRealLinkType());
        if(!StringUtils.isEmpty(alarmOrderVO.getNextEnableAcceptanceMode())){
            insertRecored.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
            if(Constants.ONE.equals(alarmOrderVO.getNextEnableAcceptanceMode())){
                //下个环节为待受理时，下个环节得下一环节信息为下个环节的当前环节
                insertRecored.setNextLinkId(alarmOrderVO.getLinkId());
                insertRecored.setNextLinkName(alarmOrderVO.getLinkName());
            }
        }else {
            //兼容受理模式为空的情况按非受理处置
            insertRecored.setEnableAcceptanceMode(Constants.ZERO);
        }
    }

    public void getWarningOrderIdListParams(List<JudgeWarningOrderDTO> warningOrderIdList,List<WarnOrderStatusDTO> orderListAll){

        List<String> tableList = new ArrayList<>();
        List<String> orderIdList = new ArrayList<>();
        for (JudgeWarningOrderDTO judgeWarningOrderDTO : warningOrderIdList) {
            String warningOrderId = judgeWarningOrderDTO.getWarningOrderId();
            orderIdList.add(warningOrderId);
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
            if(!tableList.contains(alarmTableName)){
                tableList.add(alarmTableName);
            }
        }
        for (String tableName:tableList) {
            List<WarnOrderStatusDTO> orderList = warningOrderRepository.selectCheckOrderStatus(orderIdList,tableName);
            if(!CollectionUtils.isEmpty(orderList)){
                orderListAll.addAll(orderList);
            }
        }
    }

    /**
     * 批量研判
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchJudgeWarningOrder(BatchJudgeWarningOrderDTO dto) {
        log.info("租户视角批量研判:{}", JSON.toJSONString(dto));
        //获取登入信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || (loginUser!=null && loginUser.getUser() == null)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        String tenantId = loginUser.getUser().getTenantId();
        if (SecurityUtils.isSwitch()) {
            loginUser = loginUser.getSwitchdLoginUser();
        }
        SysUser sysUser = loginUser.getUser();
        if (!tenantId.equals(dto.getTenantId())) {
            return Result.fail("请求租户不是当前登入租户，批量研判失败！");
        }
        if (StringUtils.isEmpty(dto.getJudgeResult())) {
            return Result.fail("研判结果不能为空，批量研判失败！");
        }
        List<JudgeWarningOrderDTO> warningOrderIdList = dto.getWarningOrderList();
        if (warningOrderIdList.size() > 100) {
            return Result.fail("批量研判数据:" + warningOrderIdList.size() + "条，超过100条,批量研判失败！");
        }
        List<WarnOrderStatusDTO> orderListAll = new ArrayList<>();
        //获取工单对应的其他参数并校验工单状态是都是待研判
        log.info("租户视角批量研判开始校验是否待研判:{}", JSON.toJSONString(warningOrderIdList));
        this.getWarningOrderIdListParams(warningOrderIdList,orderListAll);
        if(orderListAll.size() != warningOrderIdList.size()){
            return Result.fail(Constants.MESS_3);
        }
        //补充前端入参
        for (JudgeWarningOrderDTO judgeWarningOrderDTO : warningOrderIdList) {
            for (WarnOrderStatusDTO orderDto:orderListAll) {
                if(judgeWarningOrderDTO.getWarningOrderId().equals(orderDto.getWarningOrderId())){
                    judgeWarningOrderDTO.setFlowId(orderDto.getFlowId());
                    judgeWarningOrderDTO.setProvinceId(orderDto.getProvinceId());
                    judgeWarningOrderDTO.setCityId(orderDto.getCityId());
                    judgeWarningOrderDTO.setCountyId(orderDto.getCountyId());
                    judgeWarningOrderDTO.setDeviceCode(orderDto.getDeviceCode());
                    judgeWarningOrderDTO.setWarningSource(orderDto.getWarningSource());
                    judgeWarningOrderDTO.setAlarmTime(orderDto.getAlarmTime());
                    judgeWarningOrderDTO.setTradeVerCode(orderDto.getTradeVerCode());
                    judgeWarningOrderDTO.setIndustryCode(orderDto.getIndustryCode());
                    judgeWarningOrderDTO.setNextEnableAcceptanceMode(orderDto.getEnableAcceptanceMode());
                    judgeWarningOrderDTO.setWarningTypeId(orderDto.getWarningTypeId());
                }
            }
        }
        List<String> warningOrderIds = orderListAll.stream().map(WarnOrderStatusDTO::getWarningOrderId).collect(Collectors.toList());
        log.info("批量研判补充后端入参后数据:{}",JSON.toJSONString(warningOrderIdList));
        //假警
        List<AlarmOrderRecordDTO> alarmOrderRecordList = new ArrayList<>();
        List<AlarmOrderRecordVO> updateRecoredList = new ArrayList<>();
        List<SendAlarmOrderDTO> updateAlarmOrderList = new ArrayList<>();
        List<DelayMessageDto> updatDdelayMessageList = new ArrayList<>();
        List<LeaderAuditDto> updateLeaderAuditList = new ArrayList<>();
        List<ProcessOrderDTO> processOrderDTOS = new ArrayList<>();
        List<ReceiveAlarmVO> alarms = new ArrayList<>();
        if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(dto.getJudgeResult())) {
            List<BatchOrderDto> batchOrderDtoList = new ArrayList<>();
            /*for (JudgeWarningOrderDTO judgeWarningOrderDTO : warningOrderIdList) {
                BatchOrderDto batchOrderDto = new BatchOrderDto();
                batchOrderDto.setJudgeWarningOrderDTO(judgeWarningOrderDTO);
                batchOrderDto.setDto(dto);
                batchOrderDto.setTenantId(tenantId);
                batchOrderDtoList.add(batchOrderDto);
            }
            log.info("批量研判异步更新表数据:{}",JSON.toJSONString(batchOrderDtoList));
            ThreadParallelUtil.run(batchOrderDtoList, b->{
                getParam(b);
                return null;
            }, ThreadParallelUtil.ConfigFactory.BATCH_ORDER);*/
            for (JudgeWarningOrderDTO judgeWarningOrderDTO : warningOrderIdList) {
                JudgeAlarmOrderDTO sendAlarmOrderDTO = new JudgeAlarmOrderDTO();
                sendAlarmOrderDTO.setOrderId(judgeWarningOrderDTO.getWarningOrderId());
                sendAlarmOrderDTO.setFlowId(judgeWarningOrderDTO.getFlowId());
                sendAlarmOrderDTO.setWarningOrderId(judgeWarningOrderDTO.getWarningOrderId());
                sendAlarmOrderDTO.setLinkId(EnumUtil.FlowNode.MISINFORMATION.getCode());
                sendAlarmOrderDTO.setLinkName(EnumUtil.FlowNode.MISINFORMATION.getName());
                sendAlarmOrderDTO.setOrderStatus(EnumUtil.FlowNode.MISINFORMATION.getCode());
                sendAlarmOrderDTO.setIsNextLink(Constants.YES_FLAG);
                sendAlarmOrderDTO.setTenantId(tenantId);
                sendAlarmOrderDTO.setCurrentLink(judgeWarningOrderDTO.getCurrentLink());
                sendAlarmOrderDTO.setRemark(dto.getRemark());
                sendAlarmOrderDTO.setFlowLinkType(Constants.ZERO);
                sendAlarmOrderDTO.setRealLinkType(EnumUtil.FlowNode.MISINFORMATION.getCode());
                sendAlarmOrderDTO.setWarningTypeId(judgeWarningOrderDTO.getWarningTypeId());
                //跳过会签判断
                sendAlarmOrderDTO.setRollBack(Constants.YES_FLAG);
                sendAlarmOrderDTO.setJudgeResult(Constants.JUDGE_RESULT_0);
                sendAlarmOrderDTO.setAlarmTime(judgeWarningOrderDTO.getAlarmTime());
                sendAlarmOrderDTO.setLables(judgeWarningOrderDTO.getLables());
                log.info("租户视角批量研判误报updateAlarmOrder:{}", JSON.toJSONString(sendAlarmOrderDTO));
                /*Result result = warningOrderRepository.updateAlarmOrder(sendAlarmOrderDTO);
                if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                    continue;
                } else {
                    return result;
                }*/
                sendAlarmOrderDTO.setFlowNodeTime(DateUtils.currentDateToString());
                //已处理人
                String userRecord = sendAlarmOrderDTO.getUserRecord();
                if (!StringUtils.isEmpty(userRecord)) {
                    userRecord = userRecord + Constants.COMMA + sysUser.getUserId();
                }
                else {
                    userRecord =  sysUser.getUserId();
                }
                sendAlarmOrderDTO.setUserRecord(userRecord);
                //v1.7.10 批量研判-关联告警标签
                this.setTagRelations(sendAlarmOrderDTO);
                //批量更新研判表
                this.updateAlarmOrder(loginUser,sendAlarmOrderDTO,alarmOrderRecordList,updateRecoredList,updateAlarmOrderList,updatDdelayMessageList,updateLeaderAuditList,processOrderDTOS,alarms);
            }
            /*orderFlowRecordMapper.insertAlarmOrderRecordListByList(alarmOrderRecordList);
            orderFlowRecordMapper.updateAlarmOrderRecordByList(updateRecoredList);
            warningOrderMapper.updateByPrimaryKeySelectiveByList(updateAlarmOrderList);
            orderDelayMessageService.updateOrderDelayList(updatDdelayMessageList);
            //清空该工单所有超期及受理提醒数据
            overdueRemindMapper.deleteOverdueRemindByOrders(warningOrderIds);*/
        }
        //真警
        else if (Constants.YES_FLAG.equals(dto.getJudgeResult())) {
            Map<String, FlowLinkAttrModel> FlowLinkMap = new HashMap<>();
            try {
                this.handleFlowLink(warningOrderIdList, FlowLinkMap,dto.getTenantId());
            } catch (Exception e) {
                return Result.fail("批量研判获取流程数据异常:" + e.getMessage());
            }

            for (JudgeWarningOrderDTO judgeWarningOrderDTO : warningOrderIdList) {
                String key = "";
                if(!StringUtils.isEmpty(judgeWarningOrderDTO.getGridId())){
                    key = judgeWarningOrderDTO.getFlowId() + "_" + judgeWarningOrderDTO.getCurrentLink()+"_"+judgeWarningOrderDTO.getGridId();
                }else{
                    key = judgeWarningOrderDTO.getFlowId() + "_" + judgeWarningOrderDTO.getCurrentLink();
                }
                FlowLinkAttrModel flowLinkAttrModel = FlowLinkMap.get(key);
                JudgeAlarmOrderDTO sendAlarmOrderDTO = new JudgeAlarmOrderDTO();
                sendAlarmOrderDTO.setOrderId(judgeWarningOrderDTO.getWarningOrderId());
                sendAlarmOrderDTO.setWarningOrderId(judgeWarningOrderDTO.getWarningOrderId());
                sendAlarmOrderDTO.setFlowId(judgeWarningOrderDTO.getFlowId());
                sendAlarmOrderDTO.setLinkId(String.valueOf(flowLinkAttrModel.getCurLink()));
                sendAlarmOrderDTO.setLinkName(flowLinkAttrModel.getLinkName());
                sendAlarmOrderDTO.setOrderStatus(String.valueOf(flowLinkAttrModel.getRealLinkType()));
                sendAlarmOrderDTO.setIsNextLink(Constants.YES_FLAG);
                sendAlarmOrderDTO.setTenantId(tenantId);
                sendAlarmOrderDTO.setCurrentLink(judgeWarningOrderDTO.getCurrentLink());
                sendAlarmOrderDTO.setRemark(dto.getRemark());
                sendAlarmOrderDTO.setJudgeResult(Constants.JUDGE_RESULT_1);
                sendAlarmOrderDTO.setFlowLinkType(Constants.ZERO);
                sendAlarmOrderDTO.setRealLinkType(String.valueOf(flowLinkAttrModel.getRealLinkType()));
                sendAlarmOrderDTO.setAlarmTime(judgeWarningOrderDTO.getAlarmTime());
                sendAlarmOrderDTO.setBeforeDispatchFlag(Constants.ONE);
                sendAlarmOrderDTO.setAttrId(flowLinkAttrModel.getAttrId());
                sendAlarmOrderDTO.setNextEnableAcceptanceMode(flowLinkAttrModel.getEnableAcceptanceMode());
                sendAlarmOrderDTO.setWarningTypeId(judgeWarningOrderDTO.getWarningTypeId());
                if(!StringUtils.isEmpty(flowLinkAttrModel.getTimeLimit())) {
                    sendAlarmOrderDTO.setTimeLimit(Integer.valueOf(flowLinkAttrModel.getTimeLimit()));
                }
                sendAlarmOrderDTO.setTimeLimitAcceptance(flowLinkAttrModel.getTimeLimitAcceptance());
                sendAlarmOrderDTO.setProcessId(this.getProcessId(judgeWarningOrderDTO,flowLinkAttrModel,tenantId));
                sendAlarmOrderDTO.setMsgNotifyType(flowLinkAttrModel.getMsgNotifyType());
                sendAlarmOrderDTO.setLables(judgeWarningOrderDTO.getLables());
                log.info("租户视角批量研判真警updateAlarmOrder:{}", JSON.toJSONString(sendAlarmOrderDTO));
              /*  Result result = warningOrderRepository.updateAlarmOrder(sendAlarmOrderDTO);
                if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                    continue;
                } else {
                    return result;
                }*/

                //已处理人
                String userRecord = sendAlarmOrderDTO.getUserRecord();
                if (!StringUtils.isEmpty(userRecord)) {
                    userRecord = userRecord + Constants.COMMA + sysUser.getUserId();
                }
                else {
                    userRecord =  sysUser.getUserId();
                }
                sendAlarmOrderDTO.setUserRecord(userRecord);
                sendAlarmOrderDTO.setFlowNodeTime(DateUtils.currentDateToString());
                //批量更新研判表
                this.updateAlarmOrder(loginUser,sendAlarmOrderDTO,alarmOrderRecordList,updateRecoredList,updateAlarmOrderList,updatDdelayMessageList,updateLeaderAuditList,processOrderDTOS,alarms);

                //流程环节流转推送消息
                if (Constants.ONE.equals(sendAlarmOrderDTO.getIsNextLink())
                        && !(EnumUtil.FlowNode.END.getCode().equals(sendAlarmOrderDTO.getLinkId())
                        || EnumUtil.FlowNode.MISINFORMATION.getCode().equals(sendAlarmOrderDTO.getLinkId())
                        || EnumUtil.FlowNode.DUPLICATION.getCode().equals(sendAlarmOrderDTO.getLinkId()))) {
                    // 20210625 消息推送
                    sendAlarmOrderDTO.setFlowOverTime(sendAlarmOrderDTO.getFlowNodeTime());
                    log.info("时间流转环节结束时间:{}",JSON.toJSONString(sendAlarmOrderDTO));
                    this.messagePush(sendAlarmOrderDTO, loginUser);
                }
                //v1.7.10 批量研判-关联告警标签
                this.setTagRelations(sendAlarmOrderDTO);
            }
            if (!CollectionUtils.isEmpty(processOrderDTOS)) {
                // 插入数据
                processOrderMapper.insertProcessOrderList(processOrderDTOS);
            }
        } else {
            return Result.fail("研判结果不正确，批量研判失败！");
        }
        orderFlowRecordMapper.updateAlarmOrderRecordByList(updateRecoredList);
        orderFlowRecordMapper.insertAlarmOrderRecordListByList(alarmOrderRecordList);
        warningOrderMapper.updateByPrimaryKeySelectiveByList(updateAlarmOrderList);
        //orderDelayMessageService.updateOrderDelayList(updatDdelayMessageList);
        //清空该工单所有超期及受理提醒数据
        overdueRemindMapper.deleteOverdueRemindByOrders(warningOrderIds);
        log.info("时间流转插入超期表数据req:{}",JSON.toJSONString(alarms));
        if(!CollectionUtils.isEmpty(alarms)){
            warningOrderRepository.saveOverdueRemindData(alarms);
        }

        //批量更改运营研判表状态
        //批量更新研判表
        this.batchUpdateAlarmOperate(warningOrderIds, dto.getJudgeResult(), loginUser);
        return Result.success(ResultCode.SUCCESS);
    }

    private void setTagRelations(JudgeAlarmOrderDTO sendAlarmOrderDTO) {
        try {
            if (!CollectionUtils.isEmpty(sendAlarmOrderDTO.getLables())) {
                //v1.7.10 批量研判-关联告警标签
                AddLableRelationDTO lableRelationDTO = new AddLableRelationDTO();
                lableRelationDTO.setWaringOrderId(sendAlarmOrderDTO.getOrderId());
                lableRelationDTO.setLables(sendAlarmOrderDTO.getLables());
                log.info("批量研判-告警关联标签保存入参:" + JSON.toJSONString(lableRelationDTO));
                iTradeWarningOrderService.saveTagRelation(lableRelationDTO);
            }
        } catch (Exception e) {
            log.error("批量研判-保存关联的告警标签异常:" + e);
        }
    }

    private void messagePush(SendAlarmOrderDTO alarmOrderVO, LoginUser loginUser) {
        MessagePushDTO messagePushDTO = new MessagePushDTO();
        messagePushDTO.setOrderId(alarmOrderVO.getWarningOrderId());
        messagePushDTO.setMsgId(alarmOrderVO.getWarningOrderId());
        messagePushDTO.setTenantId(alarmOrderVO.getTenantId());
        messagePushDTO.setAlarmFlowId(alarmOrderVO.getFlowId());
        messagePushDTO.setLinkId(alarmOrderVO.getLinkId());
        messagePushDTO.setNotifyType(Constants.YES_FLAG);
        messagePushDTO.setCreator(sysService.getLoginUserId());
        messagePushDTO.setNotifyTitle(alarmOrderVO.getAlarmBody());
        messagePushDTO.setTypeValue(String.valueOf(alarmOrderVO.getAlarmType()));
        messagePushDTO.setAddress(alarmOrderVO.getAddress());
        messagePushDTO.setUserType(alarmOrderVO.getUserType());
        messagePushDTO.setMsgNotifyType(alarmOrderVO.getMsgNotifyType());
        String processId = alarmOrderVO.getProcessId();
        if (!StringUtils.isEmpty(processId)) {
            if (processId.startsWith(Constants.COMMA)) {
                processId = processId.substring(1, processId.length());
            }
            if (processId.endsWith(Constants.COMMA)) {
                processId = processId.substring(0, processId.length() - 1);
            }
        }
        messagePushDTO.setUserId(processId);
        messagePushDTO.setFlowFlag(Constants.ONE);
        String roleId = alarmOrderVO.getRoleId();
        if (!StringUtils.isEmpty(roleId)) {
            if (roleId.startsWith(Constants.COMMA)) {
                roleId = roleId.substring(1, roleId.length());
            }
            if (roleId.endsWith(Constants.COMMA)) {
                roleId = roleId.substring(0, roleId.length() - 1);
            }
        }
        messagePushDTO.setRoleId(roleId);
        messagePushDTO.setNotifyType(Constants.SYS_MSG_TYPE_1);
        messagePushDTO.setOrderStatus(alarmOrderVO.getOrderStatus());
        messagePushDTO.setFlowTemplate(alarmOrderVO.getFlowTemplate());
        messagePushDTO.setAlarmTime(alarmOrderVO.getAlarmTime());
        messagePushDTO.setAppVerCode(loginUser.getUser().getAppVerCode());
        messagePushDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        messagePushDTO.setWarningTypeId(alarmOrderVO.getWarningTypeId());
        messagePushDTO.setRealLinkType(alarmOrderVO.getRealLinkType());
        messagePushDTO.setFlowOverTime(alarmOrderVO.getFlowOverTime());
        messagePushDTO.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
        receiveRepository.messagePush(messagePushDTO);
    }

    public void getParam(BatchOrderDto batchOrderDto){
        log.info("getParam-异步更改误报工单:{}",JSON.toJSONString(batchOrderDto));
        SendAlarmOrderDTO sendAlarmOrderDTO = new SendAlarmOrderDTO();
        sendAlarmOrderDTO.setOrderId(batchOrderDto.getJudgeWarningOrderDTO().getWarningOrderId());
        sendAlarmOrderDTO.setFlowId(batchOrderDto.getJudgeWarningOrderDTO().getFlowId());
        sendAlarmOrderDTO.setLinkId(EnumUtil.FlowNode.MISINFORMATION.getCode());
        sendAlarmOrderDTO.setLinkName(EnumUtil.FlowNode.MISINFORMATION.getName());
        sendAlarmOrderDTO.setOrderStatus(EnumUtil.FlowNode.MISINFORMATION.getCode());
        sendAlarmOrderDTO.setIsNextLink(Constants.YES_FLAG);
        sendAlarmOrderDTO.setTenantId(batchOrderDto.getTenantId());
        sendAlarmOrderDTO.setCurrentLink(batchOrderDto.getJudgeWarningOrderDTO().getCurrentLink());
        sendAlarmOrderDTO.setRemark(batchOrderDto.getDto().getRemark());
        sendAlarmOrderDTO.setFlowLinkType(Constants.ZERO);
        sendAlarmOrderDTO.setRealLinkType(EnumUtil.FlowNode.MISINFORMATION.getCode());
        //跳过会签判断
        sendAlarmOrderDTO.setRollBack(Constants.YES_FLAG);
        sendAlarmOrderDTO.setJudgeResult(Constants.JUDGE_RESULT_0);
        sendAlarmOrderDTO.setAlarmTime(batchOrderDto.getJudgeWarningOrderDTO().getAlarmTime());
        log.info("租户视角批量研判误报updateAlarmOrder:{}", JSON.toJSONString(sendAlarmOrderDTO));
        Result result = warningOrderRepository.updateAlarmOrder(sendAlarmOrderDTO);
    }

    /**
     * 根据工单信息过滤下一环节处理人
     * @param judgeWarningOrderDTO
     * @param flowLinkAttrModel
     */
    public String getProcessId(JudgeWarningOrderDTO judgeWarningOrderDTO,FlowLinkAttrModel flowLinkAttrModel,String tenantId){
        log.info("批量研判获取过滤数据权限人员开始-工单信息:{}",JSON.toJSONString(judgeWarningOrderDTO));
        log.info("批量研判获取过滤数据权限人员开始-流程信息:{}",JSON.toJSONString(flowLinkAttrModel));
        if(Constants.ONE.equals(flowLinkAttrModel.getDataFilterPermiss())){
            //需要过滤数据权限
            List<String> userIdList = Arrays.asList(flowLinkAttrModel.getUserId().split(","));
            List<String> resultUserList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(userIdList)) {
                String flag = Constants.ONE;
                try {
                    Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
                    if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                        flag = suppressDictData.getData().get(0).getDictValue();
                    }
                    //调用华苏接口过滤网格员数据权限
                    if (Constants.ONE.equals(flag) && !StringUtils.isEmpty(judgeWarningOrderDTO.getWarningSource())) {
                        //调用接口获取有摄像机权限的客户
                        Result<List<SysUser>> reponse = iCommonRepository.getUserByDeptAndDeviceScope(userIdList, tenantId, judgeWarningOrderDTO.getDeviceCode(),
                                judgeWarningOrderDTO.getIndustryCode(), judgeWarningOrderDTO.getTradeVerCode(), judgeWarningOrderDTO.getWarningSource(),
                                judgeWarningOrderDTO.getCityId(), judgeWarningOrderDTO.getCountyId(), judgeWarningOrderDTO.getProvinceId(),judgeWarningOrderDTO.getWarningTypeId());
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
            String userId = "";
            if(!CollectionUtils.isEmpty(resultUserList)){
                userId = String.join(",", resultUserList);
            }
            return userId;
        }else{
            return flowLinkAttrModel.getUserId();
        }
    }

    /**
     * 批量研判流程数据
     *
     * @param warningOrderIdList
     * @param map
     */
    private void handleFlowLink(List<JudgeWarningOrderDTO> warningOrderIdList, Map<String, FlowLinkAttrModel> map,String tenantId) throws Exception {
        for (JudgeWarningOrderDTO vo : warningOrderIdList) {
            String key ="";
            if(!StringUtils.isEmpty(vo.getGridId())){
                key = vo.getFlowId() + "_" + vo.getCurrentLink()+"_"+vo.getGridId();
            }else{
                key = vo.getFlowId() + "_" + vo.getCurrentLink();
            }
            if (map.get(key) == null) {
                FlowLinkAttrModel model = new FlowLinkAttrModel();
                model.setAlarmFlowId(Long.valueOf(vo.getFlowId()));
                model.setCurLink(Long.valueOf(vo.getCurrentLink()));
                //查询是否有网格员
                String gridUserList = null;
                if(!StringUtils.isEmpty(vo.getGridId())){
                    gridUserList = deviceRepository.getGridUserByGridId(vo.getGridId());
                }
                String gridFlag = null;
                if(!StringUtils.isEmpty(gridUserList)){
                    gridFlag = Constants.ONE;
                }
                model.setGridFlag(gridFlag);
                Result<List<FlowLinkAttrModel>> result = flowLinkAttrService.queryNextLinkNew(model);
                if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                    //用户
                    List<String> flowUserList = new ArrayList<>();
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(result.getData().get(0).getUserId())) {
                        flowUserList.addAll(Arrays.asList(result.getData().get(0).getUserId().split(Constants.COMMA)));
                    }
                    //角色
                    List<Long> flowRoleList = new ArrayList<>();
                    List<String> userIdRoleList = new ArrayList<>();
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(result.getData().get(0).getRoleId())) {
                        List<String> flowRoles = Arrays.asList(result.getData().get(0).getRoleId().split(Constants.COMMA));
                        for (String role:flowRoles) {
                            if(!StringUtils.isEmpty(role)){
                                flowRoleList.add(Long.parseLong(role));
                            }
                        }
                        List<SysUser> userRolerList = deviceRepository.getUserInfoByLongRoleIdsAndIndustryCode(flowRoleList, tenantId, vo.getIndustryCode());
                        userIdRoleList = userRolerList.stream().map(CommonFeignEntity::getUserId).collect(Collectors.toList());

                    }
                    //网格员
                    if (result.getData().get(0).getUserType().contains(Constants.THREE) && gridFlag != null) {
                        flowUserList.addAll(Arrays.asList(gridUserList.split(Constants.COMMA)));
                    }
                    if(!CollectionUtils.isEmpty(userIdRoleList)){
                        flowUserList.addAll(userIdRoleList);
                    }
                    result.getData().get(0).setUserId(flowUserList.stream().collect(Collectors.joining(",")));
                    map.put(key, result.getData().get(0));
                }
            }
        }
    }


    private WarnOrderStatusDTO findWarnOrder(List<WarnOrderStatusDTO> orderStatusDTOS, String waringOrderId) {
        for (WarnOrderStatusDTO orderStatusDTO : orderStatusDTOS) {
            if (orderStatusDTO.getWarningOrderId().equals(waringOrderId)) {
                return orderStatusDTO;
            }
        }
        return null;
    }

    private List<WarnOrderStatusDTO> getWarnOrderStatusList(List<EndWarningOrderDTO> warningOrderIdList) {
        HashMap<String, AlarmOrderDTO> map = new HashMap<>(warningOrderIdList.size());
        // 把相同月份的数据归整到相同的对象中，把告警id和流程id放入对象集合中
        warningOrderIdList.forEach(warningOrder -> {
            String tableName = TableConstant.WARN_ORDER_TEBLE + warningOrder.getWarningOrderId().substring(0, 6);
            if (map.get(tableName) == null) {
                AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
                alarmOrderDTO.setTableName(tableName);
                //告警id
                ArrayList<String> warningOrderIds = new ArrayList<>(warningOrderIdList.size());
                warningOrderIds.add(warningOrder.getWarningOrderId());
                //流程id
                ArrayList<String> statuses = new ArrayList<>(warningOrderIdList.size());
                statuses.add(warningOrder.getCurrentLink());
                alarmOrderDTO.setWarningOrderIds(warningOrderIds);
                alarmOrderDTO.setOrderStatusList(statuses);
                //有效数据
                alarmOrderDTO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));;
                map.put(tableName, alarmOrderDTO);
            } else {
                AlarmOrderDTO alarmOrder = map.get(tableName);
                alarmOrder.getWarningOrderIds().add(warningOrder.getWarningOrderId());
                alarmOrder.getOrderStatusList().add(warningOrder.getCurrentLink());
            }
        });
        // 按照相同的月份批量查询
        List<WarnOrderStatusDTO> orderStatusDTOS = new ArrayList<>(warningOrderIdList.size());
        map.forEach((tableName, alarmOrder) -> {
            orderStatusDTOS.addAll(warningOrderRepository.checkOrderStatusList(alarmOrder));
        });
        return orderStatusDTOS;
    }

}

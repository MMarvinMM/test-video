package com.chinatower.event.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.api.model.receive.MessagePushVo;
import com.chinatower.event.api.model.receive.PushThirdReq;
import com.chinatower.event.api.model.receive.WarningLinkInfoReq;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.dto.AlarmOrderDuplicateDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.dto.BatchEndWarningOrderDTO;
import com.chinatower.event.dto.SelectLastProcess.SelectLastRrocessReq;
import com.chinatower.event.dto.leadertree.LeaderByTenantIdTreeReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.dto.WarningOrderDTO;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.PageInfo;

import io.seata.common.util.NumberUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * video-event
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：提供给行业调用接口
 * <p>
 * 类名称: WarningOrderForTradeController.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/21 14:40
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Api(value = "告警事件-Api", tags = "告警事件请求接口")
@Slf4j
@RestController

public class WarningOrderForTradeController extends TowerBaseController {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IWarningOrderBatchRepository warningOrderBatchRepository;

    @Autowired
    private IOrderLeaderAuditRepository orderLeaderAuditRepository;

    @Autowired
    private IWarningOrderForTradeRepository warningOrderForTradeRepository;

    @Autowired
    private IWarningOrderCollectionRepository warningOrderCollectionRepository;

    @Autowired
    private IAlarmTypeRepository alarmTypeRepository;

    @Autowired
    private IProcessOrderRepository processOrderRepository;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private IFlowLinkAttrService flowLinkAttrService;
    @Autowired
    private ISysService sysService;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IOrderFlowRecordRepository orderFlowRecordRepository;

    @Autowired
    private IWarningInterceptRepository warningInterceptRepository;

    @Autowired
    private ICheckpointAlarmRepository checkpointAlarmRepository;

    @Autowired
    private PushThirdService pushThirdService;

    @Autowired
    private IReceiveRepository iReceiveRepository;

    @Autowired
    private IReceiveAlarmNewService iReceiveAlarmNewService;

    private static final String ERROR_LOG_FORMAT = "行业查询告警列表异常：{}";


    /**
     * 功能描述：创建流程
     * @param customers
     * @return
     */
    @PostMapping("/alarm/order/createRecord")
    public List<Map> createRecord(@RequestBody List<ReceiveAlarmVO> customers){
        return warningOrderRepository.createRecord(customers);
    }

    /**
     * 功能描述：回填告警表当前环节相关字段
     * @param req
     */
    @PostMapping("/alarm/order/setWarningLinkInfo")
    public void setWarningLinkInfo(@RequestBody WarningLinkInfoReq req){
        warningOrderRepository.setWarningLinkInfo(req.getAlarms(),req.getRecordList());
    }

    /**
     * 功能描述：告警推送第三方
     * @param req
     */
    @PostMapping("/alarm/order/pushThirdOrder")
    public void pushThirdOrder(@RequestBody PushThirdReq req){
        pushThirdService.pushThirdOrder(req.getOriginalJson(),req.getList());
    }

    /**
     * 功能描述：人工上报消息推送
     * @param vo
     */
    @PostMapping("/alarm/order/messagePush")
    public void messagePush(@RequestBody MessagePushVo vo){
        MessagePushDTO dto = new MessagePushDTO();
        BeanUtil.copyProperties(vo,dto);
        log.info("人工上报消息推送-messagePush:{}",JSON.toJSONString(dto));
        iReceiveRepository.messagePush(dto);
    }

    /**
     * 功能描述：查询告警列表
     *
     * @param dto
     * @param request
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:09
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询告警列表", notes = "查询告警列表")
    @PostMapping("/alarm/orderForTrade/getOrderList")
    public TableDataInfo getOrderList(@RequestBody TradeAlarmOrderDTO dto, ServletRequest request) {
        log.info("行业查询告警列表入参：{}", JSON.toJSONString(dto));
        TableDataInfo result = new TableDataInfo();
        try{
            AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
            BeanUtils.copyProperties(dto, alarmOrderDTO);
            alarmOrderDTO.setOrderId(dto.getWarningOrderId());
            //是否行业调用 1：是
            alarmOrderDTO.setIsTrade(Constants.YES_FLAG);

            // 处理告警时间
            this.dealAlarmTimeFunc(alarmOrderDTO);

            //权限标记位为1时，提供君南调用查询所传工单所有详情
            if(Constants.ONE.equals(dto.getScopeFlag())){
                //工单号为空直接返回空
                if(CollectionUtils.isEmpty(alarmOrderDTO.getWarningOrderIds())){
                    return result;
                }
                result=warningOrderRepository.getNoScopeOrderList(alarmOrderDTO, request);
            }else{
                result= warningOrderRepository.getOrderList(alarmOrderDTO, request);
            }
            log.info("行业查询告警列表出参：{}",result);
            return result;
        }catch(Exception e){
            log.info(ERROR_LOG_FORMAT, e);
            result.setCode(ResultCode.ERROR.getCode());
            return result;
        }

    }

    /**
     * 功能描述：根据网格id获取当天的告警数量（提供给君南）
     * @param dto
     * @return String
     * @author lyy
     * @version: v1.6.10
     */
    @PostMapping("/alarm/orderForTrade/getOrderListCount")
    public String getOrderListCount(@RequestBody AlarmOrderDTO dto) {
        return warningOrderRepository.getOrderListCount(dto);
    }

    /**
     * 功能描述：根据工单号获取对应得标签和收藏状态
     * @param req
     * @return
     */
    @PostMapping("/alarm/orderForTrade/getLabelAndColStatusByOrders")
    public Result getLabelAndColStatusByOrders(@RequestBody LabelAndColStatusReq req) {
        return Result.data(warningOrderRepository.getLabelAndColStatusByOrders(req));
    }
    /**
     * 功能描述：查询告警列表
     *
     * @param dto
     * @param request
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:09
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询告警列表", notes = "查询告警列表")
    @PostMapping("/alarm/orderForTrade/getOrderListDownImgs")
    public TableDataInfo getOrderListDownImgs(@RequestBody AlarmDownloadReq dto, ServletRequest request) {
        log.info("下载图片工单列表告警列表入参：{}", JSON.toJSONString(dto));
        TableDataInfo result = new TableDataInfo();
        try{
            AlarmOrderDTO orderDto = new AlarmOrderDTO();
            BeanUtil.copyProperties(dto.getDownLoadOrderDTO(),orderDto);
            orderDto.setPageSize(50);
            result= warningOrderRepository.getOrderList(orderDto, request);
            log.info("下载图片工单列表查询告警列表出参：{}",JSON.toJSONString(result));
            return result;
        }catch(Exception e){
            log.info(ERROR_LOG_FORMAT, e);
            result.setCode(ResultCode.ERROR.getCode());
            return result;
        }
    }


    /**
     * 功能描述：查询回收站列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:09
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询告警列表", notes = "查询告警列表")
    @PostMapping("/alarm/orderForTrade/getInterceptList")
    public TableDataInfo getInterceptList(@RequestBody AlarmDownloadReq dto) {
        TableDataInfo result = new TableDataInfo();
        try{
            dto.getDownLoadWarningInterceptDTO().setPageSize(50);
            IntercepiResultVo resultVo = warningInterceptRepository.getInterceptList(dto.getDownLoadWarningInterceptDTO());
            if(resultVo == null){
                return new TableDataInfo();
            }
            if(!CollectionUtils.isEmpty(resultVo.getResultList())){
                result = getDataTable(resultVo.getResultList());
            }
            if(!Constants.ONE.equals(resultVo.getFlag())){
                result = getDataTable(resultVo.getResultList());
            }else{
                result.setCode(200);
                result.setRows(resultVo.getResultList());
                result.setTotal(resultVo.getTotal());
            }
            log.info("下载图片工单列表查询告警列表出参：{}",result);
            return result;
        }catch(Exception e){
            log.info(ERROR_LOG_FORMAT, e);
            result.setCode(ResultCode.ERROR.getCode());
            return result;
        }
    }

    private void dealAlarmTimeFunc(AlarmOrderDTO alarmOrderDTO) {
        if (Objects.isNull(alarmOrderDTO.getAlarmTimeStart()) || Objects.isNull(alarmOrderDTO.getAlarmTimeEnd())) {
            //如果没传告警时间，设置默认时间范围（半年内）
            alarmOrderDTO.setAlarmTimeStart(DateUtils.getForeSixMonth(new Date()));
            alarmOrderDTO.setAlarmTimeEnd(new Date());
        } else {
            Date startDate;
            Date minTime = DateUtils.stringToDateYMDHMS(Constants.DEFAULT_DATE);
            String localMonth = DateUtils.dateToStringYMD(new Date()).substring(Constants.FIVE_NUM, Constants.SEVEN_NUM);
            String endMonth = DateUtils.dateToStringYMD(alarmOrderDTO.getAlarmTimeEnd()).substring(Constants.FIVE_NUM, Constants.SEVEN_NUM);
            //先判断开始和结束时间是否在 “202107-当月” 范围内
            if ((alarmOrderDTO.getAlarmTimeStart().before(minTime) || alarmOrderDTO.getAlarmTimeStart().after(minTime) || alarmOrderDTO.getAlarmTimeStart().equals(minTime)) &&
                    (alarmOrderDTO.getAlarmTimeEnd().before(new Date()) || endMonth.equals(localMonth))) {
                //获取开始和结束日期相差月份（包括跨年）
                Long months = ChronoUnit.MONTHS.between(LocalDate.parse(DateUtils.dateToStringYMD(alarmOrderDTO.getAlarmTimeStart())),
                        LocalDate.parse(DateUtils.dateToStringYMD(alarmOrderDTO.getAlarmTimeEnd())));
                //如果超过6个月，根据结束时间往前推6个月，截取一个新的日期范围
                if (months > Long.valueOf(Constants.SIX_NUM)) {
                    startDate = DateUtils.getForeSixMonth(alarmOrderDTO.getAlarmTimeEnd());
                } else {
                    startDate = alarmOrderDTO.getAlarmTimeStart();
                }
                alarmOrderDTO.setAlarmTimeStart(startDate);
                alarmOrderDTO.setAlarmTimeEnd(alarmOrderDTO.getAlarmTimeEnd());
            }
        }
    }


    /**
     * 功能描述：通过摄像机查询预置位&告警数量列表
     *
     * @param dto
     * @param dto
     * @return TableDataInfo
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "通过摄像机查询预置位&告警数量列表", notes = "通过摄像机查询预置位&告警数量列表")
    @PostMapping("/alarm/orderForTrade/getPresetListAndEventCount")
    public TableDataInfo getPresetListAndEventCount(@RequestBody TradeAlarmOrderDTO dto) {
        log.info("通过摄像机查询预置位&告警数量列表：{}", JSON.toJSONString(dto));
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto, alarmOrderDTO);
        // 处理告警时间
        this.dealAlarmTimeFunc(alarmOrderDTO);
        return getDataTable(warningOrderRepository.getPresetListAndEventCount(alarmOrderDTO));
    }




        /**
         * 功能描述：获取告警详情-行业调用
         *
         * @param dto
         * @return Result<WarningOrderInfoVO>
         * @author zhen.yan
         * @date 2022/6/15 14:09
         * @version:
         */
    @ApiOperation(value = "获取告警详情", notes = "获取告警详情")
    @EventObsEncrypt
    @PostMapping("/alarm/orderForTrade/getOrderInfo")
    public Result<WarningOrderInfoVO> getOrderInfo(@RequestBody TradeAlarmOrderDTO dto) {
        log.info("行业查询告警告警详情入参：{}", JSON.toJSONString(dto));
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto, alarmOrderDTO);
        // 这里丢了userId
        if (dto.getUserId() != null) {
            alarmOrderDTO.setUserId(String.valueOf(dto.getUserId()));
        }
        alarmOrderDTO.setOrderId(dto.getWarningOrderId());
        //是否行业调用 1：是
        alarmOrderDTO.setIsTrade(Constants.YES_FLAG);
        try {
            WarningOrderInfoVO warningOrderInfoVO = warningOrderRepository.getOrderInfo(alarmOrderDTO);
            if (null == warningOrderInfoVO) {
                return Result.success("");
            }
            //查询关联告警数据
            if (!StringUtils.isEmpty(warningOrderInfoVO.getOrder().getDuplicateOrderId())) {
                alarmOrderDTO.setOrderId(warningOrderInfoVO.getOrder().getDuplicateOrderId());
                WarningOrderInfoVO duplicate = warningOrderRepository.getOrderInfo(alarmOrderDTO);
                if (null == duplicate) {
                    warningOrderInfoVO.getOrder().setDuplicateOrder(duplicate);
                }
            }
            //告警工单ID获取处理环节信息
            AlarmOrderDTO orderDTO = new AlarmOrderDTO();
            orderDTO.setOrderId(warningOrderInfoVO.getOrder().getWarningOrderId());
            String loginUserId = sysService.getLoginUserId();
            orderDTO.setUserId(loginUserId);
            List<Map> mapList = warningOrderRepository.getRecordByOrderId(orderDTO);
            warningOrderInfoVO.setRecords(mapList);
            log.info("行业查询告警列表出参：{}", JSON.toJSONString(warningOrderInfoVO));
            return Result.data(warningOrderInfoVO);
        }
        catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }


    /**
     * 功能描述：转领导批示
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:10
     * @version:
     */
    @ApiOperation(value = "转领导批示", notes = "转领导批示")
    @TowerLog(title = "转领导批示", businessType = BusinessType.INSERT)
    @PostMapping("/alarm/orderForTrade/addLeaderAudit")
    public Result addLeaderAudit(@RequestBody TradeOrderLeaderAuditDTO dto) {
        log.info("行业转领导批示入参：{}", JSON.toJSONString(dto));
        try{
            OrderLeaderAuditDTO orderLeaderAuditDTO = new OrderLeaderAuditDTO();
            BeanUtils.copyProperties(dto, orderLeaderAuditDTO);
            return orderLeaderAuditRepository.addLeaderAudit(orderLeaderAuditDTO);
        }catch(Exception e){
            log.error("行业转领导批示异常,{}",e);
            return Result.fail("行业转领导批示异常");
        }
    }

    /**
     * 功能描述：行业调用流程流转
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:10
     * @version:
     */
    @ApiOperation(value = "行业调用流程流转", notes = "行业调用流程流转")
    @TowerLog(title = "行业调用流程流转", businessType = BusinessType.UPDATE)
    @PostMapping("/alarm/orderForTrade/submitProcess")
    public Result submitProcess(@RequestBody TradeSendAlarmOrderDTO dto) {
        log.info("行业调用流程流转入参：{}", JSON.toJSONString(dto));
        try{
            SendAlarmOrderDTO sendAlarmOrderDTO = new SendAlarmOrderDTO();
            BeanUtils.copyProperties(dto, sendAlarmOrderDTO);
            if (!StringUtils.isEmpty(dto.getTimeLimit())) {
                sendAlarmOrderDTO.setTimeLimit(Integer.valueOf(dto.getTimeLimit()));
            }
            return warningOrderForTradeRepository.submitProcess(sendAlarmOrderDTO);
        }catch(Exception e){
            log.error("行业调用流程流转异常,{}",e);
            return Result.fail("行业调用流程流转异常");
        }
    }

    /**
     * 功能描述：行业调用转派接口
     *
     * @param dto
     * @return Result
     * @author songxinfeng
     * @date 2022/11/17 14:10
     * @version:
     */
    @ApiOperation(value = "行业调用转派接口", notes = "行业调用转派接口")
    @PostMapping("/alarm/orderForTrade/reassignment")
    public Result reassignment(@RequestBody TradeReassignmenOrdertDto dto) {
        log.info("行业调用流程转派接口入参:{}", JSON.toJSONString(dto));
        return warningOrderRepository.reassignment(dto);
    }

    /**
     * 功能描述：行业调用查询可转派环节接口
     *
     * @param dto
     * @return Result
     * @author songxinfeng
     * @date 2022/11/17 14:10
     * @version:
     */
    @ApiOperation(value = "行业调用查询可转派环节接口", notes = "行业调用查询可转派环节接口")
    @PostMapping("/alarm/orderForTrade/queryReassignLink")
    public Result queryReassignLink(@RequestBody TradeQueryReassignLinkDto dto) {
        log.info("行业调用流程查询可转派环节流转入参:{}", JSON.toJSONString(dto));
        FlowLinkAttrModel model = new FlowLinkAttrModel();
        model.setLinkId(Long.valueOf(dto.getCurLink()));
        model.setCurLink(Long.valueOf(dto.getCurLink()));
        model.setAlarmFlowId(Long.valueOf(dto.getAlarmFlowId()));
        model.setAppVerCode(dto.getAppVerCode());
        model.setIndustryCode(dto.getIndustryCode());
        model.setTemplateId(Long.valueOf(dto.getTemplateId()));
        model.setWarningSource(dto.getWarningSource());
        return flowLinkAttrService.queryReassignLinkNew(model);
    }

    /**
     * 功能描述：新增/取消收藏
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:10
     * @version:
     */
    @ApiOperation(value = "新增/取消收藏", notes = "新增/取消收藏")
    @PostMapping(value = "/alarm/orderForTrade/warningOrderCollection")
    public Result warningOrderCollection(@RequestBody WarningOrderCollectionDTO dto) {
        log.info("行业新增/取消收藏入参：{}", JSON.toJSONString(dto));
        WarningOrderCollectionDO warningOrderCollectionDO = new WarningOrderCollectionDO();
        BeanUtils.copyProperties(dto, warningOrderCollectionDO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        warningOrderCollectionDO.setCollectionCreator(sysService.getLoginUserId());
        try {
            //新增收藏
            if (Constants.YES_FLAG.equals(dto.getOperationType())) {
                if (StringUtils.isEmpty(warningOrderCollectionDO.getAlarmTime())) {
                    return Result.fail("alarmTime不能为空");
                }
                warningOrderCollectionRepository.addWarningOrderCollection(warningOrderCollectionDO);
            }
            //取消收藏
            else if (Constants.VALID_STATE.equals(dto.getOperationType())) {
                warningOrderCollectionRepository.delWarningOrderCollection(warningOrderCollectionDO);
            }
            else {
                return Result.fail("操作类型异常！");
            }
        }
        catch (Exception e) {
            log.error("新增/取消收藏失败：{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 功能描述：查询收藏列表
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:10
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询收藏列表", notes = "查询收藏列表")
    @PostMapping("/alarm/orderForTrade/qryWarningOrderCollection")
    public Result qryWarningOrderCollection(@RequestBody TradeAlarmOrderDTO dto) {
        log.info("行业查询收藏列表入参：{}", JSON.toJSONString(dto));
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto, alarmOrderDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        alarmOrderDTO.setCollectionCreator(sysService.getLoginUserId());
        alarmOrderDTO.setRoleIds(loginUser.getUser().getRoleIds());
        alarmOrderDTO.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        startPage();
        List<WarningOrderListVO> list = warningOrderCollectionRepository.qryWarningOrderCollection(alarmOrderDTO);
        if (CollectionUtils.isEmpty(list)) {
            return Result.fail("未查询到收藏数据");
        }
        return result(list);
    }

    /**
     * 功能描述：查询收藏列表
     *
     * @param req
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:10
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询收藏列表", notes = "查询收藏列表")
    @PostMapping("/alarm/orderForTrade/qryWarningOrderCollectionImgs")
    public Result qryWarningOrderCollectionImgs(@RequestBody AlarmDownloadReq req) {
        log.info("行业查询收藏列表入参：{}", JSON.toJSONString(req));
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        BeanUtil.copyProperties(req.getDownLoadOrderDTO(),alarmOrderDTO);
        alarmOrderDTO.setPageSize(50);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        alarmOrderDTO.setCollectionCreator(sysService.getLoginUserId());
        alarmOrderDTO.setRoleIds(loginUser.getUser().getRoleIds());
        alarmOrderDTO.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        startPage();
        alarmOrderDTO.setPageSize(50);
        List<WarningOrderListVO> list = warningOrderCollectionRepository.qryWarningOrderCollection(alarmOrderDTO);
        if (CollectionUtils.isEmpty(list)) {
            return Result.fail("未查询到收藏数据");
        }
        return result(list);
    }

    /**
     * 功能描述：查询订购关联的告警类型
     *
     * @param body
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:13
     * @version:
     */
    @ApiOperation(value = "查询订购关联的告警类型", notes = "查询订购关联的告警类型")
    @PostMapping("/alarm/orderForTrade/findOrderAlarm")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, typeValueColumn = "sat.type_value")
    public Result findOrderAlarm(@RequestBody AlarmTypeDTO body) {
        int pageNum = NumberUtils.toInt(String.valueOf(body.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(body.getPageSize()), 0);
        List<AlarmTypeDO> list = alarmTypeRepository.findOrderAlarm(body);
        if (pageNum == 0 && pageSize == 0) {
            return Result.data(list, "");
        }
        else {
            PageInfo<Map<String, Object>> pageInfo = new PageInfo(list);
            return Result.data(pageInfo, "");
        }
    }

    /**
     * 功能描述：查询重复告警
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:19
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询重复告警", notes = "查询重复告警")
    @PostMapping("/alarm/orderForTrade/getOrderListForDuplication")
    public TableDataInfo getOrderListForDuplication(@RequestBody AlarmOrderDuplicateDTO dto) {
        AlarmOrderDuplicateDTO alarmOrderDuplicateDTO = new AlarmOrderDuplicateDTO();
        BeanUtils.copyProperties(dto, alarmOrderDuplicateDTO);
        log.info("行业查询重复告警入参：{}", JSON.toJSONString(alarmOrderDuplicateDTO));
        return warningOrderRepository.getOrderListForDuplication(alarmOrderDuplicateDTO);
    }

    /**
     * 功能描述：流程回退
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:19
     * @version:
     */
    @ApiOperation(value = "流程回退", notes = "流程回退")
    @TowerLog(title = "流程回退", businessType = BusinessType.UPDATE)
    @PostMapping("/alarm/orderForTrade/rollBack")
    public Result rollBack(@RequestBody TradeSendAlarmOrderDTO dto) {
        log.info("行业流程回退入参：{}", JSON.toJSONString(dto));
        SendAlarmOrderDTO sendAlarmOrderDTO = new SendAlarmOrderDTO();
        BeanUtils.copyProperties(dto, sendAlarmOrderDTO);
        sendAlarmOrderDTO.setOrderId(dto.getWarningOrderId());
        if(!StringUtils.isEmpty(dto.getTimeLimit())){
            sendAlarmOrderDTO.setTimeLimit(Integer.valueOf(dto.getTimeLimit()));
        }
        return processOrderRepository.rollBack(sendAlarmOrderDTO);
    }

    /**
     * 功能描述：获取流程流转过程数据
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:19
     * @version:
     */
    @ApiOperation(value = "获取流程流转过程数据", notes = "获取流程流转过程数据")
    @PostMapping("/alarm/orderForTrade/getRecordByType")
    public Result getRecordByType(@RequestBody TradeSendAlarmOrderDTO dto) {
        log.info("行业获取流程流转过程数据入参：{}", JSON.toJSONString(dto));
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setOrderId(dto.getWarningOrderId());
        alarmOrderDTO.setRealLinkType(dto.getRealLinkType());
        alarmOrderDTO.setLinkId(dto.getLinkId());
        List<FlowRecordVO> list = warningOrderRepository.getRecordByType(alarmOrderDTO);
        return Result.data(list);
    }

    /**
     * 功能描述：查询领导批示用户数据
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:20
     * @version:
     */
    @ApiOperation(value = "查询领导批示用户数据", notes = "查询领导批示用户数据")
    @PostMapping("/alarm/orderForTrade/getLeaderByTenantId")
    public Result getLeaderByTenantId(@RequestBody SysUserDTO dto) {
        log.info("行业查询领导批示用户数据入参：{}", JSON.toJSONString(dto));
        List<SysUserVO> sysUserVOList = deviceRepository.getLeaderByTenantId(dto);
        return Result.data(sysUserVOList);
    }

    /**
     * 功能描述：查询告警图层
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:20
     * @version:
     */
    @EventObsEncrypt
    @ApiOperation(value = "查询告警图层", notes = "查询告警图层")
    @PostMapping("/alarm/orderForTrade/getWarningOrderLayer")
    //@IndustryAuth
    public Result getWarningOrderLayer(@RequestBody WarningOrderLayerDTO dto) {
        log.info("行业查询告警图层入参：{}", JSON.toJSONString(dto));
        return warningOrderForTradeRepository.getWarningOrderLayer(dto);
    }


    /**
     * 功能描述：查询告警图层（不需要文件加密）
     *
     * @version:
     */
    @ApiOperation(value = "查询告警图层", notes = "查询告警图层")
    @PostMapping("/alarm/orderForTrade/getWarningOrderLayerNoFile")
    //@IndustryAuth
    public Result getWarningOrderLayerNoFile(@RequestBody WarningOrderLayerDTO dto) {
        log.info("行业查询不需要告警文件得告警图层入参：{}", JSON.toJSONString(dto));
        return warningOrderForTradeRepository.getWarningOrderLayer(dto);
    }

    /**
     * 功能描述：修改告警事件信息
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:21
     * @version:
     */
    @ApiOperation(value = "修改告警事件信息", notes = "修改告警事件信息")
    @TowerLog(title = "修改告警事件信息", businessType = BusinessType.UPDATE)
    @PostMapping("/alarm/orderForTrade/modifyWarningOrderInfo")
    public Result modifyWarningOrderInfo(@RequestBody WarningOrderDTO dto) {
        log.info("行业修改告警事件信息入参：{}", JSON.toJSONString(dto));
        return warningOrderForTradeRepository.modifyWarningOrderInfo(dto);
    }

    /**
     * 功能描述：查询关联告警事件信息
     *
     * @param dto
     * @return Result<List<WarningOrderInfoVO>>
     * @author zhen.yan
     * @date 2022/6/15 14:21
     * @version:
     */
    //@IndustryAuth
    @EventObsEncrypt
    @ApiOperation(value = "查询关联告警事件信息", notes = "查询关联告警事件信息")
    @PostMapping("/alarm/orderForTrade/warningOrderInfoRel")
    public Result<List<WarningOrderInfoVO>> warningOrderInfoRel(@RequestBody WarningOrderRelDTO dto) throws Exception {
        log.info("行业查询关联告警事件信息入参：{}", JSON.toJSONString(dto));
        return warningOrderForTradeRepository.warningOrderInfoRel(dto);
    }

    /**
     * 功能描述：生成告警压制
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:21
     * @version:
     */
    @TowerLog(title = "生成告警压制", businessType = BusinessType.INSERT)
    @PostMapping("/alarm/orderForTrade/createIntercept")
    public Result createIntercept(@RequestBody InterceptConfigDTO dto) {
        log.info("生成告警压制入参:{}", JSON.toJSONString(dto));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        SysUser sysUser = loginUser.getUser();
        if (StringUtils.isEmpty(dto.getDeviceCode())) {
            return Result.fail("deviceCode不能为空！");
        }
        if (StringUtils.isEmpty(dto.getWarningTypeId())) {
            return Result.fail("warningTypeId不能为空！");
        }
        if (0 >= dto.getInterceptTime()) {
            return Result.fail("interceptTime不能为空！");
        }
        else if (43200 < dto.getInterceptTime()) {
            return Result.fail("压制时间不能超过30天！");
        }
//        if (CollectionUtils.isEmpty(dto.getAppVerCodeList())) {
//            return Result.fail("appVerCodeList不能为空！");
//        }
        String key = Constants.INTERCEPT_CONFIG + sysUser.getTenantId() + "_" + sysUser.getIndustryCode() + "_" + dto.getDeviceCode() + "_" + dto.getWarningTypeId();
        RedisUtil.set(key, dto, RedisDBType.PRIMARY);
        RedisUtil.expire(key, dto.getInterceptTime(), TimeUnit.MINUTES, RedisDBType.PRIMARY);
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 功能描述：查询用户代办事项数量
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:22
     * @version:
     */
    @PostMapping("/alarm/orderForTrade/getTodoNum")
    public Result getTodoNum(@RequestBody AppOrderListByUserDTO dto) {
        log.info("行业查询用户代办事项数量入参：{}", JSON.toJSONString(dto));
        com.chinatower.event.dto.AppOrderListByUserDTO appOrderListByUserDTO = new com.chinatower.event.dto.AppOrderListByUserDTO();
        BeanUtils.copyProperties(dto, appOrderListByUserDTO);
        return warningOrderRepository.getTodoNum(appOrderListByUserDTO);
    }

    /**
     * 功能描述：批量关闭告警
     *
     * @param dto
     * @return Result
     * @author zhang.xionghui
     * @date 2022/09/16
     * @version:
     */
    @PostMapping("/alarm/orderForTrade/batchEndWarningOrder")
    public Result batchEndWarningOrder(@Validated @RequestBody BatchEndWarningOrderDTO dto) {
        log.info("行业批量关闭告警信息入参：{}", JSON.toJSONString(dto));
        return warningOrderBatchRepository.batchEndWarningOrder(dto);
    }

    /**
     * 功能描述：查询领导批示用户数据(根据组织节点展示树状数据)
     *
     * @param model
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:36
     * @version:
     */
    @PostMapping("/alarm/orderForTrade/getLeaderByTenantIdShowTree")
    @EventObsEncrypt
    public Result getLeaderByTenantIdShowTree(@RequestBody ApiLeaderByTenantIdTreeReq model) {
        log.info("行业-getLeaderByTenantIdShowTree-req: {}", JSON.toJSONString(model));
        LeaderByTenantIdTreeReq dto = new LeaderByTenantIdTreeReq();
        BeanUtil.copyProperties(model,dto);
        List<SysInfo> sysUserVOList = deviceRepository.getLeaderByTenantIdShowTree(dto);
        log.info("行业-getLeaderByTenantIdShowTree-resp: {}", JSON.toJSONString(sysUserVOList));
        return Result.data(sysUserVOList);
    }

    /**
     * 功能描述：行业获取当前工单最后一个处理过环节的流程处理人
     *
     * @param model
     * @return Result
     * @version:
     */
    @PostMapping("/alarm/orderForTrade/queryLastProcess")
    public Result queryLastProcess(@RequestBody ApiSelectLastRrocessReq model) {
        log.info("行业queryLastProcess:获取最后一个环节处理人接口入参：{}", JSON.toJSONString(model));
        SelectLastRrocessReq dto = new SelectLastRrocessReq();
        BeanUtil.copyProperties(model,dto);
        return orderFlowRecordRepository.queryLastProcess(dto);
    }

    /**
     * 功能描述：根据流程ID，租户ID获取用户树
     *
     * @param model
     * @return Result<SysInfo>
     * @author zhang.xionghui
     * @date 2022/9/19 10:00
     * @version:
     */
    @PostMapping("/alarm/orderForTrade/getFlowByIdUserTree")
    public Result<SysInfoVo> getFlowByIdUserTree(@RequestBody FlowLinkAttrModelDTO model) {
        log.info("行业查询获取用户树信息入参：{}", JSON.toJSONString(model));

        //接口传入的flowId实际是alarmFlowId
        model.setAlarmFlowId(model.getFlowId());
        model.setFlowId(null);
        FlowLinkAttrModel flowLinkAttrModel = new FlowLinkAttrModel();
        BeanUtil.copyProperties(model,flowLinkAttrModel);
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FORTRADE_FLAG);

        //默认开关为0，不调用，1调用
        String flag =Constants.ZERO;
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        //行业调用权限开关打开或者orderId不为空均会调用华苏权限接口过滤用户
        if(Constants.ONE.equals(flag) || !StringUtils.isEmpty(model.getWarningOrderId())){
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + model.getWarningOrderId().substring(0, 6);
            AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
            alarmOrderDTO.setOrderId(model.getWarningOrderId());
            alarmOrderDTO.setTableName(alarmTableName);
            WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderDTO);
            if(warnOrderStatusDTO != null){
                flowLinkAttrModel.setCityId(warnOrderStatusDTO.getCityId());
                flowLinkAttrModel.setCountyId(warnOrderStatusDTO.getCountyId());
                flowLinkAttrModel.setProvinceId(warnOrderStatusDTO.getProvinceId());
                flowLinkAttrModel.setWarningSource(warnOrderStatusDTO.getWarningSource());
                flowLinkAttrModel.setDeviceCode(warnOrderStatusDTO.getDeviceCode());
                flowLinkAttrModel.setTenantId(warnOrderStatusDTO.getTenantId());
                flowLinkAttrModel.setGridId(warnOrderStatusDTO.getGridId());
            }
            //行业调用权限开关开，调用权限接口
            flowLinkAttrModel.setStatusFlag(Constants.ZERO);
        }else{
            //行业调用权限开关关，不调用权限接口
            flowLinkAttrModel.setStatusFlag(Constants.ONE);
        }
        return flowLinkAttrService.getFlowByIdUserTreeNew(flowLinkAttrModel);
    }

    /**
     * 功能描述：根据流程ID，租户ID获取用户树
     *
     * @param model
     * @return Result<SysInfo>
     * @author zhang.xionghui
     * @date 2022/9/19 10:00
     * @version:
     */
    @PostMapping("/alarm/orderForTrade/getFlowByIdUserTreeNew")
    public Result<SysInfoVo> getFlowByIdUserTreeNew(@RequestBody FlowLinkAttrModel model) {
        //接口传入的flowId实际是alarmFlowId
        model.setAlarmFlowId(model.getFlowId());
        model.setFlowId(null);
        return flowLinkAttrService.getFlowByIdUserTree(model);
    }

    /**
     * 功能描述：获取处置单详情
     * @param dto
     * @return Result
     * @author cui.sj
     * @date 2022/10/31 13:39
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/alarm/orderForTrade/getProcessDetail")
    public Result getProcessDetail(@RequestBody ProcessOrderDTO dto) {
        log.info("行业查询处置单详情入参：{}", JSON.toJSONString(dto));
        return processOrderRepository.getProcessDetail(dto);
    }


    /**
     * 功能描述：人工上报-行业
     * 处置
     * @param receiveAlarmVO
     * @return Result
     */
    @EventObsEncrypt
    @PostMapping("/alarm/orderForTrade/addAlarmOrder")
    public Result addAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO) {
        log.info("行业新增人工上报入参：{}", JSON.toJSONString(receiveAlarmVO));
        return iReceiveAlarmNewService.addAlarmOrder(receiveAlarmVO);
    }

    /**
     * 功能描述：获取卡口告警详情
     * @param dto
     * @return
     */
    @PostMapping("/alarm/orderForTrade/getCheckpointAlarmInfo")
    public Result getCheckpointAlarmInfo(@RequestBody CheckpointAlarmDTO dto) {
        List<CheckAlarmByDeviceVO> list = checkpointAlarmRepository.getCheckpointAlarmInfo(dto);
        return Result.data(list);
    }

}

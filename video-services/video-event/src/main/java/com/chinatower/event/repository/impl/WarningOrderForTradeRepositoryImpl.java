package com.chinatower.event.repository.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.constant.AlarmConstants;
import com.chinatower.event.core.alarm.util.SystemUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.ThreadDto.WarningRelResultDto;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mapper.WarningOrderCollectionMapper;
import com.chinatower.event.service.OperateService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.DistanceUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.dto.WarnOrderStatusDTO;
import com.chinatower.event.lntlagservice.AngleUtils;
import com.chinatower.event.mapper.AlarmOperateMapper;
import com.chinatower.event.mapper.OrderFlowRecordMapper;
import com.chinatower.event.mapper.WarningOrderForTradeMapper;
import com.chinatower.event.repository.IAlarmOperateRepository;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IWarningOrderForTradeRepository;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.event.vo.AlarmOrderRecordVO;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.resp.SysDictDataResponse;

import lombok.extern.slf4j.Slf4j;

import static com.chinatower.event.constants.Constants.ALARM_LEVEL;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderForTradeRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/24 10:21
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class WarningOrderForTradeRepositoryImpl implements IWarningOrderForTradeRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private IAlarmOperateRepository alarmOperateRepository;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private WarningOrderForTradeMapper warningOrderForTradeMapper;

    @Autowired
    private WarningOrderCollectionMapper warningOrderCollectionMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private IVideoDeviceService videoDeviceService;

    @Autowired
    private SystemUtil systemUtil;

    @Autowired
    private OperateService operateService;
    /**
     * 行业调用流程流转
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitProcess(SendAlarmOrderDTO dto) {
        log.info("行业调用流程流转入参：" + JSON.toJSONString(dto));
        try {
            //兼容未更新版本的行业默认流程
            if(StringUtils.isEmpty(dto.getRealLinkType()) && Constants.REAL_LINK_TYPE_ALL.contains(dto.getLinkId())){
                dto.setRealLinkType(dto.getLinkId());
            }
            if(StringUtils.isEmpty(dto.getRealLinkType())){
                return Result.fail(Constants.MESS_4);
            }
            //查询订单是否存在
            String orderId = dto.getWarningOrderId();
            dto.setOrderId(orderId);
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
            String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + orderId.substring(0, 6);
            WarnOrderStatusDTO warnOrderStatusDTO = warningOrderRepository.checkOrderStatus(orderId, alarmTableName,null, dto.getCurrentLink());
            if (null == warnOrderStatusDTO) {
                return Result.fail(Constants.MESS_3);
            }
            // 校验环节表:判断工单流程环节是否在当前处理环节
            AlarmOrderRecordVO alarmOrderRecordVO = new AlarmOrderRecordVO();
            alarmOrderRecordVO.setOrderId(orderId);
            alarmOrderRecordVO.setIfCurrentLink(Constants.YES_FLAG);
            //处理环节
            alarmOrderRecordVO.setLinkId(dto.getCurrentLink());
            alarmOrderRecordVO.setTableName(recordTableName);
            List<AlarmOrderRecordVO> recordVOList = orderFlowRecordMapper.getAlarmOrderRecordInfoNew(alarmOrderRecordVO);
            if (CollectionUtils.isEmpty(recordVOList)) {
                return Result.fail(Constants.MESS_FLOW_RECORD);
            }
            //查询当前环节是否是研判环节，是的话做如下判断
            if(Constants.ONE.equals(recordVOList.get(0).getRealLinkType())){
                dto.setJudgeResult(Constants.JUDGE_RESULT_1);
                //误报重复告警linkId与realLinkType值一样
                if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(dto.getLinkId())) {
                    dto.setLinkName(EnumUtil.FlowNode.MISINFORMATION.getName());
                    dto.setJudgeResult(Constants.JUDGE_RESULT_0);
                }
                if (EnumUtil.FlowNode.DUPLICATION.getCode().equals(dto.getLinkId())) {
                    dto.setLinkName(EnumUtil.FlowNode.DUPLICATION.getName());
                    dto.setJudgeResult(Constants.JUDGE_RESULT_2);
                }
            }
            if (StringUtils.isEmpty(dto.getFlowId())) {
                dto.setFlowId(warnOrderStatusDTO.getFlowId());
            }
            //下一环节ID
            dto.setOrderStatus(dto.getRealLinkType());
            // 20210621 start
            if (StringUtils.isEmpty(dto.getWarningTypeId())) {
                dto.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
            }
            dto.setTenantId(warnOrderStatusDTO.getTenantId());
            // 20210621 end
            dto.setAlarmBody(warnOrderStatusDTO.getAlarmBody());
            dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
            if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
                dto.setOrgRecord(String.valueOf(warnOrderStatusDTO.getOrgRecord()));
            }
            // 从redis获取行政区划列表
            List<String> list = Arrays.asList(dto.getProvinceId(), dto.getCityId(), dto.getCountyId());
            Map<String, String> areaRedisMap = deviceRepository.getAreaNameCaffeineMap(list);
            if(MapUtils.isNotEmpty(areaRedisMap)){
                dto.setProvinceName(MapUtils.getString(areaRedisMap, dto.getProvinceId(), ""));
                dto.setCityName(MapUtils.getString(areaRedisMap, dto.getCityId(), ""));
                dto.setCountyName(MapUtils.getString(areaRedisMap, dto.getCountyId(), ""));
            }
            dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
            //判断是否运营研判，如果是并行且为真警更新运营研判数据
            //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(orderId);
            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId,null);
            if (null != alarmOperateDTO
                    && Constants.TWO_FLAG.equals(alarmOperateDTO.getOperateType())
                    && Constants.JUDGE_RESULT_3.equals(alarmOperateDTO.getJudgeResult())) {
                // 运营研判为并行并且待研判状态
                String originalJson = JSON.toJSONString(dto);
                Map param = JSON.parseObject(originalJson, Map.class);
                param.put(Constants.PARAM_WARNING_ORDER_ID, orderId);
                // 来源：客户
                param.put(Constants.PARAM_SOURCE, Constants.YES_FLAG);
                if (!org.springframework.util.StringUtils.isEmpty(dto.getEmergencyLevel())) {
                    List<SysDictDataResponse> emergencyLevelEnum = warningOrderRepository.getEnumList(Constants.EMERGENCY_LEVEL);
                    if (!CollectionUtils.isEmpty(emergencyLevelEnum)) {
                        emergencyLevelEnum.forEach(item -> {
                            if (item.getDictValue().equals(param.get(Constants.PARAM_EMERGENCY_LEVEL))) {
                                param.put(Constants.PARAM_EMERGENCY_LEVEL_NAME, item.getDictLabel());
                            }
                        });
                    }
                }
                if (!org.springframework.util.StringUtils.isEmpty(dto.getWarningTypeId())) {
                    List<Map> alramTypeList = warningOrderRepository.getAlarmTypeList();
                    if (!CollectionUtils.isEmpty(alramTypeList)) {
                        alramTypeList.forEach(item -> {
                            if (item.get(Constants.PARAM_TYPE_VALUE_).toString()
                                    .equals(param.get(Constants.PARAM_WARNING_TYPE_ID))) {
                                param.put(Constants.PARAM_WARNING_TYPE_NAME, item.get(Constants.PARAM_TYPENAME));
                            }
                        });
                    }
                }
                Result result = alarmOperateRepository.checkAlarmOrder(param);
                if (ResultCode.FAILURE.getCode() == result.getCode()) {
                    log.error(Constants.PROCESS_FLOW_FAIL + result.getData());
                    return Result.fail(ResultCode.ERROR, Constants.PROCESS_FLOW_FAIL + result.getData());
                }
            }
            return warningOrderRepository.updateAlarmOrder(dto);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(Constants.PROCESS_FLOW_FAIL + e.getMessage());
            return Result.fail(ResultCode.ERROR, Constants.PROCESS_FLOW_FAIL + e.getMessage());
        }
    }

    /**
     * 查询告警图层
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,11,12",areaPro = "dataTable.province_code"
            ,areaCity = "dataTable.city_code",areaCode = "dataTable.county_code",processIdColumn = "dataTable.process_id",typeValueColumn = "dataTable.warning_type_id")
    public Result getWarningOrderLayer(WarningOrderLayerDTO dto) {

        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        // 当前表查询开始的数
        int tableStartNum = 0;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());
        }
        else {
            startDate = dto.getAlarmTimeStart();
            int sd = Integer.parseInt(DateUtils.dateToStringYM(startDate));
            if (sd < Integer.parseInt(TableUtil.getMinTableDate())) {
                startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());
            }
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        }
        else {
            endDate = dto.getAlarmTimeEnd();
        }

        List<WarningOrderListVO> resultList = new ArrayList<>();
        // 防止时间传反，导致gc
        if(ObjectUtil.isNotNull(dto.getAlarmTimeStart()) && ObjectUtil.isNotNull(dto.getAlarmTimeEnd()) && dto.getAlarmTimeStart().after(dto.getAlarmTimeEnd())) return Result.data(resultList);

        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) &&dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        // 告警等级枚举
        List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(ALARM_LEVEL).getData();
        int errCount = 1;
        boolean stop = false;
        final int limitCount = dto.getLimitCount();
        while (!stop) {
            try {
                int resultSize = org.apache.commons.collections4.CollectionUtils.size(resultList);
                if (limitCount > 0) {
                    if(resultSize >= limitCount){
                        break;
                    }
                    dto.setLimitCount(limitCount - resultSize);
                }
                String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                dto.setTableName(tableName);
                List<WarningOrderListVO> queryResult = warningOrderForTradeMapper.getWarningOrderLayer(dto);
                // 设置重要等级名称
                queryResult.forEach(e -> {
                    if (!CollectionUtils.isEmpty(importanceLevelEnum)) {
                        importanceLevelEnum.forEach(item -> {
                            if (item.getDictValue().equals(e.getImportanceLevel())) {
                                e.setImportanceLevelName(item.getDictLabel());
                            }
                        });
                    }
                });
                resultList.addAll(queryResult);
                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
            catch (Exception e) {
                log.error("查询告警事件列表错误：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if(errCount > 3){
                    return Result.data(resultList);
                }

                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
        }
        return Result.data(resultList);
    }

    /**
     * 修改告警事件信息
     *
     * @param dto
     * @return
     */
    @Override
    public Result modifyWarningOrderInfo(WarningOrderDTO dto) {
        log.info("调用修改告警事件信息接口入参:{}", JSON.toJSONString(dto));
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == loginUser || null == loginUser.getUser()) {
                return Result.fail(ResultCode.CURRENT_USER_FAIL);
            }
            if (StringUtils.isEmpty(dto.getWarningOrderId())) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), ResultCode.GLOBAL_PARAM_ERROR.getMsg() + ":warningOrderId不能为空!");
            }
            if (StringUtils.isEmpty(dto.getTenantId()) || !dto.getTenantId().equals(loginUser.getUser().getTenantId())) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), ResultCode.GLOBAL_PARAM_ERROR.getMsg() + ":tenantId为空或与登入信息不符!");
            }
            if (StringUtils.isEmpty(dto.getAppVerCode()) || !dto.getAppVerCode().equals(loginUser.getUser().getAppVerCode())) {
                return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), ResultCode.GLOBAL_PARAM_ERROR.getMsg() + ":appVerCode为空或与登入信息不符!");
            }
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + dto.getWarningOrderId().substring(0, 6);
            dto.setTableName(alarmTableName);
            warningOrderForTradeMapper.updateWarningOrderForTrade(dto);
        }
        catch (Exception e) {
            return Result.fail("调用修改告警事件信息接口失败" + e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 查询关联告警事件信息
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="a.device_code",tenantColumn="a.tenant_id",
            industryCode="a.industry_code", alarmSourceColumn="a.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,11,12",areaPro = "a.province_code"
            ,areaCity = "a.city_code",areaCode = "a.county_code",typeValueColumn = "a.warning_type_id")
    public Result<List<WarningOrderInfoVO>> warningOrderInfoRel(WarningOrderRelDTO dto) throws Exception {

        List<WarningOrderInfoVO> warningOrderInfoVOList = new ArrayList<>();
        if (StringUtils.isEmpty(dto.getDuplicateOrderId())) {
            return Result.fail("重复告警ID不能为空");
        }

        // 有传userid,使用行业传入userid, 否则用户登入信息中获取
        if (StringUtils.isEmpty(dto.getUserId())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            dto.setUserId(loginUser.getUser().getUserId());
        }
        //查询当前用户收藏事件
        AlarmOrderDTO orderDTO = new AlarmOrderDTO();
        orderDTO.setCollectionCreator(dto.getUserId());
        orderDTO.setSort(dto.getSort());
        orderDTO.setSortName(dto.getSortName());
        List<WarningOrderListVO> warningOrderCollectionList = warningOrderCollectionMapper.qryWarningOrderCollection(orderDTO);
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            Calendar calendar = Calendar.getInstance();
            //得到前30天
            calendar.add(Calendar.DATE, -30);
            startDate = calendar.getTime();
        }
        else {
            startDate = dto.getAlarmTimeStart();
            int sd = Integer.parseInt(DateUtils.dateToStringYM(startDate));
            if (sd < Integer.parseInt(TableUtil.getMinTableDate())) {
                startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());
            }
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        }
        else {
            endDate = dto.getAlarmTimeEnd();
        }
        // 防止时间传反，导致gc
        if(ObjectUtil.isNotNull(dto.getAlarmTimeStart()) && ObjectUtil.isNotNull(dto.getAlarmTimeEnd()) && dto.getAlarmTimeStart().after(dto.getAlarmTimeEnd())) return Result.data(warningOrderInfoVOList);
        int errCount = 1;
        boolean stop = false;
        List<OrderInfoVO> resultList = new ArrayList<>();
        // 从最后一个表开始向前获取
        while (!stop) {
            try {
                String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);

                dto.setTableName(tableName);
                List<OrderInfoVO> queryList = warningOrderForTradeMapper.warningOrderInfoRel(dto);
                resultList.addAll(queryList);
                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
            catch (Exception e) {
                log.error("查询告警事件列表错误：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if(errCount > 3){
                    return Result.data(warningOrderInfoVOList);
                }

                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
        }
        if (!CollectionUtils.isEmpty(resultList)) {
            // 告警类型名称
            List<Map> alramTypeList = warningOrderRepository.getAlarmTypeList();
            List<String> paramList = new ArrayList<>();
            paramList.add(Constants.ALARM_LEVEL);
            paramList.add(String.valueOf(Constants.MODEL_ID));
            paramList.add(Constants.IOT_MODEL_ID);
            paramList.add(String.valueOf(Constants.MSG_TYPE));
            paramList.add(String.valueOf(Constants.NOTICE_RESOURCE));
            paramList.add(String.valueOf(Constants.EMERGENCY_LEVEL));
            paramList.add(String.valueOf(Constants.ORDER_STATUS));
            paramList.add(Constants.WARNING_SOURCE_COMMON);
            // 告警重要等级名称
            AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
            //摄像机厂商
            AtomicReference<List<SysDictDataResponse>> modelIdEnum = new AtomicReference<>();
            // 物联设备厂商
            AtomicReference<List<SysDictDataResponse>> iotModelIdEnum = new AtomicReference<>();
            // 消息类型
            AtomicReference<List<SysDictDataResponse>> msgTypeEnum = new AtomicReference<>();
            // 通知类型
            AtomicReference<List<SysDictDataResponse>> noticeResourceEnum = new AtomicReference<>();
            // 告警等级名称
            AtomicReference<List<SysDictDataResponse>> emergencyLevelEnum = new AtomicReference<>();
            // 告警状态名称
            AtomicReference<List<SysDictDataResponse>> orderStatusEnum = new AtomicReference<>();
            // 告警来源枚举
            AtomicReference<List<SysDictDataResponse>> warningSourceEnum = new AtomicReference<>();
            ThreadParallelUtil.run(paramList,o->{
                if(Constants.ALARM_LEVEL.equals(String.valueOf(o))){
                    importanceLevelEnum.set(systemUtil.getEnum(o));
                }else if(String.valueOf(Constants.MODEL_ID).equals(String.valueOf(o))){
                    modelIdEnum.set(systemUtil.getEnum(o));
                }else if(Constants.IOT_MODEL_ID.equals(String.valueOf(o))){
                    iotModelIdEnum.set(systemUtil.getEnum(o));
                }else if(String.valueOf(Constants.MSG_TYPE).equals(String.valueOf(o))){
                    msgTypeEnum.set(systemUtil.getEnum(o));
                }else if(String.valueOf(Constants.NOTICE_RESOURCE).equals(String.valueOf(o))){
                    noticeResourceEnum.set(systemUtil.getEnum(o));
                }else if(String.valueOf(Constants.EMERGENCY_LEVEL).equals(String.valueOf(o))){
                    emergencyLevelEnum.set(systemUtil.getEnum(o));
                }else if(String.valueOf(Constants.ORDER_STATUS).equals(String.valueOf(o))){
                    orderStatusEnum.set(systemUtil.getEnum(o));
                }else if(Constants.WARNING_SOURCE_COMMON.equals(String.valueOf(o))){
                    warningSourceEnum.set(systemUtil.getEnum(o));
                }
                return null;
            }, ThreadParallelUtil.ConfigFactory.WARNING_CODE);

            List<String> deviceCodeList = new ArrayList<>();
            List<DeviceSiteVO> deviceSiteVOList = new ArrayList<>();
            resultList.forEach(info -> {
                if (StringUtils.isNotEmpty(info.getDeviceCode())) {
                    deviceCodeList.add(info.getDeviceCode());
                }
            });
            deviceCodeList.stream().distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                deviceSiteVOList = deviceRepository.queryDeviceList(deviceCodeList);
            }
            List<WarningRelResultDto> resultDtoList = new ArrayList<>();
            for (OrderInfoVO vo : resultList){
                WarningRelResultDto resultDto = new WarningRelResultDto();
                resultDto.setDto(dto);
                resultDto.setWarningOrderInfoVOList(warningOrderInfoVOList);
                resultDto.setWarningOrderCollectionList(warningOrderCollectionList);
                resultDto.setImportanceLevelEnum(importanceLevelEnum.get());
                resultDto.setModelIdEnum(modelIdEnum.get());
                resultDto.setIotModelIdEnum(iotModelIdEnum.get());
                resultDto.setMsgTypeEnum(msgTypeEnum.get());
                resultDto.setNoticeResourceEnum(noticeResourceEnum.get());
                resultDto.setEmergencyLevelEnum(emergencyLevelEnum.get());
                resultDto.setOrderStatusEnum(orderStatusEnum.get());
                resultDto.setAlramTypeList(alramTypeList);
                resultDto.setWarningSourceEnum(warningSourceEnum.get());
                resultDto.setDeviceSiteVOList(deviceSiteVOList);
                resultDto.setVo(vo);
                resultDtoList.add(resultDto);
            }
            ThreadParallelUtil.run(resultDtoList, f->{
                getRelResult(f);
                return null;
            }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        }

        //排序，因为异步拼装，导致排序错乱，重新排序
        try {
            if (Constants.PARAM_IMPORTANCE_LEVEL.equals(dto.getSortName())) {
                //默认正序
                Comparator<WarningOrderInfoVO> comparator = (w1, w2) -> {
                    if (ObjectUtil.isNull(w1.getOrder().getImportanceLevel())
                            || ObjectUtil.isNull(w2.getOrder().getImportanceLevel())) {
                        return -1;
                    }
                    int level1 = Integer.parseInt(w1.getOrder().getImportanceLevel());
                    int level2 = Integer.parseInt(w2.getOrder().getImportanceLevel());
                    return Integer.compare(level1, level2);
                };
                if (EnumUtil.Order.DESC.getName().equals(dto.getSort())) {
                    comparator = comparator.reversed();
                }
                Comparator<WarningOrderInfoVO> timeComparator = getAlarmTimeComparator();
                //默认倒序
                comparator = comparator.thenComparing(timeComparator.reversed());
                warningOrderInfoVOList.sort(comparator);
            } else if (Constants.PARAM_ALARM_TIME.equals(dto.getSortName())) {
                Comparator<WarningOrderInfoVO> comparator = getAlarmTimeComparator();
                //如果排序为空，或者倒序，就按倒序拍
                if (StringUtils.isEmpty(dto.getSort()) || EnumUtil.Order.DESC.getName().equals(dto.getSort())) {
                    comparator = comparator.reversed();
                }
                warningOrderInfoVOList.sort(comparator);
            }
        } catch (Exception e) {
            log.error("查询关联告警事件信息排序错误", e);
        }

        return Result.data(warningOrderInfoVOList);
    }

    private Comparator<WarningOrderInfoVO> getAlarmTimeComparator() {
        return (woi1, woi2) -> {
            if (ObjectUtil.isNull(woi1.getOrder().getAlarmTime())
                    || ObjectUtil.isNull(woi2.getOrder().getAlarmTime())) {
                return 1;
            }
            Date alarmTime1 = DateUtil.parseDateTime(woi1.getOrder().getAlarmTime());
            Date alarmTime2 = DateUtil.parseDateTime(woi2.getOrder().getAlarmTime());
            return DateUtil.compare(alarmTime1, alarmTime2);
        };
    }

    private void getRelResult(WarningRelResultDto resultDto) {
        // 置信度 取最大置信度展示 -- 新增综合置信度，若存在综合置信度则展示综合置信度
        if (!org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getCompConfidenceLevel())) {
            resultDto.getVo().setConfidenceLevel(resultDto.getVo().getCompConfidenceLevel());
        }
        else {
            String confidenceLevel = resultDto.getVo().getConfidenceLevel();
            if (!org.springframework.util.StringUtils.isEmpty(confidenceLevel)) {
                String[] confidenceLevelList = confidenceLevel.split(Constants.COMMA);
                String confidenceLevelNew = Arrays.stream(confidenceLevelList).max(String::compareTo).get();
                resultDto.getVo().setConfidenceLevel(confidenceLevelNew);
            }
        }

        warningOrderRepository.getDeviceRel(resultDto.getVo());
        String fileImgUrlSrc = resultDto.getVo().getFileImgUrlSrc();
        String fileImgUrlIcon = resultDto.getVo().getFileImgUrlIcon();
        String videoUrl = resultDto.getVo().getVideoUrl();
        String frontAlarmPicUrl = resultDto.getVo().getFrontAlarmPicUrl();
        String frontAlarmVedioUrl = resultDto.getVo().getFrontAlarmVedioUrl();
        if ((Constants.YES_FLAG.equals(resultDto.getVo().getWarningSource()) || Constants.FOUR.equals(resultDto.getVo().getWarningSource()))
            && (org.springframework.util.StringUtils.isEmpty(fileImgUrlIcon)
                || org.springframework.util.StringUtils.isEmpty(videoUrl)
                || org.springframework.util.StringUtils.isEmpty(fileImgUrlSrc))) {
            // AI告警调用6.13接口
            try {
                alarmOperateRepository.getAlarmImgs(resultDto.getVo());
            }
            catch (Exception e) {
                log.error("调用6.13异常: ", e);
            }
        }
        //物联设备告警
        List<SensorMonitorVo> monitors = new ArrayList<>();
        IotRelDeviceVo reldevice = new IotRelDeviceVo();
        if(AlarmConstants.ALARM_SOURCE_8.equals(resultDto.getVo().getWarningSource())  && !org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getDeviceCode()) ){
            try{
               //查询物联设备关联摄像机信息
                TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
                qryCond.setDeviceCode(resultDto.getVo().getDeviceCode());
                qryCond.setTenantId(resultDto.getVo().getTenantId());
                qryCond.setIndustryCode(resultDto.getVo().getIndustryCode());
                try{
                    Result<TenantIotDeviceConfVO> result = videoDeviceService.getTenantIotDevConf(qryCond);
                    if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                        List<TenantIotConfRelVO> iotDeviceConf = result.getData().getConfRels();
                        if(iotDeviceConf!=null&&!iotDeviceConf.isEmpty()){
                            TenantIotConfRelVO iotConfRelVO = iotDeviceConf.get(0);
                            BeanUtil.copyProperties(iotConfRelVO,reldevice);
                        }
                    }
                }catch(Exception e){
                    log.error("物联设备关联摄像机信息查询异常，工单号：{}，异常：{}", resultDto.getVo().getOrderId(), e);
                }
            }catch(Exception e){
                log.error("物联设备因子和关联摄像机信息查询异常，工单号：{}，异常：{}", resultDto.getVo().getOrderId(), e);
            }
        }

        if(reldevice!=null){
            if (!CollectionUtils.isEmpty(resultDto.getModelIdEnum())) {
                resultDto.getModelIdEnum().forEach(item -> {
                    if (Objects.equals(reldevice.getModelCode(), item.getDictValue())){
                        reldevice.setModelCodeName(item.getDictLabel());
                    }
                });
            }
            // 丢一份到orderInfoVO里面，
            resultDto.getVo().setRelDevice(reldevice);
        }
        // 获取网格员
        if (!StringUtils.isEmpty(resultDto.getVo().getGridId())) {
            resultDto.getVo().setKeeperName(deviceRepository.getGridKeeperByGridId(resultDto.getVo().getGridId()));
            //获取网格信息
            GridInfo gridInfo = warningOrderUtil.getGrifInfo(Long.valueOf(resultDto.getVo().getGridId()));
            resultDto.getVo().setGridInfo(gridInfo);
        }

        //是否收藏
        resultDto.getVo().setIsCollection(Constants.ZERO);
        if (!CollectionUtils.isEmpty(resultDto.getWarningOrderCollectionList())) {
            resultDto.getWarningOrderCollectionList().forEach(item -> {
                if(item.getOrderId().equals(resultDto.getVo().getWarningOrderId())){
                    resultDto.getVo().setIsCollection(Constants.ONE);
                }
            });
        }
        if (!CollectionUtils.isEmpty(resultDto.getAlramTypeList())) {
            // 设置图标
            resultDto.getAlramTypeList().forEach(item -> {
                if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(String.valueOf(resultDto.getVo().getWarningTypeId()))) {
                    resultDto.getVo().setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                    resultDto.getVo().setIcon((String) item.get(Constants.PARAM_ICON));
                }
            });
        }
        if (!CollectionUtils.isEmpty(resultDto.getEmergencyLevelEnum())) {
            resultDto.getEmergencyLevelEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getVo().getEmergencyLevel())) {
                    resultDto.getVo().setEmergencyLevelName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(resultDto.getImportanceLevelEnum())) {
            resultDto.getImportanceLevelEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getVo().getImportanceLevel())) {
                    resultDto.getVo().setImportanceLevelName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(resultDto.getMsgTypeEnum())) {
            resultDto.getMsgTypeEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getVo().getMsgType())) {
                    resultDto.getVo().setMsgTypeName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(resultDto.getNoticeResourceEnum())) {
            resultDto.getNoticeResourceEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getVo().getNoticeResource())) {
                    resultDto.getVo().setNoticeResourceName(item.getDictLabel());
                }
            });
        }

        if (!CollectionUtils.isEmpty(resultDto.getOrderStatusEnum())) {
            resultDto.getOrderStatusEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getVo().getOrderStatus())) {
                    resultDto.getVo().setOrderStatusName(item.getDictLabel());
                }
            });
        }

        List<Map> imgs = new ArrayList<>();
        Map imgUrl;
        if (!org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getFileImgUrlIcon())) {
            String[] imgIcons = (resultDto.getVo().getFileImgUrlIcon()).split(Constants.COMMA);
            if (!org.springframework.util.StringUtils.isEmpty(imgIcons) && imgIcons.length > 0) {
                for (String img : imgIcons) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }
        if (!org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getFrontAlarmPicUrl())) {
            String[] imgSrcs = (resultDto.getVo().getFrontAlarmPicUrl()).split(Constants.COMMA);
            if (!org.springframework.util.StringUtils.isEmpty(imgSrcs) && imgSrcs.length > 0) {
                for (String img : imgSrcs) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }
        if (!org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getFileImgUrlSrc())) {
            String[] imgSrcs = (resultDto.getVo().getFileImgUrlSrc()).split(Constants.COMMA);
            if (!org.springframework.util.StringUtils.isEmpty(imgSrcs) && imgSrcs.length > 0) {
                for (String img : imgSrcs) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }
        // 增加摄像机地址
        if (!CollectionUtils.isEmpty(resultDto.getDeviceSiteVOList())) {
            resultDto.getDeviceSiteVOList().forEach(deviceSiteVO -> {
                if (deviceSiteVO.getDeviceCode().equals(resultDto.getVo().getDeviceCode())) {
                    resultDto.getVo().setDeviceAddress(deviceSiteVO.getLocation());
                    resultDto.getVo().setModelId(deviceSiteVO.getModelId());
                    resultDto.getVo().setHeight(deviceSiteVO.getHeight());
                    resultDto.getVo().setVideoLatitude(deviceSiteVO.getLatitude());
                    resultDto.getVo().setVideoLongitude(deviceSiteVO.getLongitude());
                    resultDto.getVo().setDeviceLatitude(deviceSiteVO.getLatitude());
                    resultDto.getVo().setDeviceLongitude(deviceSiteVO.getLongitude());
                    // 摄像机告警定位配置判断
                    if (Constants.YES_FLAG.equals(deviceSiteVO.getAlarmLocation())) {
                        resultDto.getVo().setAlarmLocation(deviceSiteVO.getAlarmLocation());
                    }

                    // 判断设备是收藏
                    String deviceCodeCollect = deviceRepository.getDeviceCodeCollectByDeviceCodeAndObjType(resultDto.getVo().getDeviceCode(), "5", String.valueOf(resultDto.getDto().getUserId()));
                    resultDto.getVo().setDeviceCodeCollect(deviceCodeCollect);
                }
            });
        }
        if (WarningSourceEnum.IOT_ALARM.getTypeValue().equals(resultDto.getVo().getWarningSource())) {
            //物联告警
            DeviceSiteVO deviceSiteVO = deviceRepository.queryIotDevice(resultDto.getVo().getDeviceCode());
            if (deviceSiteVO != null) {
                resultDto.getVo().setDeviceAddress(deviceSiteVO.getLocation());
                resultDto.getVo().setModelId(deviceSiteVO.getModelId());
                resultDto.getVo().setHeight(deviceSiteVO.getHeight());
                resultDto.getVo().setVideoLatitude(deviceSiteVO.getLatitude());
                resultDto.getVo().setVideoLongitude(deviceSiteVO.getLongitude());
                resultDto.getVo().setDeviceLongitude(deviceSiteVO.getLongitude());
                resultDto.getVo().setDeviceLatitude(deviceSiteVO.getLatitude());
                resultDto.getVo().setDeviceModelCode(deviceSiteVO.getDeviceModelCode());
                resultDto.getVo().setFocusRange(deviceSiteVO.getFocusRange());
                resultDto.getVo().setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
            }

        }
        //物联设备厂商
        if (WarningSourceEnum.IOT_ALARM.getTypeValue().equals(resultDto.getVo().getWarningSource()) || WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(resultDto.getVo().getWarningSource())) {
            if (!CollectionUtils.isEmpty(resultDto.getIotModelIdEnum())) {
                resultDto.getIotModelIdEnum().forEach(item -> {
                    if (Objects.equals(resultDto.getVo().getModelId(), item.getDictValue())) {
                        resultDto.getVo().setModelFactoryName(item.getDictLabel());
                    }
                });
            }
        }
        //增加摄像机到事发地点距离
        if (!org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getLongitude()) && !org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getLatitude())
                && !org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getVideoLongitude()) && !org.springframework.util.StringUtils.isEmpty(resultDto.getVo().getVideoLatitude())) {
            //根据设备的经纬度和事发地点的经纬度计算距离位置
            double distance = DistanceUtils.getDistance(Double.parseDouble(resultDto.getVo().getLongitude()), Double.parseDouble(resultDto.getVo().getLatitude()),
                    Double.parseDouble(resultDto.getVo().getVideoLongitude()), Double.parseDouble(resultDto.getVo().getVideoLatitude()));
            // 事发地点距离
            resultDto.getVo().setIncidentDistance(String.format("%.2f", distance));
        }

        Map<Function, Object> paramMap = new ConcurrentHashMap<>();
        //获取图版
        Function<OrderInfoVO, OrderInfoVO> fun = this::getPolygonOperate;
        paramMap.put(fun, resultDto.getVo());
        List<Function> listFunc = Arrays.asList(fun);
        ThreadParallelUtil.run(listFunc, f -> {
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);

        if (!CollectionUtils.isEmpty(resultDto.getModelIdEnum())) {
            resultDto.getModelIdEnum().forEach(item -> {
                if (Objects.equals(resultDto.getVo().getModelId(), item.getDictValue())) {
                    resultDto.getVo().setModelFactoryName(item.getDictLabel());
                }
            });
        }
        // 告警点和设备点水平方位角处理转换
        if (!StringUtils.isEmpty(resultDto.getVo().getHoriAzimuthAngle())) {
            String angleValue = AngleUtils.transformAngle(resultDto.getVo().getHoriAzimuthAngle());
            if (!org.springframework.util.StringUtils.isEmpty(angleValue)) {
                resultDto.getVo().setHoriAzimuthAngle(angleValue);
            }
        }

        List<Map> videos = new ArrayList<>();
        if (!StringUtils.isEmpty(resultDto.getVo().getVideoUrl()) || !StringUtils.isEmpty(resultDto.getVo().getVideoUrl())) {
            String videoUrls = resultDto.getVo().getVideoUrl() == null ? resultDto.getVo().getVideoUrl() : resultDto.getVo().getVideoUrl();
            String[] videoUrlStr = videoUrls.split(Constants.COMMA);
            for (String video : videoUrlStr) {
                imgUrl = new HashMap();
                imgUrl.put("src", video);
                videos.add(imgUrl);
            }
        }
        if (!StringUtils.isEmpty(resultDto.getVo().getFrontAlarmVedioUrl())) {
            String videoUrls = resultDto.getVo().getFrontAlarmVedioUrl();
            String[] videoUrlStr = videoUrls.split(Constants.COMMA);
            for (String video : videoUrlStr) {
                imgUrl = new HashMap();
                imgUrl.put("src", video);
                videos.add(imgUrl);
            }
        }

        resultDto.getWarningSourceEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getVo().getWarningSource())) {
                resultDto.getVo().setWarningSourceName(item.getDictLabel());
            }
        });
        if (!StringUtils.isEmpty(resultDto.getVo().getLongitude()) && !StringUtils.isEmpty(resultDto.getVo().getLatitude())
                && !StringUtils.isEmpty(resultDto.getVo().getDeviceLongitude()) && !StringUtils.isEmpty(resultDto.getVo().getDeviceLatitude())) {
            //根据设备的经纬度和事发地点的经纬度计算距离位置
            double distance = DistanceUtils.getDistance(Double.parseDouble(resultDto.getVo().getLongitude()), Double.parseDouble(resultDto.getVo().getLatitude()),
                    Double.parseDouble(resultDto.getVo().getDeviceLongitude()), Double.parseDouble(resultDto.getVo().getDeviceLatitude()));
            // 事发地点距离
            resultDto.getVo().setIncidentDistance(String.format("%.2f", distance));
        }
        Map<String, Object> video = new HashMap<>(3);
        // 兼容APP旧版本 故此处理
        video.put("fileImageUrl", resultDto.getVo().getVideoUrl() == null ? resultDto.getVo().getVideoUrl().split(Constants.COMMA)[0] : resultDto.getVo().getVideoUrl().split(Constants.COMMA)[0]);
        video.put("TIvideo", resultDto.getVo().getFrontAlarmVedioUrl());
        video.put("videoList", videos);

        WarningOrderInfoVO warningOrderInfoVO = new WarningOrderInfoVO();
        warningOrderInfoVO.setOrder(resultDto.getVo());
        warningOrderInfoVO.setImgs(imgs);
        warningOrderInfoVO.setVideo(video);
        resultDto.getWarningOrderInfoVOList().add(warningOrderInfoVO);
    }

    private OrderInfoVO getPolygonOperate(OrderInfoVO orderInfoVO) {
        log.info("详情查询图版入参:{}", JSON.toJSONString(orderInfoVO));
        if(!org.springframework.util.StringUtils.isEmpty(orderInfoVO.getTaskId())){
            try {
                PolygonOperateDTO polygonOperateDTO = new PolygonOperateDTO();
                polygonOperateDTO.setTaskId(orderInfoVO.getTaskId());
                polygonOperateDTO.setTenantId(orderInfoVO.getTenantId());
                polygonOperateDTO.setDeviceCode(orderInfoVO.getDeviceCode());
                polygonOperateDTO.setChannelCode(orderInfoVO.getChannelCode());
                polygonOperateDTO.setPresetId(orderInfoVO.getPresetId());
                Result result = warningOrderRepository.getPolygonOperate(polygonOperateDTO);
                if(result != null && result.getData()!=null){
                    List<Map> polygonOperate = JSON.parseArray(JSON.toJSONString(result.getData()), Map.class);
                    orderInfoVO.setPolygonOperate(polygonOperate);
                }
            } catch (Exception e) {
                log.error("get warning order info rel polygonOperate 电子图斑失败，orderInfoVO:{}", JSON.toJSONString(orderInfoVO), e);
            }
        }
        return orderInfoVO;
    }
}

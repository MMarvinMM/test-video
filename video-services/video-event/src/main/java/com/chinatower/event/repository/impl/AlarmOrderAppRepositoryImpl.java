package com.chinatower.event.repository.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.util.ArrayUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.event.core.alarm.util.SystemUtil;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.ThreadDto.AppWarningAlarmOrderDto;
import com.chinatower.event.dto.ThreadDto.AppWarningOrderDto;
import com.chinatower.event.dto.ThreadDto.AppWarningOrderResultDto;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.entity.dto.OrderLeaderNumDTO;
import com.chinatower.event.entity.vo.HandleRecordVO;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.util.CommonUtils;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.mapper.AlarmOrderAppMapper;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.resp.SysDictDataResponse;

import lombok.extern.slf4j.Slf4j;

import static com.chinatower.event.constants.Constants.*;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOrderAppRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/19 16:20
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class AlarmOrderAppRepositoryImpl implements IAlarmOrderAppRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private AlarmOrderAppMapper alarmOrderAppMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private IAlarmLableRelRepository alarmLableRelRepository;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private SystemUtil systemUtil;

    @Autowired
    private SysMapper sysMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取当前登录人上报的事件列表
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_code"
            ,areaCity = "dataTable.city_code",areaCode = "dataTable.county_code",processIdColumn = "dataTable.process_id")
    public TableDataInfo getOrderListByUser(AppOrderListByUserDTO dto) {
        long start = System.currentTimeMillis();
        log.info("获取当前登录人上报的事件列表开始时间：{},入参：{}", start, JSON.toJSONString(dto));
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        int allCount = 0;
        List<WarningOrderListVO> resultList = new ArrayList<>();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            log.error("获取当前登录人上报的事件列表失败：" + ResultCode.CURRENT_USER_FAIL.getMsg());
            tableDataInfo.setTotal(allCount);
            tableDataInfo.setRows(resultList);
            return tableDataInfo;
        }
        SysUser sysUser = loginUser.getUser();
        dto.setTenantId(sysUser.getTenantId());;
        dto.setOrgIds(CommonUtils.getOrgIds(loginUser.getUser().getDeptId()));
        // 获取当前用户角色
        Long[] roleIds = sysUser.getRoleIds();
        List<String> roleIdList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        dto.setRoleIdList(roleIdList);

        dto.setAdmin((OrderConstants.IS_TENANT_ADMIN.equals(sysUser.getIsTenantAdmin())));
        dto.setHandle(CommonUtils.ifHandleRole(roleIds));
        //根据用户id和角色id查询流程环节信息
        //String flowLinkIds = flowLinkService.getFlowLinkAttrByUser(sysUser, roleIdList);
        String flowLinkIds="";
        dto.setUserId(sysUser.getUserId());

        // 铁塔切租户则查询租户管理员的已办
        if (SecurityUtils.isSwitch()) {
            SysUser user = sysMapper.selectUserOnlyByUserName(sysUser.getUserName());
            dto.setUserId(user.getUserId());
        }

        this.handleOrderReq(dto,flowLinkIds);
        // 分页
        if (dto.getPageNum() == null) {
            dto.setPageNum(Constants.PAGE_NUM);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(Constants.PAGE_SIZE);
        }

        //查询开始结束时间
        Date startDate;
        Date endDate;
        if (StringUtils.isEmpty(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        }
        else {
            endDate = DateUtils.string2Date(dto.getAlarmTimeEnd());
            int se = Integer.parseInt(DateUtils.dateToStringYM(endDate));
            if (se < Integer.parseInt(TableUtil.getMinTableDate()) || se > Integer.parseInt(TableUtil.getMinTableDate())+5 ) {
                endDate = new Date();
            }
        }
        if (StringUtils.isEmpty(dto.getAlarmTimeStart())) {
            // 默认查询近30天的告警
            startDate = DateUtils.getDateAfter(new Date(), -30);
        }
        else {
            startDate = DateUtils.string2Date(dto.getAlarmTimeStart());
            int sd = Integer.parseInt(DateUtils.dateToStringYM(startDate));
            if (sd < Integer.parseInt(TableUtil.getMinTableDate())) {
                startDate = DateUtils.getDateAfter(new Date(), -30);
            }
        }

        if(Constants.NINE.equals(dto.getOrderStatus())){
            dto.setEnableAcceptanceMode(Constants.ONE);
            dto.setOrderStatus("");
        }
        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
            //dto.getOrderStatusList().clear();
        }
        dto.setAlarmTimeStart(DateUtils.dateToStringNoMS(startDate));
        dto.setAlarmTimeEnd(DateUtils.dateToStringNoMS(endDate));
        //需要查询的个数
        int limit = dto.getPageSize();
        // 分页开始的数
        int offset = (dto.getPageNum() - 1) * limit;
        // 当前表查询开始的数
        int tableStartNum;
        int count;
        boolean stop = false;
        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) &&dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
            //dto.getOrderStatusList().clear();
        }
        //告警类型标签
        AppWarningOrderDto orderDto = new AppWarningOrderDto();
        orderDto.setLoginUser(loginUser);
        orderDto.setResultList(resultList);
        orderDto.setDto(dto);
        orderDto.setAllCount(allCount);
        orderDto.setTableDataInfo(tableDataInfo);
        Function<AppWarningOrderDto,TableDataInfo> fun1= this::getTableDataInfo;
        //跨库获取页面查询设备信息关联数据
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setSysUser(sysUser);
        //处理传入的组织条件
        AppWarningAlarmOrderDto orderAlarmDto = new AppWarningAlarmOrderDto();
        orderAlarmDto.setAlarmOrderDTO(alarmOrderDTO);
        orderAlarmDto.setResultList(resultList);
        orderAlarmDto.setDto(dto);
        orderAlarmDto.setAllCount(allCount);
        orderAlarmDto.setTableDataInfo(tableDataInfo);
        Function<AppWarningAlarmOrderDto,TableDataInfo> fun2= this::getTableDataInfo;
        //告警标签
        Function<AppOrderListByUserDTO,List<AlarmLableRelDTO>> fun3= this::getAlarmLableRelDTOS;
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put(fun1,orderDto);
        paramMap.put(fun2,orderAlarmDto);
        paramMap.put(fun3,dto);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if(!StringUtils.isEmpty(dto.getOrderStatus()) && EnumUtil.FlowNode.END.getCode().equals(dto.getOrderStatus())){
            List<String> orderStatusList = new ArrayList<>();
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        log.info("获取当前登录人上报的事件列表入参处理耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
        String tableName;
        String flowTableName;
        //从最后一个表开始向前获取
        while (!stop) {
            tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
            flowTableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_PROCESS_TEBLE);

            dto.setTableName(tableName);
            dto.setFlowTableName(flowTableName);
            String yearMonth = tableName.substring(tableName.length() - 6, tableName.length());
            //告警标签
            List<String> warningOrderIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                List<AlarmLableRelDTO> firstALarmLable= dto.getAlarmLableRelDTOList().stream().filter(item -> yearMonth.equals(item.getTableTime())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(firstALarmLable)) {
                    firstALarmLable.forEach(item -> {
                        warningOrderIds.add(item.getWaringOrderId());
                    });
                    dto.setWarningOrderIds(warningOrderIds);
                }else {
                    stop = DateUtils.sameMouth(startDate, endDate);
                    // 转到上月
                    String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                    endDate = DateUtils.stringToDateYM(lastMonth);
                    continue;
                }
            }
            count = alarmOrderAppMapper.getOrderListCount(dto);
            //累计总数
            allCount += count;
            if (allCount > offset && count > 0 && limit > 0) {
                tableStartNum = offset - (allCount - count);
                if (tableStartNum < 0) {
                    tableStartNum = 0;
                }
                if (count > limit) {
                    dto.setLimit(limit);
                }
                else {
                    dto.setLimit(count);
                }
                dto.setOffset(tableStartNum);
                List<WarningOrderListVO> queryResult = alarmOrderAppMapper.getOrderList(dto);
                resultList.addAll(queryResult);
                limit = limit - queryResult.size();
            }
            stop = DateUtils.sameMouth(startDate, endDate);
            //转到上月
            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
            endDate = DateUtils.stringToDateYM(lastMonth);
        }
        log.info("获取当前登录人上报的事件列表查询耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
        // 处理时长，剩余/超期时间，是否有编辑权限，是否超期、领导批示个数
        //返回领导批示数量
        List<OrderLeaderNumDTO> auditNum = new ArrayList<>();
        List<Map> alramTypeList = new ArrayList<>();
        List<HandleRecordVO> handleRecordList = new ArrayList<>();
        // 告警等级名称
        AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
        // 告警状态名称
        AtomicReference<List<SysDictDataResponse>> orderStatusEnum = new AtomicReference<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            auditNum = warningOrderMapper.getLeaderAuditNum(resultList);
            // 查询告警类型名称
            alramTypeList = warningOrderRepository.getAlarmTypeList();
            List<String> paramList = new ArrayList<>();
            paramList.add(Constants.ALARM_LEVEL);
            paramList.add(String.valueOf(Constants.ORDER_STATUS));

            ThreadParallelUtil.run(paramList,o->{
                if(String.valueOf(Constants.ORDER_STATUS).equals(String.valueOf(o))){
                    orderStatusEnum.set(systemUtil.getEnum(o));
                }else if(Constants.ALARM_LEVEL.equals(String.valueOf(o))){
                    importanceLevelEnum.set(systemUtil.getEnum(o));
                }
                return null;
            }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
            //查询督办记录
            Query query = new Query();
            List<String> orderIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId()))
                    .map(item -> item.getOrderId()).distinct().collect(Collectors.toList());
            query.addCriteria(Criteria.where(PARAM_ORDER_ID).in(orderIdList));
            query.addCriteria(Criteria.where(PARAM_DELETE_FLAG).is(ZERO));
            handleRecordList = mongoTemplate.find(query, HandleRecordVO.class, TableConstant.VM_FLOW_HANDLE_RECORD_T);
        }
        List<AppWarningOrderResultDto> resultDtoList = new ArrayList<>();
        List<String> userIdList = new ArrayList<>();
        for (WarningOrderListVO map : resultList) {
            if(!StringUtils.isEmpty(map.getProcessId())){
                String[] processIdList = map.getProcessId().split(",");
                for (String userId:processIdList) {
                    userIdList.add(userId);
                }
            }

            //v1.6.80 是否显示督办提示图标
            map.setIsHandleDisplay(Constants.ZERO);
            if (!CollectionUtils.isEmpty(handleRecordList)) {
                handleRecordList.forEach(item -> {
                    if (map.getOrderId().equals(item.getOrderId()) && map.getLinkId().equals(item.getLinkId())) {
                        map.setIsHandleDisplay(Constants.ONE);
                    }
                });
            }
        }
        List<UserInfoDto> userList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(userIdList)){
            userList = sysMapper.queryUserListInfo(userIdList);
        }
        for (WarningOrderListVO map : resultList){
            AppWarningOrderResultDto resultDto = new AppWarningOrderResultDto();
            resultDto.setDto(dto);
            resultDto.setFlowLinkIds(flowLinkIds);
            resultDto.setAuditNum(auditNum);
            resultDto.setAlramTypeList(alramTypeList);
            resultDto.setImportanceLevelEnum(importanceLevelEnum.get());
            resultDto.setOrderStatusEnum(orderStatusEnum.get());
            resultDto.setMap(map);
            resultDto.setUserList(userList);
            resultDtoList.add(resultDto);
        }
        ThreadParallelUtil.run(resultDtoList,f->{
            extracted(f);
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        log.info("获取当前登录人上报的事件列表最终耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setTotal(allCount);
        tableDataInfo.setRows(resultList);
        return tableDataInfo;
    }

    private List<AlarmLableRelDTO> getAlarmLableRelDTOS(AppOrderListByUserDTO dto) {
        List<AlarmLableRelDTO> alarmLableRelDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
            alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(dto.getLableIdList(), dto.getOrderId());
        }
        dto.setAlarmLableRelDTOList(alarmLableRelDTOList);
        return alarmLableRelDTOList;
    }

    private void extracted(AppWarningOrderResultDto resultDto) {
        // 处理时长
        resultDto.getMap().setDuration(DateUtils.getTimeDifference2(new Date(), resultDto.getMap().getCreateDate()));
        // 当前环节停留时长
        resultDto.getMap().setWaitTime(DateUtils.getTimeDifference2(new Date(), resultDto.getMap().getFlowNodeTime()));
        // 是否有编辑权限
        resultDto.getMap().setIfEdit(warningOrderRepository.ifEditHandle(resultDto.getDto().isAdmin(), resultDto.getDto().getUserId(), resultDto.getMap(), resultDto.getFlowLinkIds()));
        List<UserInfoDto> userInfoDtoList = new ArrayList<>();
        if(!StringUtils.isEmpty(resultDto.getMap().getProcessId())){
            String[] processIdList = resultDto.getMap().getProcessId().split(",");
            for (String userId:processIdList) {
                for (UserInfoDto dto:resultDto.getUserList()) {
                    if(userId.equals(dto.getUserId())){
                        userInfoDtoList.add(dto);
                        break;
                    }
                }
            }
        }
        resultDto.getMap().setProcessUserList(userInfoDtoList);
        if(!StringUtils.isEmpty(resultDto.getMap().getUserDistance())){
            String userDistance = resultDto.getMap().getUserDistance();
            String cleanedUserDistance = userDistance.replaceAll("[^\\d.]", ""); // 移除非数字字符
            double num = Double.parseDouble(cleanedUserDistance);
            DecimalFormat df = new DecimalFormat("#.00");
            String realDistance = "";
            if(num>1000){
                //如果大于一千米，单位为千米保留两位小数
                double result = num / 1000.0;
                realDistance = df.format(result);
                resultDto.getMap().setUserDistance(realDistance+"千米");
            }else{
                BigDecimal decimal = new BigDecimal(cleanedUserDistance);
                int rounded = decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                resultDto.getMap().setUserDistance(String.valueOf(rounded)+"米");
            }
        }
        // 是否超期&超期/剩余时间
        resultDto.getMap().setIfTimeout(0);
        resultDto.getMap().setShowTime("");
        //受理状态的工单超期展示受理超期
        if(Constants.ONE.equals(resultDto.getMap().getEnableAcceptanceMode())){
            if(StringUtils.isEmpty(resultDto.getMap().getTimeLimitAcceptance())){
                resultDto.getMap().setTimeLimit(null);
            }else{
                resultDto.getMap().setTimeLimit(Integer.valueOf(resultDto.getMap().getTimeLimitAcceptance()));
            }
        }
        if (!StringUtils.isEmpty(resultDto.getMap().getTimeLimit())) {
            resultDto.getMap().setIfTimeout(DateUtils.checkIfTimeOut(resultDto.getMap().getTimeLimit(), resultDto.getMap().getFlowNodeTime()));
            StringBuilder showTime = new StringBuilder();
            if (resultDto.getMap().getIfTimeout() == 0) {
                // 未超期
                showTime.append("剩余").append(DateUtils.getTimeDifference(
                    DateUtils.subtractTime(resultDto.getMap().getFlowNodeTime(), resultDto.getMap().getTimeLimit() * 1000), new Date()));
            }
            else {
                // 超期
                showTime.append("超期")
                    .append(DateUtils.getTimeDifference(new Date(),
                        DateUtils.subtractTime(resultDto.getMap().getFlowNodeTime(),  resultDto.getMap().getTimeLimit() * 1000)));
            }
            resultDto.getMap().setShowTime(showTime.toString());
        }
        resultDto.getMap().setLeaderNum(0);
        // 领导批示
        resultDto.getAuditNum().forEach(item -> {
            if (item.getOrderId().equals(resultDto.getMap().getOrderId())) {
                resultDto.getMap().setLeaderNum(item.getLeaderNum());
            }
        });

        if (!CollectionUtils.isEmpty(resultDto.getAlramTypeList())) {
            // 设置图标
            resultDto.getAlramTypeList().forEach(item -> {
                if (item.get(Constants.PARAM_TYPE_VALUE_).toString()
                    .equals(resultDto.getMap().getWarningTypeId())) {
                    resultDto.getMap().setIcon((String) item.get(Constants.PARAM_ICON));
                    resultDto.getMap().setWarningTypeName((String) item.get(Constants.PARAM_TYPENAME));
                }
            });
        }

        if (!CollectionUtils.isEmpty(resultDto.getImportanceLevelEnum())) {
            resultDto.getImportanceLevelEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getMap().getImportanceLevel())) {
                    resultDto.getMap().setImportanceLevelName(item.getDictLabel());
                }
            });
        }

        if (!CollectionUtils.isEmpty(resultDto.getOrderStatusEnum())) {
            resultDto.getOrderStatusEnum().forEach(item -> {
                if (item.getDictValue().equals(resultDto.getMap().getOrderStatus())) {
                    resultDto.getMap().setOrderStatusName(item.getDictLabel());
                }
            });
        }
        if(EnumUtil.FlowNode.MISINFORMATION.getCode().equals(resultDto.getMap().getOrderStatus()) || EnumUtil.FlowNode.DUPLICATION.getCode().equals(resultDto.getMap().getOrderStatus())){
            resultDto.getMap().setOrderStatusName(EnumUtil.FlowNode.END.getName());
        }

        // 20211127 zzh 处理图片，多张图片取第一张
        String imgUrl = resultDto.getMap().getImgUrl();
        if (!StringUtils.isEmpty(imgUrl) && imgUrl.contains(Constants.COMMA)) {
            resultDto.getMap().setImgUrl(imgUrl.split(Constants.COMMA)[0]);
        }
    }

    private TableDataInfo getTableDataInfo(AppWarningAlarmOrderDto orderDto) {

        warningOrderRepository.handleOrgIdList(orderDto.getAlarmOrderDTO());
        BeanUtils.copyBeanProp(orderDto.getAlarmOrderDTO(), orderDto.getDto());
        orderDto.getAlarmOrderDTO().setModelId(orderDto.getDto().getModelId());
        orderDto.getAlarmOrderDTO().setModelIds(orderDto.getDto().getModelIds());
        List<AlarmOrderByDeviceDTO> devList = deviceRepository.queryAlarmOrderByDevice(orderDto.getAlarmOrderDTO());
        orderDto.getDto().setDevList(devList);
        if (CollectionUtils.isEmpty(devList)
                && (!StringUtils.isEmpty(orderDto.getDto().getDeviceLabelId()) || !CollectionUtils.isEmpty(orderDto.getDto().getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(orderDto.getDto().getModelId()) || !StringUtils.isEmpty(orderDto.getDto().getDeviceName()) ||
                !CollectionUtils.isEmpty(orderDto.getDto().getModelIds()))) {
            orderDto.getTableDataInfo().setCode(ResultCode.SUCCESS.getCode());
            orderDto.getTableDataInfo().setTotal(orderDto.getAllCount());
            orderDto.getTableDataInfo().setRows(orderDto.getResultList());
            return orderDto.getTableDataInfo();
        }
        return null;
    }

    private TableDataInfo getTableDataInfo(AppWarningOrderDto orderDto) {
        List<AlarmTagsTypeRelDTO> alarmTagsTypeRelDTOList = sysAlarmTagsRepository.getAlarmTypeByIds(orderDto.getDto().getTagIdList(), "122069881");
        log.info("APP事件上报列表-查询告警类型标签对应的告警类型：{}" + JSON.toJSONString(alarmTagsTypeRelDTOList));
        List<String> warningTypeIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderDto.getDto().getTagIdList()) && CollectionUtils.isEmpty(alarmTagsTypeRelDTOList)) {
            log.info("没有查询到告警类型标签对应的告警类型");
            warningTypeIdList.add("没有告警标签数据");
        }
        alarmTagsTypeRelDTOList.forEach(alarmTagsTypeRelDTO -> {
            if (!StringUtils.isEmpty(alarmTagsTypeRelDTO.getTypeValue())) {
                warningTypeIdList.add(alarmTagsTypeRelDTO.getTypeValue());
            }
        });
        if (!CollectionUtils.isEmpty(orderDto.getDto().getWarningTypeIdList())) {
            warningTypeIdList.addAll(orderDto.getDto().getWarningTypeIdList());
        }
        orderDto.getDto().setWarningTypeIdList(warningTypeIdList);
        return null;
    }

    /**
     * 功能描述:
     * 处理工单列表入参
     *
     * @param:
     * @return:
     * @auther: yuexuebin
     * @date: 2021/11/22 22:39
     */
    public void handleOrderReq(AppOrderListByUserDTO dto,String flowLinkIds) {
        // 已办查询 根据当前用户的用户id和告警表的user_record（逗号拼接）模糊关联
        // 待办查询 根据当前用户的角色、id和环节配置表的处理角色和用户关联，取环节配置id和环节id字段做过滤
        if ((Constants.SEARCH_TYPE_1.equals(dto.getSearchType()) || Constants.SEARCH_TYPE_2.equals(dto.getSearchType())) && !StringUtils.isEmpty(flowLinkIds)) {
            dto.setFlowLinkIds(flowLinkIds);
        }
    }

    @Override
    public Result updateOrderAlarmType(AppOrderUpdateDto dto) {
        try{
            String tableName = TableConstant.WARN_ORDER_TEBLE + dto.getWarningOrderId().substring(0, 6);

            dto.setTableName(tableName);
            int num = alarmOrderAppMapper.updateOrderAlarmType(dto);
        }catch(Exception e){
            log.error("告警工单事件更新异常：{}",e);
            return Result.fail("更新失败");
        }
        return Result.success("更新成功");
    }
}

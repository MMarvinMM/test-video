package com.chinatower.receive.core.alarm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.api.deprecated.ISensorDeviceServiceMigrate;
import com.chinatower.device.entity.vo.SensorMonitorRelInfoVo;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.receive.asyncTask.AsyncCommon;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.alarm.common.service.CacheService;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.mapper.IotAlarmConfigMapper;
import com.chinatower.receive.mongodb.IotConfigMongoMapper;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.util.TableUtil;
import com.chinatower.receive.vo.dto.IotFactorVo;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.DeviceSiteReceiveVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 物联告警接收策略实现类
 * @Author: lyy
 * @Date: 2024/08/06 09:54
 * @version release_v1.6.90
 */
@Component("iotAlarmStrategy")
@Slf4j
public class IotAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private IotAlarmConfigMapper iotAlarmConfigMapper;

    @Autowired
    private IotConfigMongoMapper iotConfigMongoMapper;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private WarningOrderFlowService warningOrderFlowService;

    @Autowired
    private IVideoDeviceService videoDeviceService;

    @Autowired
    private ISensorDeviceServiceMigrate iSensorDeviceServiceMigrate;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.IOT_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    //内存无值自动加载 有效时间5min
    private  LoadingCache<String, String> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    log.info("cache load key = {}", key);
                    //DB查询全量物联配置信息
                    List<ReceiveIotAlarmConfigDto> iotConfigList = iotAlarmConfigMapper.selectAllIotConfig();
                    if (CollectionUtils.isEmpty(iotConfigList)) {
                        log.info("未查询到物联配置,不处理!");
                        return null;
                    }
                    //获取全量物联配置id
                    List<String> configIdList = iotConfigList.stream()
                            .map(ReceiveIotAlarmConfigDto::getConfigId)
                            .distinct()
                            .collect(Collectors.toList());
                    //查询物联规则
                    List<ReceiveIotAlarmGroupsDto> groupsList = iotAlarmConfigMapper.selectAllIotGroupsInConfig(configIdList);
                    //获取全量物联规则id
                    List<String> groupsIdList = groupsList.stream().map(v -> v.getId()).distinct().collect(Collectors.toList());
                    //查询物联设备
                    List<ReceiveIotAlarmDeviceDto> deviceList = iotAlarmConfigMapper.selectAllIotDeviceInConfig(groupsIdList);
                    //查询物联因子
                    List<ReceiveIotAlarmFactorDto> factorList = iotAlarmConfigMapper.selectAllIotFactorInConfig(groupsIdList);
                    //匹配物联配置下的物联设备,物联因子,多组规则
                    for (ReceiveIotAlarmConfigDto configDto : iotConfigList) {
                        List<ReceiveIotAlarmGroupsDto> groupsDtoList = new ArrayList<>();
                        //规则
                        for (ReceiveIotAlarmGroupsDto groupsDto : groupsList) {
                            if (configDto.getConfigId().equals(groupsDto.getIotConfigId())) {
                                List<ReceiveIotAlarmDeviceDto> deviceDtoList = new ArrayList<>();
                                List<ReceiveIotAlarmFactorDto> factoryDtoList = new ArrayList<>();
                                for (ReceiveIotAlarmDeviceDto deviceDto : deviceList) {
                                    if (groupsDto.getId().equals(deviceDto.getGroupId())) {
                                        deviceDtoList.add(deviceDto);
                                    }
                                }
                                for (ReceiveIotAlarmFactorDto factorDto : factorList) {
                                    if (groupsDto.getId().equals(factorDto.getGroupId())) {
                                        factoryDtoList.add(factorDto);
                                    }
                                }
                                groupsDto.setDeviceList(deviceDtoList);
                                groupsDto.setFactorList(factoryDtoList);
                                groupsDtoList.add(groupsDto);
                            }
                        }
                        configDto.setIotAlarmConfigGroupsList(groupsDtoList);
                    }
                    String jsonString = JSONArray.toJSONString(iotConfigList);
                    return jsonString;
                }
            });

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备告警上报入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try {
            // ###1、接收物联设备告警的租户信息
            // 获取物联设备告警信息并处理
            List<ReceiveIotAlarmVO> receiveIotAlarmVOList = receiveAlarmDTO.getReceiveIotAlarmVOList();
            if (CollectionUtils.isEmpty(receiveIotAlarmVOList)) {
                log.info("物联设备批量上报告警信息为空,不处理!");
                return ;
            }
            //获取设备编码列表并查询相关租户
            List<String> deviceCodeList = receiveIotAlarmVOList.stream().map(ReceiveIotAlarmVO::getDeviceCode).distinct().collect(Collectors.toList());
            List<OrderCustomerVO> customers = deviceRepository.getCustomersBySensorDeviceList(deviceCodeList);

            if(CollectionUtils.isEmpty(customers)){
                log.info("未查询到租户信息,告警编码：{}",receiveAlarmDTO.getReceiveAlarmVO().getAlarmCode());
                return ;
            }else {
                receiveAlarmDTO.setCustomers(customers);
                log.info("物联设备批量处理接收告警的租户信息：{}", JSON.toJSONString(customers));
            }

            // ###2、处理物联设备告警配置及MongoDB因子累计值信息
            processIotAlarmConfigurations(receiveAlarmDTO, receiveIotAlarmVOList, deviceCodeList);

            // ###3、根据配置的物联因子阈值筛选需要生成的告警工单类型及租户
            processAlarmEvents(receiveAlarmDTO, receiveIotAlarmVOList);

            // ###4、处理告警地址和经纬度信息
            processAddressAndLocation(receiveAlarmDTO, deviceCodeList);

            // ###5、处理告警工单、告警压制、运营研判
            List<ReceiveAlarmVO> alarms = iotWarningOrderHandler(receiveAlarmDTO);
            if (CollectionUtils.isEmpty(alarms)) {
                log.info(MessageConstants.INTERCEPT_FAIL_MESSAGE);
                return;
            }

            // ###6、推送告警消息
            try{
                commonService.batchPushMessage(alarms);
            }catch(Exception e){
                log.error("批量推送物联告警消息时发生异常：{}", e);
            }
        } catch(Exception e) {
            log.error("物联设备告警上报时发生异常：{}", e);
        }
    }

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        return null;
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        log.info("该策略无需重写此方法");
    }

    /**
     * 处理告警工单、告警压制及运营研判。
     * @param receiveAlarmDTO 包含告警数据和相关信息的传输对象
     * @return 处理后的告警对象列表
     */
    private List<ReceiveAlarmVO> iotWarningOrderHandler(ReceiveAlarmDTO receiveAlarmDTO) {
        log.info("物联设备生成告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        List<ReceiveAlarmVO> successDatas = new ArrayList<>();
        try {
            long beginTime = System.currentTimeMillis();
            List<ReceiveAlarmVO> receiveAlarmVOList = receiveAlarmDTO.getList();
            if (CollectionUtils.isEmpty(receiveAlarmVOList)) {
                return null;
            }

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            String alarmTime = receiveAlarmVOList.get(0).getAlarmTime();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));
            // 接收告警集合
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            // 运营研判告警集合
            List<ReceiveAlarmVO> operations = new ArrayList<>();
            // 告警压制集合
            List<ReceiveAlarmVO> intercepts = new ArrayList<>();
            //获取需要特殊处理行业版本
            List<SysDictDataResponse> dictList = warningOrderUtil.getDict(Constants.APP_VER_CODE_TOPIC);
            for (ReceiveAlarmVO receiveAlarmVO : receiveAlarmVOList) {
                //构建告警对象
                buildAlarmVo(receiveAlarmVO,new ArrayList<>());

                //处理特殊行业版本
                if (warningOrderUtil.judgeSpecialAppVer(dictList, receiveAlarmVO)) {
                    continue;
                }

                //判断并处理告警压制
                if (warningOrderUtil.judgeOperationIntercept(receiveAlarmVO, intercepts)) {
                    continue;
                }
                //判断该配置是否存在压制时效
                if (this.getIotAlarmSuppress(receiveAlarmDTO,receiveAlarmVO, intercepts)){
                    continue;
                }

                //处理运营研判,首先判断是否开启研判，并行研判
                /*if (this.doOperate(receiveAlarmVO, alarms, operations)) {
                    continue;
                }*/
                if(StringUtils.isEmpty(receiveAlarmVO.getJudgeType())){
                    receiveAlarmVO.setJudgeType(Constants.JUDGE_TYPE_COMMON);
                }
                alarms.add(receiveAlarmVO);
            }
            if (!CollectionUtils.isEmpty(intercepts)) {
                this.getAllSensorMonitorList(intercepts);
                receiveAlarmVODB.setIntercepts(intercepts);
            }
            // 批量保存告警工单信息
            successDatas = batchCreateWarningOrder(alarms);
            //物联设备告警批量上报中不确定是否存在跨月份的数据，这里当做会有跨月份的告警数据上报
            Map<String, List<ReceiveAlarmVO>> alarmsMap = alarms.stream().collect(Collectors.groupingBy(alarmVO -> alarmVO.getWarningOrderId().substring(0, 6)));
            alarmsMap.entrySet().stream().forEach(item -> {
                receiveAlarmVODB.setList(item.getValue());
            });
            // 批量保存运营研判数据
            if (!CollectionUtils.isEmpty(operations)) {
                this.getAllSensorMonitorList(operations);
                receiveAlarmVODB.setOperations(operations);
            }
            String originalJson = JSON.toJSONString(receiveAlarmVODB.getList());
            // 告警信息入库
            commonService.saveAlarm(receiveAlarmVODB, alarmTime, Constants.WARNING_SOURCE_8, originalJson, null);
            // 回填研判信息
            successDatas.addAll(operations);
            long endTime = System.currentTimeMillis();
            log.info("IotWarningOrderHandler excute cost: {}", (endTime - beginTime));
        } catch(Exception e) {
            log.info("物联设备生成告警事件异常：", e);
        }
        return successDatas;
    }

    private List<ReceiveAlarmVO> batchCreateWarningOrder(List<ReceiveAlarmVO> alarms) {
        // 批量保存告警信息
        if (!CollectionUtils.isEmpty(alarms)) {
            this.getAllSensorMonitorList(alarms);
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);
            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms,recordList);
            return alarms;
        }
        return new ArrayList<>();
    }

    //查询配置下是否存在告警压制时效
    private boolean getIotAlarmSuppress (ReceiveAlarmDTO receiveAlarmDTO,ReceiveAlarmVO receiveAlarmVO,List<ReceiveAlarmVO> intercepts){
        boolean isSuppress = false;
        ReceiveIotAlarmConfigDto iotAlarmConfigDto = receiveAlarmVO.getIotAlarmConfigDto();
        //查询告警压制时效
        if (StringUtils.isNotEmpty(iotAlarmConfigDto.getInterceptTime())){
            String key = Constants.IOT_ALARM_SUPPRESS_REDIS_KEY+"_"+iotAlarmConfigDto.getConfigId();
            Long expire = RedisUtil.getExpire(key, RedisDBType.PRIMARY);
            if (expire<=0){
                isSuppress =  false;
            }else {
                isSuppress =  true;
            }
        }
        //判断是否压制后  存放告警压制时效Redis
        List<IotAlarmSuppressVO> alarmSuppressVOList = receiveAlarmDTO.getIotAlarmSuppressVOList();
        //获取配置告警压制时效需要redis存储数据
        if (!CollectionUtils.isEmpty(alarmSuppressVOList)){
            asyncCommon.iotAlarmSuppressRedisHandle(alarmSuppressVOList);
        }
        //如果为true 存在告警压制时效放入回收站
        if (isSuppress){
            receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALARM_INTERVAL);
            intercepts.add(receiveAlarmVO);
            return true;
        }
        return false;
    }

    /**
     * 取所有要插入的物联因子/物联设备及联动摄像机
     * @return
     */
    private List<ReceiveAlarmVO> getAllSensorMonitorList(List<ReceiveAlarmVO> alarms) {
        List<String> iotList = alarms.stream().flatMap(v -> v.getIotDeviceExtraFieldVOList().stream().map(i -> i.getIotDeviceCode())).distinct().collect(Collectors.toList());
        TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
        qryCond.setAlarmReporting("1");
        qryCond.setDeviceCodeList(iotList);
         Result<List<TenantIotDeviceConfVO>> result = videoDeviceService.getTenantIotDevConfBatch(qryCond);
        List<TenantIotDeviceConfVO> mapList = new ArrayList<>();
        Map<String, List<TenantIotConfRelVO>> deviceCodeToConfRelsMap = new HashMap<>();
        if (result.getCode() == ResultCode.SUCCESS.getCode() && Objects.nonNull(result.getData())) {
            mapList = result.getData();
            deviceCodeToConfRelsMap = mapList.stream()
                    .collect(Collectors.toMap(

                            v-> v.getTenantId()+"_"+v.getIndustryCode()+"_"+v.getDeviceCode(),   // key 是 deviceCode物联设备编号
                            TenantIotDeviceConfVO::getConfRels      // value 是 confRels联动的摄像机信息
                    ));
        }
            for (ReceiveAlarmVO alarmVO : alarms) {
                if (!CollectionUtils.isEmpty(alarmVO.getIotDeviceExtraFieldVOList())) {
                    for (IotDeviceExtraFieldVO iotDeviceExtraFieldVO : alarmVO.getIotDeviceExtraFieldVOList()) {
                        //key  租户id+行业编码+物联设备编码 取出对应关联设备
                        List<TenantIotConfRelVO> confRelVOS = deviceCodeToConfRelsMap.get(alarmVO.getTenantId()+"_" + alarmVO.getIndustryCode()+"_" + iotDeviceExtraFieldVO.getIotDeviceCode());
                        List<IotDeviceExtraFieldVO.RelDeviceInfo> devList = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(confRelVOS)){
                            for (TenantIotConfRelVO relVO : confRelVOS) {
                                IotDeviceExtraFieldVO.RelDeviceInfo deviceInfo = new IotDeviceExtraFieldVO.RelDeviceInfo();
                                deviceInfo.setDeviceCode(relVO.getDeviceCode());
                                deviceInfo.setIsSecondIdentify("0");
                                devList.add(deviceInfo);
                            }
                            iotDeviceExtraFieldVO.setRelDevice(devList);
                        }
                    }
                }

                Map sensorMonitorMap = new HashMap();
                //放入因子信息
                if (!CollectionUtils.isEmpty(alarmVO.getSensorMonitorVOList())) {
                    sensorMonitorMap.put("monitors", alarmVO.getSensorMonitorVOList());
                }
                //放入物联设备信息
                if (!CollectionUtils.isEmpty(alarmVO.getIotDeviceExtraFieldVOList())) {
                    sensorMonitorMap.put("deviceInfo", alarmVO.getIotDeviceExtraFieldVOList());
                }
                //放入物联上报全部因子信息
                if (!CollectionUtils.isEmpty(alarmVO.getAllSensorMonitorVOList())) {
                    sensorMonitorMap.put("monitorsList", alarmVO.getAllSensorMonitorVOList());
                }

                alarmVO.setExtraField(JSON.toJSONString(sensorMonitorMap));

            }
        return alarms;
    }

    /**
     * 构建告警对象
     * @param receiveAlarmVO
     * @return
     */
    private ReceiveAlarmVO buildAlarmVo(ReceiveAlarmVO receiveAlarmVO, List<Map> alarmTypeList) {
        //更改工单编码生成规则 add by hued 20211103
        receiveAlarmVO.setWarningOrderId(IPKUtils.getWarnOrderId(receiveAlarmVO.getTenantId(), receiveAlarmVO.getAlarmTime()));
        receiveAlarmVO.setNotifyType(Constants.YES_FLAG);
        receiveAlarmVO.setAlarmCode(receiveAlarmVO.getWarningOrderId());
        receiveAlarmVO.setWarningSource(receiveAlarmVO.getAlarmSource());

        receiveAlarmVO.setWarningTypeId(String.valueOf(receiveAlarmVO.getAlarmType()));
        receiveAlarmVO.setEmergencyLevel(EnumUtil.AlarmLevel.GENERALLY.getCode());

        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        //receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
        receiveAlarmVO.setAlarmBody(Constants.IOT_ALARM_BODY);
        //根据检测因子回填通道编码，通道名称等信息
        this.setChannelInfo(receiveAlarmVO);
        return receiveAlarmVO;
    }

    /**
     * 回填告警事件,通道编码，通道名称相关信息
     * @return
     */
    private void setChannelInfo(ReceiveAlarmVO alarmVo) {
        StringBuilder monitorCodeSd = new StringBuilder();
        StringBuilder monitorNameSd = new StringBuilder();
        List<WarningSensorMonitorVO> sensorMonitorVOList = alarmVo.getSensorMonitorVOList();
        for (WarningSensorMonitorVO monitorVO  : sensorMonitorVOList) {
            monitorVO.setWarningOrderId(alarmVo.getWarningOrderId());
            monitorCodeSd.append(monitorVO.getMonitorCode()).append(",");
            monitorNameSd.append(monitorVO.getMonitorName()).append(",");
        }
        monitorCodeSd = monitorCodeSd.deleteCharAt(monitorCodeSd.length() - 1);
        monitorNameSd = monitorNameSd.deleteCharAt(monitorNameSd.length() - 1);
        //alarmVo.setChannelCode(monitorCodeSd.toString());
        //alarmVo.setChannelName(monitorNameSd.toString());
    }

    /**
     * 处理告警配置内存信息，MongoDB因子累计值
     * @param receiveAlarmDTO 包含告警数据和相关信息的传输对象
     * @param receiveIotAlarmVOList 物联设备告警信息列表
     * @param deviceCodeList 物联设备编码列表
     * @throws ExecutionException
     */
    private void processIotAlarmConfigurations(ReceiveAlarmDTO receiveAlarmDTO, List<ReceiveIotAlarmVO> receiveIotAlarmVOList, List<String> deviceCodeList) throws ExecutionException {
        try {
            long beginTime = System.currentTimeMillis();
            //内存取全量物联配置信息为null 则自动加载内存 DB更新信息
            String cacheKey = Constants.IOT_ALARM_CONFIG_CACHE;
            String iotConfigCache = CACHE.get(cacheKey);
            log.info("物理配置内存信息：{}", iotConfigCache);
            List<ReceiveIotAlarmConfigDto> iotConfigList = JSONArray.parseArray(iotConfigCache, ReceiveIotAlarmConfigDto.class);
            if (iotConfigList.isEmpty()) {
                log.info("没有需要生成告警的物联设备!");
                return ;
            }
            //用于接收设备编码过滤后配置
            List<ReceiveIotAlarmConfigDto> iotFilterConfigList = new ArrayList<>();
            //用于接收设备编码过滤后配置下所有规则总合
            List<ReceiveIotAlarmGroupsDto> iotFilterAllGroupList = new ArrayList<>();
            //去除物联配置中不包含这个物联设备的
            for (ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto : iotConfigList) {
                List<ReceiveIotAlarmGroupsDto> groupsDtoList = receiveIotAlarmConfigDto.getIotAlarmConfigGroupsList().stream().filter(iotGroup -> iotGroup.getDeviceList().stream()
                        .map(ReceiveIotAlarmDeviceDto::getDeviceCode)
                        .anyMatch(deviceCodeList::contains))
                        .collect(Collectors.toList());
                //如果该物联配置下的规则过滤后不存在任何一条规则，则舍去
                if (!CollectionUtils.isEmpty(groupsDtoList) && groupsDtoList.size()>0){
                    receiveIotAlarmConfigDto.setIotAlarmConfigGroupsList(groupsDtoList);
                    iotFilterConfigList.add(receiveIotAlarmConfigDto);
                    iotFilterAllGroupList.addAll(groupsDtoList);

                }
            }
            receiveAlarmDTO.setIotConfigList(iotConfigList);
            //MongoDB累计值 数据处理
            this.mongoDataHandle(receiveAlarmDTO, iotFilterAllGroupList, receiveIotAlarmVOList);
            long endTime = System.currentTimeMillis();
            log.info("IotAlarmConfigHandler excute cost: {}", (endTime - beginTime));
        } catch(Exception e) {
            log.error("物联设备批量处理物联告警配置内存及MongoDB因子累计值信息异常：{}", e);
        }
    }

    /**
     * 根据配置的物联因子阈值筛选需要生成的告警工单类型及租户,该步骤已经组装好了需要生成的告警事件
     * @param receiveAlarmDTO 包含告警数据和相关信息的传输对象
     * @param receiveIotAlarmVOList 物联设备告警信息列表
     */
    private void processAlarmEvents(ReceiveAlarmDTO receiveAlarmDTO, List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
        try{
            //获取物联配置信息
            List<ReceiveIotAlarmConfigDto> iotAlarmConfigList = receiveAlarmDTO.getIotConfigList();
            //获取订购物联设备租户信息
            List<OrderCustomerVO> customerVOList = receiveAlarmDTO.getCustomers();
            //生成告警事件的map
            Map<String, ReceiveAlarmVO> receiveAlarmVOMap = this.matchSensorMonitorConfig(receiveAlarmDTO,iotAlarmConfigList, receiveIotAlarmVOList, customerVOList);

            //获取Mongo需要累计值数据 更新Mongo累计值数据
            List<Map<String,String>> mongoFactoryList = receiveAlarmDTO.getMongoFactoryList();
            if (!CollectionUtils.isEmpty(mongoFactoryList)){
                //物联配置信息累计值入MongoDB
                asyncCommon.factoryValueMongoHandle(mongoFactoryList,StringUtils.substring(receiveIotAlarmVOList.get(0).getAlarmTime(), 0, 16));
            }
            //获取告警防抖次数数据，对应更新redis/MongoDB
            List<AntiShakeValueVO> antiShakeSaveList = receiveAlarmDTO.getAntiShakeSaveList();
            if (!CollectionUtils.isEmpty(antiShakeSaveList)){
                //获取告警防抖次数需要redis存储数据
                List<AntiShakeValueVO> antiShakeSaveRedisList = antiShakeSaveList.stream().filter(v -> Constants.ONE.equals(v.getValueType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(antiShakeSaveRedisList)){
                    asyncCommon.antiShakeValueRedisHandle(antiShakeSaveRedisList);
                }
                //获取告警防抖次数需要Mongo存储数据
                List<AntiShakeValueVO> antiShakeSaveMongoList = antiShakeSaveList.stream().filter(v -> Constants.TWO.equals(v.getValueType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(antiShakeSaveMongoList)){
                    asyncCommon.antiShakeValueMongoHandle(antiShakeSaveMongoList);
                }

            }
            //获取设备池数需要redis存储数据
            List<IotDeviceGroupsVO> deviceGroupsSaveList = receiveAlarmDTO.getDeviceGroupsSaveList();
            if (!CollectionUtils.isEmpty(deviceGroupsSaveList)){
                asyncCommon.deviceGroupsValueRedisHandle(deviceGroupsSaveList);
            }

            if (MapUtils.isEmpty(receiveAlarmVOMap)) {
                log.info("没有需要生成告警的物联设备!");
                return ;
            }

            List<ReceiveAlarmVO> valueList = receiveAlarmVOMap.values().stream().collect(Collectors.toList());
            log.info("-----------------IotAlarmConfigDealHandler receiveAlarmList------------:{}", valueList);
            receiveAlarmDTO.setReceiveAlarmVO(null);
            receiveAlarmDTO.setReceiveIotAlarmVOList(null);
            receiveAlarmDTO.setCustomers(null);
            receiveAlarmDTO.setList(valueList);
        } catch(Exception e) {
            log.error("根据配置的物联因子阈值来筛选需要给哪些租户发送告警信息异常：{}", e);
        }
    }

    /**
     * 物联设备告警批量处理地址和经纬度信息
     * @param receiveAlarmDTO 包含告警数据和相关信息的传输对象
     * @param deviceCodeList 物联设备编码列表
     */
    private void processAddressAndLocation(ReceiveAlarmDTO receiveAlarmDTO, List<String> deviceCodeList) {
        long beginTime = System.currentTimeMillis();
        List<ReceiveAlarmVO> list = receiveAlarmDTO.getList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<ReceiveAlarmVO> receiveAlarmVOList = new ArrayList<>();
        // 摄像机告警定位配置判断
        List<DeviceSiteReceiveVO> deviceSiteReceiveVOList = deviceRepository.queryIotDeviceList(deviceCodeList);
        if (!CollectionUtils.isEmpty(deviceSiteReceiveVOList)) {
            deviceSiteReceiveVOList.stream().distinct().forEach(deviceSiteVO -> {
                ReceiveAlarmVO addressAlarmVO = new ReceiveAlarmVO();
                addressAlarmVO.setLongitude(deviceSiteVO.getLongitude());
                addressAlarmVO.setLatitude(deviceSiteVO.getLatitude());
                try {
                    gisUtil.getAddressFromAmap(addressAlarmVO,null);
                    //V1.6.80 告警的事发地点默认为告警传感器在【铁塔设备管理-物联设备管理】中的“设备地址”
                    addressAlarmVO.setAddress(deviceSiteVO.getLocation());
                    List<ReceiveAlarmVO> deviceAlarmCodeList = list.stream().filter(receiveAlarmVO -> Objects.equals(deviceSiteVO.getDeviceCode(), receiveAlarmVO.getDeviceCode())).map(receiveAlarmVO -> this.buildDeviceInfo(deviceSiteVO, receiveAlarmVO, addressAlarmVO)).collect(Collectors.toList());
                    receiveAlarmVOList.addAll(deviceAlarmCodeList);
                    //组装网格信息
                    this.buildGridInfo(deviceAlarmCodeList, deviceSiteVO.getLatitude(), deviceSiteVO.getLongitude());
                } catch (Exception e) {
                    log.error("根据经纬度查询位置报错", e);
                }
            });
            receiveAlarmDTO.setList(receiveAlarmVOList);
        }
        long endTime = System.currentTimeMillis();
        log.info("IotAddressAndGpsHandler excute cost: {}", (endTime - beginTime));
    }

    /**
     * 组装设备信息
     * @param deviceSiteReceiveVO
     * @param receiveAlarmVO
     * @param addressAlarmVO
     * @return
     */
    private ReceiveAlarmVO buildDeviceInfo(DeviceSiteReceiveVO deviceSiteReceiveVO, ReceiveAlarmVO receiveAlarmVO, ReceiveAlarmVO addressAlarmVO) {
        log.info("物联设备告警批量处理地址和经纬度信息入参：{}，{}，{}", deviceSiteReceiveVO.toString(),receiveAlarmVO.toString(),addressAlarmVO.toString());
        // 获取摄像头区域
        receiveAlarmVO.setProvinceCode(deviceSiteReceiveVO.getProvinceId());
        receiveAlarmVO.setCityCode(deviceSiteReceiveVO.getCityId());
        receiveAlarmVO.setCountyCode(deviceSiteReceiveVO.getCountyId());
        receiveAlarmVO.setProvinceName(deviceSiteReceiveVO.getProvinceName());
        receiveAlarmVO.setCityName(deviceSiteReceiveVO.getCityName());
        receiveAlarmVO.setCountyName(deviceSiteReceiveVO.getCountyName());
        receiveAlarmVO.setLatitude(deviceSiteReceiveVO.getLatitude());
        receiveAlarmVO.setLongitude(deviceSiteReceiveVO.getLongitude());
        receiveAlarmVO.setDeviceCode(deviceSiteReceiveVO.getDeviceCode());
        receiveAlarmVO.setDeviceName(deviceSiteReceiveVO.getDeviceName());
        receiveAlarmVO.setTownCode(addressAlarmVO.getTownCode());
        receiveAlarmVO.setTownName(addressAlarmVO.getTownName());
        receiveAlarmVO.setAddress(addressAlarmVO.getAddress());
        return receiveAlarmVO;
    }

    /**
     * 组装网格信息
     * @param receiveAlarmVOList
     * @param latitude
     * @param longitude
     */
    private void buildGridInfo(List<ReceiveAlarmVO> receiveAlarmVOList, String latitude, String longitude) {
        log.info("组装网格信息入参：{}", JSON.toJSONString(receiveAlarmVOList));
        try{
            // 根据经纬度获取网格信息
            if (!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude)) {
                try {
                    GridByLngLatParam param = new GridByLngLatParam();
                    param.setLatitude(latitude);
                    param.setLongitude(longitude);
                    List<BaseIndustryEntity> tenantList = new ArrayList<>();
                    receiveAlarmVOList.forEach(item ->{
                        BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                        baseIndustryEntity.setIndustryCode(item.getIndustryCode());
                        baseIndustryEntity.setTenantId(item.getTenantId());
                        tenantList.add(baseIndustryEntity);
                    });
                    param.setTenantList(tenantList);
                    Result<List<GridByLngLat>> result = gridAPI.getMessageByLngLatNew(param);

                    log.info(MessageFormat.format(MessageConstants.GRID_MESSAGE, result));
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                        List<GridByLngLat> gridList = result.getData();
                        if (!CollectionUtils.isEmpty(gridList)) {
                            for (ReceiveAlarmVO receiveAlarmVO : receiveAlarmVOList) {
                                GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(receiveAlarmVO.getTenantId(), grid.getTenantId()) && Objects.equals(receiveAlarmVO.getIndustryCode(), grid.getIndustryCode()))
                                        .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                                receiveAlarmVO.setGridId(gridInfo.getGridId());
                                receiveAlarmVO.setGridName(gridInfo.getGridName());
                                receiveAlarmVO.setGridUserList(gridInfo.getUserList());
                            }
                        }
                    }
                }
                catch (Exception e) {
                    log.error(MessageConstants.GRID_ERROR_MESSAGE, e);
                }
            }
        }catch(Exception e){
            log.info("组装网格信息异常：{}", e);
        }

    }

    /**
     * MongoDB累计值 数据处理
     * @param receiveAlarmDTO
     * @param iotFilterAllGroupList
     * @param iotAlarmVOList
     */
    public void mongoDataHandle(ReceiveAlarmDTO receiveAlarmDTO, List<ReceiveIotAlarmGroupsDto> iotFilterAllGroupList, List<ReceiveIotAlarmVO> iotAlarmVOList) {
        //取出物联配置中需要累计的因子编码并去重
        List<String> factoryCode = iotFilterAllGroupList.stream()
                .flatMap(group -> group.getFactorList().stream())
                .filter(factor -> Constants.ONE.equals(factor.getValueType()))
                .map(ReceiveIotAlarmFactorDto::getFactorCode)
                .distinct()
                .collect(Collectors.toList());

        //取出物联上报信息中需要累计的值 组合MongoDB需要数据
        List<Map<String,String>> mongoFactoryList = new ArrayList<>();
        for (ReceiveIotAlarmVO receiveIotAlarmVO : iotAlarmVOList) {
            Map<String, String> map = new HashMap<>();
            for (String key : receiveIotAlarmVO.getMonitor().keySet()) {
                if (factoryCode.contains(key)){
                    map.put("deviceCode",receiveIotAlarmVO.getDeviceCode());
                    map.put("mergeTime", StringUtils.substring(receiveIotAlarmVO.getAlarmTime(), 0,16));
                    String monitorCode = "";
                    if(key.contains("#")){
                        monitorCode = key.split("#")[1];
                    }else {
                        monitorCode = key;
                    }
                    map.put(key,monitorCode);
                }
            }
            if (!CollectionUtils.isEmpty(map)){
                mongoFactoryList.add(map);
            }
        }
        receiveAlarmDTO.setMongoFactoryList(mongoFactoryList);
    }

    /**
     * 根据 物联设备/物联因子 配置阈值生成告警数量
     * @param iotConfigList
     * @param receiveIotAlarmVOList
     * @param customerVOList
     * @return
     */
    private Map<String, ReceiveAlarmVO> matchSensorMonitorConfig(ReceiveAlarmDTO receiveAlarmDTO,List<ReceiveIotAlarmConfigDto> iotConfigList, List<ReceiveIotAlarmVO> receiveIotAlarmVOList, List<OrderCustomerVO> customerVOList) {
        //处理物联设备上报的告警信息，根据配置的阈值规则和告警类型判断是否需要生成告警以及生成一条告警还是生成多条告警
        return this.dealAlarmInfo(receiveAlarmDTO,iotConfigList,receiveIotAlarmVOList, customerVOList);

    }


    /**
     * 循环处理物联设备上报的告警信息，根据配置的阈值规则和告警类型判断是否需要生成告警以及生成一条告警还是生成多条告警
     *
     遍历租户订购信息{
     (1)根据tenantId,industryCode,deviceCode过滤物联配置信息
        遍历配置信息{
        (1)查询告警联合生效设备池redis
            遍历配置下规则信息{
            (1)查询告警防抖次数redis/mongo
                 遍历上报报文设备信息{
                 (1)判断时间段是否符合(物联告警配置只在所填写的告警时间段内生效，若不填写告警日期，则在所选时间段内永久生效)
                 (2)判断告警防抖和告警生效设备组合是否符合
                    遍历上报因子信息{
                    (1)判断因子阈值范围是否符合
                    (2)组装生成的告警事件信息
                    }
                  (3)存储防抖次数，告警生效设备，告警压制数据
                 }
             }
         }
     }
     *
     * @param receiveAlarmDTO
     * @param iotConfigList
     * @param receiveIotAlarmVOList
     * @param customerVOList
     * @return
     */
    private Map<String, ReceiveAlarmVO> dealAlarmInfo(ReceiveAlarmDTO receiveAlarmDTO,List<ReceiveIotAlarmConfigDto> iotConfigList, List<ReceiveIotAlarmVO> receiveIotAlarmVOList, List<OrderCustomerVO> customerVOList) {
        long beginTime = System.currentTimeMillis();
        //这里不确定同一批次上报的物联设备告警信息是否存在相同设备的，这里当做存在处理
        Map<String, List<ReceiveIotAlarmVO>> receiveIotAlarmVOMap = receiveIotAlarmVOList.stream().collect(Collectors.groupingBy(ReceiveIotAlarmVO::getDeviceCode));
        //租户_行业版本编码_告警类型_上报告警信息编号相同生成一个告警事件
        Map<String, ReceiveAlarmVO> receiveAlarmMap = new HashMap<>();
        //存放告警防抖次数 后续统一更新存储次数
        List<AntiShakeValueVO> antiShakeSaveList = new ArrayList<>();
        //存放告警生效物联设备 后续统一更新存储到设备池
        List<IotDeviceGroupsVO> iotDeviceGroupsVOList = new ArrayList<>();
        //存放配置告警压制 后续统计更新对应配置压制时效
        List<IotAlarmSuppressVO> iotAlarmSuppressVOList = new ArrayList<>();
        //对租户信息进行处理
        for (OrderCustomerVO orderCustomerVO : customerVOList) {
            //用客户订购物联设备编码过滤物联配置
            List<ReceiveIotAlarmConfigDto> iotFilterConfigList = this.getNeedIotConfig(orderCustomerVO,iotConfigList);
            //查询该物联设备下所有的因子及对应传感器
            List<SensorMonitorRelInfoVo> factorSensorInFo = this.getFactorSensorInFo(orderCustomerVO);
            //租户下此设备下的物联配置 ， 物联规则
            for (ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto : iotFilterConfigList) {
                //物联告警判断值
                ReceiveIotAlarmBooleanDto iotAlarmBooleanDto = new ReceiveIotAlarmBooleanDto();
                //查询存储告警生效物联设备池
                IotDeviceGroupsVO iotDeviceGroupsVO = this.getIotDeviceGroup(receiveIotAlarmConfigDto,iotDeviceGroupsVOList);
                for (ReceiveIotAlarmGroupsDto groupsDto : receiveIotAlarmConfigDto.getIotAlarmConfigGroupsList()) {
                    //查询存储告警防抖次数
                    AntiShakeValueVO antiShakeValue =this.getAntiShake(groupsDto,antiShakeSaveList);
                    //租户订购设备下上报的物联设备信息
                    here:
                    for (ReceiveIotAlarmVO receiveIotAlarmVO : receiveIotAlarmVOMap.get(orderCustomerVO.getDeviceCode())) {
                        //判断是否配置了告警时间,告警时间，时间段是否符合,时间段不符合不走后续判断
                        boolean judgeTime = this.judgeAlarmTime(receiveIotAlarmConfigDto, receiveIotAlarmVO);
                        if (!judgeTime){
                            break here;
                        }
                        //判断告警防抖和告警生效设备组合是否符合
                        String fitIotDeviceGroup =this.judgeAntiShakeAndDeviceGroup(antiShakeValue,orderCustomerVO,iotAlarmBooleanDto,groupsDto,iotDeviceGroupsVO,receiveIotAlarmConfigDto);
                        String uuid = UUID.randomUUID().toString();
                        //设备池需要本次上报因子code和值 集合
                        List<IotDeviceGroupsVO.IotFactor> reportIotFactorList = new ArrayList<>();
                        //透传字段需要本次上报全部因子集合 用于前端展示全部因子及符合阈值因子高亮展示
                        List<WarningAllSensorMonitorVO.IotFactor> allSensorMonitorVOList = new ArrayList<>();
                        //本次上报的因子信息
                        for (Map.Entry<String, Object> entry : receiveIotAlarmVO.getMonitor().entrySet()) {
                            //处理上报因子信息，分割因子编码及传感器编码
                            ReceiveIotAlarmFactorDto factorSensorDto = this.handleReceiveFactory(entry);
                            List<String> factoryCodeList = factorSensorInFo.stream().map(v -> v.getMonitorCode()).collect(Collectors.toList());
                            //如果该物联设备有物联因子，且上报物联因子存在其中
                            if (!CollectionUtils.isEmpty(factoryCodeList) && factoryCodeList.contains(factorSensorDto.getFactorCode())){
                                ReceiveIotAlarmFactorDto configVO = this.judgeThresholdValue(factorSensorInFo,factorSensorDto, orderCustomerVO, receiveIotAlarmConfigDto,groupsDto, receiveIotAlarmVO.getAlarmTime(), receiveIotAlarmVO);
                                //添加本次上报因子
                                allSensorMonitorVOList.add(this.buildAllSensorMonitor(factorSensorInFo,reportIotFactorList,factorSensorDto, configVO));
                                //因子关系为且 删除这一条对应租户数据
                                if ("1".equals(configVO.getRelation()) && !configVO.isTrue()) {
                                    iotAlarmBooleanDto.setTrueGroup(false);
                                    this.deleteKey(receiveAlarmMap, configVO, orderCustomerVO, receiveIotAlarmVO,uuid);
                                    //防抖次数计算，用于后续存储防抖次数累计数据
                                    this.insertAntiShake(antiShakeValue,groupsDto,iotAlarmBooleanDto,antiShakeSaveList);
                                    break here;
                                }
                                //组装生成的告警事件信息,注意一个告警信息如果上报了多个检测因子,需要判断这些检测因子关联的告警类型是否相同,如果相同则生成
                                this.buildAlarmInfo(receiveIotAlarmConfigDto,iotAlarmBooleanDto,receiveAlarmMap, configVO, orderCustomerVO, receiveIotAlarmVO, String.valueOf(entry.getValue()), uuid);
                            }
                        }
                        //防抖次数计算，用于后续存储防抖次数累计数据
                        this.insertAntiShake(antiShakeValue,groupsDto,iotAlarmBooleanDto,antiShakeSaveList);
                        //告警生效设备，用于后续存储设备池
                        this.insertDeviceGroups(receiveIotAlarmVO,reportIotFactorList,receiveIotAlarmConfigDto,iotDeviceGroupsVO,iotAlarmBooleanDto,iotDeviceGroupsVOList, orderCustomerVO.getDeviceCode());
                        //告警压制，用于后续存储告警压制时效
                        this.insertIotAlarmSuppress(receiveIotAlarmConfigDto,iotAlarmBooleanDto,iotAlarmSuppressVOList);
                        //构建透传字段里的 物联设备信息及上报全部因子信息
                        this.buildIotDeviceExtraField(iotDeviceGroupsVO,allSensorMonitorVOList,receiveAlarmMap,orderCustomerVO,receiveIotAlarmConfigDto, receiveIotAlarmVO, uuid,fitIotDeviceGroup);
                    }
                }
            }
        }
        //存入告警防抖需要存储集合
        receiveAlarmDTO.setAntiShakeSaveList(antiShakeSaveList);
        //存入设备池需要存储集合
        receiveAlarmDTO.setDeviceGroupsSaveList(iotDeviceGroupsVOList);
        //存入各个配置的告警压制时效需要存储集合
        receiveAlarmDTO.setIotAlarmSuppressVOList(iotAlarmSuppressVOList);
        long endTime = System.currentTimeMillis();
        log.info("IotMonitorConfigDealHandler dealAlarmInfo cost: {}", (endTime - beginTime));
        return receiveAlarmMap;
    }

    //处理上报因子信息，分割因子编码及传感器编码
    private ReceiveIotAlarmFactorDto handleReceiveFactory(Map.Entry<String, Object> entry){
        //上报检测因子值
        String monitorValue = String.valueOf(entry.getValue());
        //上报检测因子编码
        String monitorCode = "";
        //上报传感器
        String sensorCode = "";
        String key = entry.getKey();
        //上报因子key如果包含#,说明该设备有传感器
        if (key.contains("#")){
            String[] split = key.split("#");
            monitorCode = split[1];
            sensorCode = split[0];
        }else {
            monitorCode = key;
        }
        ReceiveIotAlarmFactorDto factorDto = new ReceiveIotAlarmFactorDto();
        factorDto.setFactorCode(monitorCode);
        factorDto.setFactorValue(monitorValue);
        factorDto.setSensorCode(sensorCode);
        return  factorDto;
    }

    private List<IotFactorVo> getMonitorInfo(List<WarningAllSensorMonitorVO> deviceFactoryList){
        List<String> factoryCodeList = deviceFactoryList.stream().flatMap(v-> v.getMonitorList().stream().map(m-> m.getFactorCode())).distinct().collect(Collectors.toList());
        return  iotAlarmConfigMapper.getMonitorInfo(factoryCodeList);
    }

    private WarningAllSensorMonitorVO.IotFactor buildAllSensorMonitor(List<SensorMonitorRelInfoVo> factorSensorInFo,List<IotDeviceGroupsVO.IotFactor> reportIotFactorList,ReceiveIotAlarmFactorDto factorSensorDto,ReceiveIotAlarmFactorDto configVO){
        //上报物联因子传感器编码
        String sensorName ="";
        String sensorCode = factorSensorDto.getSensorCode();
        SensorMonitorRelInfoVo sensorMonitorRelInfoVo = new SensorMonitorRelInfoVo();
        //通过传感器编码过滤 获取传感器名称
        if (!CollectionUtils.isEmpty(factorSensorInFo)){
            sensorMonitorRelInfoVo = factorSensorInFo.stream().filter(v -> sensorCode.equals(v.getSensorCode())).findFirst().orElse(null);
        }
        //获取传感器名称不为空
        if (ObjectUtil.isNotNull(sensorMonitorRelInfoVo)){
            sensorName = sensorMonitorRelInfoVo.getSensorName();
        }
        if (StringUtils.isEmpty(sensorName)){
            sensorName = factorSensorDto.getSensorCode();
        }
        IotDeviceGroupsVO.IotFactor iotFactor = new IotDeviceGroupsVO.IotFactor();
        iotFactor.setFactorCode(factorSensorDto.getFactorCode());
        iotFactor.setFactorValue(factorSensorDto.getFactorValue());
        iotFactor.setSensorCode(factorSensorDto.getSensorCode());
        iotFactor.setSensorName(sensorName);
        reportIotFactorList.add(iotFactor);

        WarningAllSensorMonitorVO.IotFactor monitorVO = new WarningAllSensorMonitorVO.IotFactor();
        monitorVO.setFactorCode(factorSensorDto.getFactorCode());
        monitorVO.setFactorValue(factorSensorDto.getFactorValue());
        monitorVO.setSensorCode(factorSensorDto.getSensorCode());
        monitorVO.setSensorName(sensorName);
        if (!configVO.isTrue()) {
            monitorVO.setHighlight(false);
        }else {
            monitorVO.setHighlight(true);
        }
        return monitorVO;

    }

    //判断告警防抖和告警生效设备组合是否符合
    private String judgeAntiShakeAndDeviceGroup(AntiShakeValueVO antiShakeValue,OrderCustomerVO orderCustomerVO,ReceiveIotAlarmBooleanDto iotAlarmBooleanDto,ReceiveIotAlarmGroupsDto groupsDto,IotDeviceGroupsVO iotDeviceGroupsVO,ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto){
        //存放透传字段物联设备信息 默认当前订购物联设备
        String fitIotDeviceGroup = orderCustomerVO.getDeviceCode();
        //判断是否符合告警防抖规则
        iotAlarmBooleanDto.setAntiShake(this.judgeAntiShake(antiShakeValue, groupsDto));
        //如果告警防抖规则符合则判断是否符合告警生效设备组合,防抖和设备组合都满组才可生成告警
        if (iotAlarmBooleanDto.isAntiShake()){
            //如果满足告警生效物联设备组合 返回逗号分割多个物联设备
            fitIotDeviceGroup = this.judgeIotDeviceGroups(iotAlarmBooleanDto, iotDeviceGroupsVO, receiveIotAlarmConfigDto, orderCustomerVO.getDeviceCode());
        }
        //是否符合物联规则下的阈值范围  关系为且则有一条因子阈值范围不符合即为false，为或有一条因子阈值范围符合即为true
        iotAlarmBooleanDto.setTrueGroup(false);
        return fitIotDeviceGroup;
    }

    //根据租户订购物联设备取出需要用的物联告警配置
    private List<ReceiveIotAlarmConfigDto> getNeedIotConfig(OrderCustomerVO orderCustomerVO,List<ReceiveIotAlarmConfigDto> iotConfigList){
        //过滤物联配置
        String tenantId = orderCustomerVO.getTenantId();
        String industryCode = orderCustomerVO.getIndustryCode();
        String deviceCode = orderCustomerVO.getDeviceCode();
        //通过tenantId,industryCode,deviceCode过滤配置信息
        List<ReceiveIotAlarmConfigDto> filteredConfigList = iotConfigList.stream()
                .filter(config -> config.getTenantId().equals(tenantId) && config.getIndustryCode().equals(industryCode))
                .collect(Collectors.toList());
        //用客户该设备编码过滤后配置
        List<ReceiveIotAlarmConfigDto> iotFilterConfigList = new ArrayList<>();
        for (ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto : filteredConfigList) {
            List<ReceiveIotAlarmGroupsDto> groupsDtoList = receiveIotAlarmConfigDto.getIotAlarmConfigGroupsList().stream().filter(iotGroup -> iotGroup.getDeviceList().stream()
                    .anyMatch(v-> v.getDeviceCode().equals(deviceCode)))
                    .collect(Collectors.toList());
            //如果该物联配置下的规则过滤后不存在任何一条规则，则舍去
            if (!CollectionUtils.isEmpty(groupsDtoList) && groupsDtoList.size()>0){
                receiveIotAlarmConfigDto.setIotAlarmConfigGroupsList(groupsDtoList);
                iotFilterConfigList.add(receiveIotAlarmConfigDto);
            }
        }
        return iotFilterConfigList;
    }




    //根据租户订购物联设备取出需要用的物联告警配置
    private List<SensorMonitorRelInfoVo> getFactorSensorInFo(OrderCustomerVO orderCustomerVO){
        String deviceCode = orderCustomerVO.getDeviceCode();
        List<String> deviceCodeList = new ArrayList<>();
        deviceCodeList.add(deviceCode);
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        deviceReqDTO.setDeviceCodes(deviceCodeList);
        log.info("根据设备编码查询因子及对应传感器信息入参{}",deviceReqDTO);
        Result<List<SensorMonitorRelInfoVo>> result = iSensorDeviceServiceMigrate.querySensorMonitorRelInfo(deviceReqDTO);
        log.info("根据设备编码查询因子及对应传感器信息返回结果{}", JSONArray.toJSONString(result));
        List<SensorMonitorRelInfoVo> iotAlarmSensorList = new ArrayList<>();
        if (result!= null && CollectionUtil.isNotEmpty(result.getData())){
            iotAlarmSensorList = result.getData();
        }
        return iotAlarmSensorList;
    }


    /**
     * 查询设备池(设备池:该物联配置下有效的设备)
     * 有效设备规则(1.存在告警规则中所勾选设备
     *           2.满足告警时间设置范围
     *           3.满足阈值范围
     *           4.满足告警防抖
     *          )
     * 存储结构为
      {
          "configId":"",//配置id
          "deviceGroupList":[
            {
                "deviceCode":"",//物联设备编码
                "insertTime":"" //物联设备最新生效时间
            }
          ]
      }
     *
     * @param configDto
     */
    private IotDeviceGroupsVO getIotDeviceGroup (ReceiveIotAlarmConfigDto configDto,List<IotDeviceGroupsVO> iotDeviceGroupsVOList){
        IotDeviceGroupsVO iotDeviceGroupsVO = new IotDeviceGroupsVO();
        //查询存储设备池
        if (StringUtils.isNotEmpty(configDto.getDeviceGroupsJson()) && !Constants.EMPTY_STRING.equals(configDto.getDeviceGroupsJson())){
            String key = Constants.IOT_DEVICE_GROUPS_REDIS_KEY+"_"+configDto.getConfigId();
            log.info("设备池key{}", key);
            String jsonString = RedisUtil.get(key, String.class, RedisDBType.PRIMARY);
            log.info(key, jsonString);
            List<IotDeviceGroupsVO.DeviceGroup> list =JSON.parseObject(jsonString, new TypeReference<List<IotDeviceGroupsVO.DeviceGroup>>(){});
            iotDeviceGroupsVO.setDeviceGroupList(list);
            iotDeviceGroupsVO.setConfigId(configDto.getConfigId());
            List<IotDeviceGroupsVO> deviceGroupsVOList = iotDeviceGroupsVOList.stream().filter(v -> v.getConfigId().equals(configDto.getConfigId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deviceGroupsVOList) && deviceGroupsVOList.size()>0){
                iotDeviceGroupsVO.getDeviceGroupList().addAll(deviceGroupsVOList.get(0).getDeviceGroupList());
            }
        }
        log.info("物联设备生效设备池{}", JSONArray.toJSONString(iotDeviceGroupsVO));
        return iotDeviceGroupsVO;
    }

    //查询存储告警防抖次数
    private AntiShakeValueVO getAntiShake (ReceiveIotAlarmGroupsDto groupsDto,List<AntiShakeValueVO> antiShakeSaveList){
        AntiShakeValueVO antiShakeValue = new AntiShakeValueVO();
        //查询存储告警防抖次数，如果后续存入redis和mongo的告警防抖数据 通过配置ID和规则ID匹配存在，就取现有数据，否则重新查取
        if ("1".equals(groupsDto.getAntiShakeType())){
            List<AntiShakeValueVO> antiShakeValueVOList = antiShakeSaveList.stream().filter(v -> Constants.ONE.equals(v.getValueType()) && groupsDto.getId().equals(v.getGroupId()) && groupsDto.getIotConfigId().equals(v.getConfigId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(antiShakeValueVOList) && antiShakeValueVOList.size()>0){
                antiShakeValue.setTriggerValue(antiShakeValueVOList.get(0).getTriggerValue());
            }else {
                //key 配置id+规则id
                String key = Constants.ANTI_SHAKE_REDIS_KEY+"_"+groupsDto.getIotConfigId()+"_"+groupsDto.getId();
                //取出防抖时间内触发次数 再加一放入缓存中
                BigDecimal num = RedisUtil.get(key, BigDecimal.class, RedisDBType.PRIMARY);
                log.info(key, num);
                //如果历史存储过该规则防抖数值，则用历史存储中的值，否则从0开始
                antiShakeValue.setTriggerValue(num!=null?num:new BigDecimal(0));
            }
        }else if ("2".equals(groupsDto.getAntiShakeType()) || "3".equals(groupsDto.getAntiShakeType())){
            List<AntiShakeValueVO> antiShakeValueVOList = antiShakeSaveList.stream().filter(v -> Constants.TWO.equals(v.getValueType()) && groupsDto.getId().equals(v.getGroupId()) && groupsDto.getIotConfigId().equals(v.getConfigId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(antiShakeValueVOList) && antiShakeValueVOList.size()>0){
                antiShakeValue.setContinueValue(antiShakeValueVOList.get(0).getContinueValue());
                antiShakeValue.setSuddenValue(antiShakeValueVOList.get(0).getSuddenValue());
            }else {
                AntiShakeValueVO shakeValue = iotConfigMongoMapper.getMongoAntiShakeValue(groupsDto);
                //如果历史存储过该规则防抖数值，则用历史存储中的值，否则从0开始
                antiShakeValue.setContinueValue(shakeValue !=null && shakeValue.getContinueValue()!=null?shakeValue.getContinueValue():new BigDecimal(0));
                antiShakeValue.setSuddenValue(shakeValue !=null && shakeValue.getSuddenValue()!=null?shakeValue.getSuddenValue():new BigDecimal(0));
            }
              }
        log.info("告警防抖存储值{}", JSONArray.toJSONString(antiShakeValue));
        return antiShakeValue;
    }

    //如果有配置告警防抖，判断告警防抖是否符合
    private boolean judgeAntiShake (AntiShakeValueVO antiShakeValue,ReceiveIotAlarmGroupsDto groupsDto){
        //判断是否配置告警防抖，没配置则跳过为true
        if (StringUtils.isNotEmpty(groupsDto.getAntiShakeNum())){
            //比较值
            int compare = 0;
            //告警防抖次数
            BigDecimal numValue = new BigDecimal(0);
            //如果历史存储过该规则防抖数值，则用历史存储中的值，否则从0开始 ，假设上报因子符合阈值 (触发次数/持续超标)＋1次数
            if (ObjectUtil.isNotNull(antiShakeValue)){
                //告警防抖比较    1:触发次数 2:持续超标 3:突然超标
                if ("1".equals(groupsDto.getAntiShakeType())){
                    numValue = antiShakeValue.getTriggerValue().add(new BigDecimal(1));
                }else if ("2".equals(groupsDto.getAntiShakeType())){
                    numValue = antiShakeValue.getContinueValue().add(new BigDecimal(1));
                }else if ("3".equals(groupsDto.getAntiShakeType())){
                    numValue = antiShakeValue.getSuddenValue();
                }
            }
            //比较实际防抖次数 和 规则中的防抖次数比较  大于则符合否则不符合告警防抖
            compare = numValue.compareTo(new BigDecimal(groupsDto.getAntiShakeNum()));
            if (compare >= 0){
                return true;
            }else {
                return false;
            }
        }
        return true;
    }

    //如果有配置告警生效物联设备组合，判断告警生效物联设备组合是否符合
    private String judgeIotDeviceGroups (ReceiveIotAlarmBooleanDto iotAlarmBooleanDto, IotDeviceGroupsVO iotDeviceGroupsVO, ReceiveIotAlarmConfigDto configDto, String deviceCode){
        //判断是否配置告警防抖，没配置则跳过为true
        if (StringUtils.isNotEmpty(configDto.getDeviceGroupsJson()) && !Constants.EMPTY_STRING.equals(configDto.getDeviceGroupsJson())){
            //设备池是否 符合告警生效设备组合
            //配置下告警生效设备组合
            List<String> configDeviceList = JSONArray.parseArray(configDto.getDeviceGroupsJson(), String.class);
            //获取五分钟前的时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS);
            LocalDateTime beforeFiveDate = LocalDateTime.parse(LocalDateTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS)),formatter);
            List<String> realDeviceList = new ArrayList<>();
            if (ObjectUtil.isNotNull(iotDeviceGroupsVO) && !CollectionUtils.isEmpty(iotDeviceGroupsVO.getDeviceGroupList())){
                realDeviceList = iotDeviceGroupsVO.getDeviceGroupList().stream().filter(v->LocalDateTime.parse(v.getInsertTime(),formatter).isAfter(beforeFiveDate)).map(v -> v.getDeviceCode()).collect(Collectors.toList());
            }
            //实际存储告警生效设备池 且插入时间在当前时间前五分钟之内
            //假设符合规则，实际存储告警生效设备组合加上告警上报的物联设备
            realDeviceList.add(deviceCode);
            //判断实际存储告警生效设备池中是否全部包含配置中的设备组合
            for (String codeList : configDeviceList) {
                boolean isTrue = true;
                String[] split = codeList.split(",");
                for (String code : split) {
                    //有一个设备不包含 就不符合
                    if (!realDeviceList.contains(code)){
                        isTrue = false;
                    }
                }
                if (isTrue){
                    iotAlarmBooleanDto.setIotDeviceGroup(true);
                    return codeList;
                }
            }
        }else {
            iotAlarmBooleanDto.setIotDeviceGroup(true);
        }
        return deviceCode;
    }

    //防抖次数计算，用于后续存储防抖次数累计数据
    private void insertAntiShake (AntiShakeValueVO antiShakeValue,ReceiveIotAlarmGroupsDto groupsDto,ReceiveIotAlarmBooleanDto iotAlarmBooleanDto,List<AntiShakeValueVO> antiShakeSaveList){
        //如果告警防抖类型不为空 对于各类型防抖类型累计数值 1:触发次数 2:持续超标 3:突然超标
        String shakeType = groupsDto.getAntiShakeType();
        if (StringUtils.isNotEmpty(shakeType)){
            //比较值
            int compare = 0;
            //告警防抖次数
            BigDecimal numValue = new BigDecimal(0);
            //  1:触发次数 2:持续超标 3:突然超标
            if ("1".equals(groupsDto.getAntiShakeType())){
                numValue = antiShakeValue.getTriggerValue();
                //isTrueGroup为true 说明符合阈值 次数＋1 ，否则不变
                if (iotAlarmBooleanDto.isTrueGroup()){
                    numValue = numValue.add(new BigDecimal(1));
                    antiShakeValue.setTriggerValue(numValue);
                }
                antiShakeValue.setValueType("1");
                //通过告警防抖时间 和 时间单位 转换为秒作为redis过期时间
                long seconds = this.convertToSeconds(groupsDto.getAntiShakeTime(), groupsDto.getAntiShakeTimeUnit());
                antiShakeValue.setTimeoutValue(seconds);
            }else if ("2".equals(groupsDto.getAntiShakeType())){
                numValue = antiShakeValue.getContinueValue();
                //比较实际防抖次数(历史+本次1) 和 规则中的防抖次数比较
                compare = numValue.add(new BigDecimal(1)).compareTo(new BigDecimal(groupsDto.getAntiShakeNum()));
                //isTrueGroup为false 说明不符合阈值，将不再连续符合阈值
                //或者 连续次数等于规则中的次数(如果符合阈值将大于等于规则中次数产生告警) 此时连续次数将置为0
                //否则 连续次数＋1
                if (!iotAlarmBooleanDto.isTrueGroup() || compare>=0 ){
                    numValue = new BigDecimal(0);
                }else {
                    numValue = numValue.add(new BigDecimal(1));
                }
                antiShakeValue.setContinueValue(numValue);
                antiShakeValue.setValueType("2");
            }else if ("3".equals(groupsDto.getAntiShakeType())){
                numValue = antiShakeValue.getSuddenValue();
                //isTrueGroup为true 说明符合阈值，将不再连续不符合阈值  此时连续次数将置为0
                //否则 连续次数＋1
                if (iotAlarmBooleanDto.isTrueGroup()){
                    numValue = new BigDecimal(0);
                }else {
                    numValue = numValue.add(new BigDecimal(1));
                }
                antiShakeValue.setSuddenValue(numValue);
                antiShakeValue.setValueType("2");
            }
            antiShakeValue.setConfigId(groupsDto.getIotConfigId());
            antiShakeValue.setGroupId(groupsDto.getId());
            //删除该规则Id旧数据， 放入该规则Id新的数据
            for (int i = antiShakeSaveList.size() - 1; i >= 0; i--) {
                if (antiShakeSaveList.get(i).getGroupId().equals(groupsDto.getId())) {
                    antiShakeSaveList.remove(i);
                }
            }
            antiShakeSaveList.add(antiShakeValue);
        }
    }

    //告警生效设备池，用于后续存储告警生效设备
    private void insertDeviceGroups (ReceiveIotAlarmVO receiveIotAlarmVO,List<IotDeviceGroupsVO.IotFactor> reportIotFactorList,ReceiveIotAlarmConfigDto configDto,IotDeviceGroupsVO iotDeviceGroupsVO,ReceiveIotAlarmBooleanDto iotAlarmBooleanDto,List<IotDeviceGroupsVO> iotDeviceGroupsVOList,String deviceCode){
        String alarmTime = receiveIotAlarmVO.getAlarmTime();
        //如果符合规则 更新设备池
        if (StringUtils.isNotEmpty(configDto.getDeviceGroupsJson()) && !Constants.EMPTY_STRING.equals(configDto.getDeviceGroupsJson()) && iotAlarmBooleanDto.isAntiShake() && iotAlarmBooleanDto.isTrueGroup()){
            //获取当前时间
            LocalDateTime nowDate = LocalDateTime.now();
            String insertTime = nowDate.format(DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS));
            //判断设备池中是否存在上报设备，有则更新，无则新增
            boolean isHave = false;
            if (!CollectionUtils.isEmpty(iotDeviceGroupsVO.getDeviceGroupList())){
                 isHave = iotDeviceGroupsVO.getDeviceGroupList().stream().anyMatch(v -> deviceCode.equals(v.getDeviceCode()));
                if (isHave){
                    iotDeviceGroupsVO.getDeviceGroupList().forEach(v->{
                        if (deviceCode.equals(v.getDeviceCode())){
                            v.setInsertTime(insertTime);
                            v.setAlarmTime(alarmTime);
                            v.setMonitorList(reportIotFactorList);
                        }
                    });
                }else {
                    IotDeviceGroupsVO.DeviceGroup deviceGroup = new IotDeviceGroupsVO.DeviceGroup();
                    deviceGroup.setDeviceCode(deviceCode);
                    deviceGroup.setInsertTime(insertTime);
                    deviceGroup.setAlarmTime(alarmTime);
                    deviceGroup.setMonitorList(reportIotFactorList);
                    iotDeviceGroupsVO.getDeviceGroupList().add(deviceGroup);
                }
            }else {
                List<IotDeviceGroupsVO.DeviceGroup> groupList = new ArrayList<>();
                IotDeviceGroupsVO.DeviceGroup deviceGroup = new IotDeviceGroupsVO.DeviceGroup();
                deviceGroup.setDeviceCode(deviceCode);
                deviceGroup.setInsertTime(insertTime);
                deviceGroup.setAlarmTime(alarmTime);
                deviceGroup.setMonitorList(reportIotFactorList);
                groupList.add(deviceGroup);
                iotDeviceGroupsVO.setDeviceGroupList(groupList);
            }

            iotDeviceGroupsVOList.add(iotDeviceGroupsVO);
        }
    }


    //告警生效设备池，用于后续存储告警生效设备
    private void insertIotAlarmSuppress (ReceiveIotAlarmConfigDto configDto , ReceiveIotAlarmBooleanDto iotAlarmBooleanDto, List<IotAlarmSuppressVO> iotAlarmSuppressVOList) {
        //如果配置告警压制时间不为空，且符合告警防抖，阈值范围，告警生效设备组合等所有条件 产生告警压制
        if (StringUtils.isNotEmpty(configDto.getInterceptTime()) && iotAlarmBooleanDto.isAntiShake() && iotAlarmBooleanDto.isTrueGroup() && iotAlarmBooleanDto.isIotDeviceGroup()) {
            //判断是否已存在该配置告警压制时效，无则新增
            boolean isHave = iotAlarmSuppressVOList.stream().anyMatch(v -> configDto.getConfigId().equals(v.getConfigId()));
            if (!isHave){
                IotAlarmSuppressVO alarmSuppressVO = new IotAlarmSuppressVO();
                alarmSuppressVO.setConfigId(configDto.getConfigId());
                long time = this.convertToSeconds(configDto.getInterceptTime(), configDto.getInterceptTimeUnit());
                alarmSuppressVO.setSuppressTime(time);
                iotAlarmSuppressVOList.add(alarmSuppressVO);
            }
        }
    }

    //构建透传字段里的 物联设备信息
    private void buildIotDeviceExtraField (IotDeviceGroupsVO iotDeviceGroupsVO,List<WarningAllSensorMonitorVO.IotFactor> allSensorMonitorVOList,Map<String, ReceiveAlarmVO> receiveAlarmMap,OrderCustomerVO orderCustomerVO,ReceiveIotAlarmConfigDto configVO, ReceiveIotAlarmVO receiveIotAlarmVO,String uuid,String fitIotDeviceGroup) {
        String receiveAlarmKey = orderCustomerVO.getTenantId() + "_" + orderCustomerVO.getIndustryCode() + "_" + orderCustomerVO.getDeviceCode() + "_" + configVO.getIotAlarmType() + "_" + receiveIotAlarmVO.getAlarmTime() + "_" + uuid;
        //获取构建过的告警信息
        ReceiveAlarmVO receiveAlarmVO = receiveAlarmMap.get(receiveAlarmKey);
        //如果构建告警事件信息不为空  透传字段放入物联设备信息
        if (null != receiveAlarmVO) {
            List<IotDeviceExtraFieldVO> iotDeviceExtraFieldVOList = new ArrayList<>();
            String[] split = fitIotDeviceGroup.split(",");
            for (String deviceCode : split) {
                IotDeviceExtraFieldVO extraFieldVO = new IotDeviceExtraFieldVO();
                extraFieldVO.setIotDeviceCode(deviceCode);
                iotDeviceExtraFieldVOList.add(extraFieldVO);
            }
            receiveAlarmVO.setIotDeviceExtraFieldVOList(iotDeviceExtraFieldVOList);
            List<WarningAllSensorMonitorVO> deviceFactoryList = new ArrayList<>();
            //本次上报因子信息
            WarningAllSensorMonitorVO thisDeviceFactory = new WarningAllSensorMonitorVO();
            thisDeviceFactory.setDeviceCode(orderCustomerVO.getDeviceCode());
            thisDeviceFactory.setMonitorTime(receiveIotAlarmVO.getAlarmTime());
            thisDeviceFactory.setMonitorList(allSensorMonitorVOList);
            deviceFactoryList.add(thisDeviceFactory);
            //如果设备池不为空，把除了本次上报外的设备因子信息存入
            if (ObjectUtil.isNotNull(iotDeviceGroupsVO) && !CollectionUtils.isEmpty(iotDeviceGroupsVO.getDeviceGroupList())){
                for (IotDeviceGroupsVO.DeviceGroup deviceGroup : iotDeviceGroupsVO.getDeviceGroupList()) {
                    if (!deviceGroup.getDeviceCode().equals(orderCustomerVO.getDeviceCode())){
                        WarningAllSensorMonitorVO deviceFactory = new WarningAllSensorMonitorVO();
                        deviceFactory.setDeviceCode(deviceGroup.getDeviceCode());
                        deviceFactory.setMonitorTime(deviceGroup.getAlarmTime());
                        for (IotDeviceGroupsVO.IotFactor iotFactor : deviceGroup.getMonitorList()) {
                            WarningAllSensorMonitorVO.IotFactor factor = new WarningAllSensorMonitorVO.IotFactor();
                            factor.setFactorCode(iotFactor.getFactorCode());
                            factor.setFactorValue(iotFactor.getFactorValue());
                            factor.setSensorCode(iotFactor.getSensorCode());
                            factor.setSensorName(iotFactor.getSensorName());
                            deviceFactory.getMonitorList().add(factor);
                        }
                        deviceFactoryList.add(deviceFactory);
                    }

                }
            }

            //根据因子编码查询因子信息 匹配因子名称和因子单位及对应的传感器编码和名称
            List<IotFactorVo> monitorInfo = this.getMonitorInfo(deviceFactoryList);
            for (WarningAllSensorMonitorVO deviceFactory : deviceFactoryList) {
                for (WarningAllSensorMonitorVO.IotFactor iotFactor : deviceFactory.getMonitorList()) {
                    //通过传感器编码过滤 获取传感器名称
                    for (IotFactorVo factorVo : monitorInfo) {
                        if (factorVo.getFactorCode().equalsIgnoreCase(iotFactor.getFactorCode())) {
                            iotFactor.setFactorUnit(factorVo.getMonitorUnits());
                            iotFactor.setFactorName(factorVo.getFactorName());
                        }
                    }

                }
            }
            receiveAlarmVO.setAllSensorMonitorVOList(deviceFactoryList);
            receiveAlarmMap.put(receiveAlarmKey, receiveAlarmVO);
        }
    }
    //时间 和 时间单位 转换为秒
    public long convertToSeconds(String time, String unit) {
        if ("day".equals(unit)){
            return Long.parseLong(time) * 24 * 60 * 60;
        }else if ("h".equals(unit)){
            return Long.parseLong(time) * 60 * 60;
        }else if ("min".equals(unit)){
            return Long.parseLong(time) * 60;
        }else if ("s".equals(unit)){
            return Long.parseLong(time);
        }
        return Long.parseLong(time);
    }

    /**
     * 同一个告警信息下,租户_行业版本编码_设备编码_检测因子上报时间_告警类型相同,则生成一个告警事件,告警类型不同则生成多个
     * @param receiveAlarmMap    告警事件map
     * @param configVO           物联因子配置阈值信息
     * @param orderCustomerVO   租户信息
     * @param receiveIotAlarmVO  接受物联设备报文
     * @param monitorValue  检测因子值
     */
    private void buildAlarmInfo(ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto,ReceiveIotAlarmBooleanDto iotAlarmBooleanDto,Map<String, ReceiveAlarmVO> receiveAlarmMap, ReceiveIotAlarmFactorDto configVO, OrderCustomerVO orderCustomerVO, ReceiveIotAlarmVO receiveIotAlarmVO, String monitorValue, String uuid) {
        if (!configVO.isTrue()) {
            return;
        }
        //符合因子范围
        iotAlarmBooleanDto.setTrueGroup(true);
        //不符合告警防抖 或 不符合告警生效设备组合
        if (!iotAlarmBooleanDto.isIotDeviceGroup() || !iotAlarmBooleanDto.isAntiShake()){
            return;
        }
        String buildKey = orderCustomerVO.getTenantId() + "_" + orderCustomerVO.getIndustryCode() + "_" + orderCustomerVO.getDeviceCode() + "_" + configVO.getIotAlarmType() + "_" + receiveIotAlarmVO.getAlarmTime();
        String receiveAlarmKey = orderCustomerVO.getTenantId() + "_" + orderCustomerVO.getIndustryCode() + "_" + orderCustomerVO.getDeviceCode() + "_" + configVO.getIotAlarmType() + "_" + receiveIotAlarmVO.getAlarmTime() + "_" + uuid;
        //判断是否有相同物联告警配置下的不同规则生成告警，如果多规则都满足生成条件，则只保留第一个匹配到的规则
        for (String key : receiveAlarmMap.keySet()) {
            if (!receiveAlarmKey.equals(key) && key.contains(buildKey)){
                return;
            }
        }
        //针对上报的同一个告警信息
        ReceiveAlarmVO receiveAlarmVO = receiveAlarmMap.get(receiveAlarmKey);
        //如果为空构建告警事件信息
        if (null == receiveAlarmVO) {
            receiveAlarmVO = this.buildReceiveAlarmVO(receiveIotAlarmConfigDto,configVO, orderCustomerVO, receiveIotAlarmVO);
            List<WarningSensorMonitorVO> sensorMonitorVOList = new ArrayList<>();
            sensorMonitorVOList.add(this.buildSensorMonitor(configVO, monitorValue));
            receiveAlarmVO.setSensorMonitorVOList(sensorMonitorVOList);
            receiveAlarmVO.setIotAlarmConfigDto(receiveIotAlarmConfigDto);
            receiveAlarmMap.put(receiveAlarmKey, receiveAlarmVO);
        }
        //不为空，告警事件下增加物联因子信息
        else {
            receiveAlarmVO.getSensorMonitorVOList().add(this.buildSensorMonitor(configVO, monitorValue));
        }
    }


    /**
     * 构建告警事件参数
     * @param configVO
     * @param orderCustomerVO
     * @param receiveIotAlarmVO
     * @return
     */
    private ReceiveAlarmVO buildReceiveAlarmVO(ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto,ReceiveIotAlarmFactorDto configVO, OrderCustomerVO orderCustomerVO, ReceiveIotAlarmVO receiveIotAlarmVO) {
        ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
        receiveAlarmVO.setAlarmSource(WarningSourceEnum.IOT_ALARM.getTypeValue());
        receiveAlarmVO.setAlarmType(configVO.getIotAlarmType());
        receiveAlarmVO.setAlarmTime(receiveIotAlarmVO.getAlarmTime());
        receiveAlarmVO.setIndustryCode(orderCustomerVO.getIndustryCode());
        receiveAlarmVO.setAppVerCode(orderCustomerVO.getAppVerCode());
        receiveAlarmVO.setDeviceCode(receiveIotAlarmVO.getDeviceCode());
        receiveAlarmVO.setChannelCode("");
        receiveAlarmVO.setIsOperation(orderCustomerVO.getIsOperation());
        receiveAlarmVO.setTenantId(orderCustomerVO.getTenantId());
        receiveAlarmVO.setTenantName(orderCustomerVO.getTenantName());
        receiveAlarmVO.setParallelAlarmTypeStr(orderCustomerVO.getParallelAlarmTypeStr());
        receiveAlarmVO.setOperationType(orderCustomerVO.getOperationType());
        receiveAlarmVO.setSerialAlarmTypeStr(orderCustomerVO.getSerialAlarmTypeStr());
        receiveAlarmVO.setImportanceLevel(receiveIotAlarmConfigDto.getAlarmLevel());
        return receiveAlarmVO;
    }


    /**
     * 构建租户下挂靠检测因子信息数据
     * @param configVO
     * @param monitorValue
     * @return
     */
    private WarningSensorMonitorVO buildSensorMonitor(ReceiveIotAlarmFactorDto configVO, String monitorValue) {
        WarningSensorMonitorVO monitorVO = new WarningSensorMonitorVO();
        //前端物联因子展示集合
        List<OneFactoryVO> list = new ArrayList<>();
        //如果因子对应传感器不为空
        if (StringUtils.isNotEmpty(configVO.getSensorCode())){
            list.add(new OneFactoryVO("传感器编码", configVO.getSensorCode()));
            list.add(new OneFactoryVO("传感器名称", configVO.getSensorName()));
        }
        list.add(new OneFactoryVO("监测指标编码", configVO.getFactorCode()));
        if (StringUtils.isNotEmpty(configVO.getMonitorUnits())){
            list.add(new OneFactoryVO("监测指标", configVO.getFactorName()+" ("+configVO.getMonitorUnits()+")"));
        }else {
            list.add(new OneFactoryVO("监测指标", configVO.getFactorName()));
        }
        if (Constants.ONE.equals(configVO.getValueType())){
            list.add(new OneFactoryVO("累计值", configVO.getSumMongoValue()));
        }else if (Constants.ZERO.equals(configVO.getValueType())){
            list.add(new OneFactoryVO("监测值", monitorValue));
        }else if (Constants.TWO.equals(configVO.getValueType())){
            if ("min".equals(configVO.getSumValueUnit())){
                list.add(new OneFactoryVO("分钟均值", monitorValue));
            }else if ("h".equals(configVO.getSumValueUnit())){
                list.add(new OneFactoryVO("小时均值", monitorValue));
            }else if ("day".equals(configVO.getSumValueUnit())){
                list.add(new OneFactoryVO("日均值", monitorValue));
            }
        }
        String minNum = StringUtils.isEmpty(configVO.getMinNum()) ? "" : configVO.getMinNum();
        String maxNum = StringUtils.isEmpty(configVO.getMaxNum()) ? "" : configVO.getMaxNum();
        String minChar = StringUtils.isEmpty(configVO.getMinChar()) || "无".equals(configVO.getMinChar()) ? "" : configVO.getMinChar();
        String maxChar = StringUtils.isEmpty(configVO.getMaxChar()) || "无".equals(configVO.getMaxChar()) ? "" : configVO.getMaxChar();
        //因子大小值都存在 用~
        if (!StringUtils.isEmpty(configVO.getMinNum()) && !StringUtils.isEmpty(configVO.getMaxNum())){
            list.add(new OneFactoryVO("告警阈值", minNum + "~" + maxNum));
            //因子配置的阈值区间
            monitorVO.setThresholdValue(minNum + "~" + maxNum);
        }else {
            list.add(new OneFactoryVO("告警阈值", minNum + minChar + maxChar + maxNum));
            //因子配置的阈值区间
            monitorVO.setThresholdValue(minNum + minChar + maxChar + maxNum);
        }
        //因子编码
        monitorVO.setMonitorCode(configVO.getFactorCode());
        //因子名称
        monitorVO.setMonitorName(configVO.getFactorName());
        //因子检测值
        monitorVO.setMonitorValue(monitorValue);
        //因子对应的告警类型
        monitorVO.setAlarmType(configVO.getIotAlarmType());
        monitorVO.setRemark(String.valueOf(configVO.getGroupId()));
        monitorVO.setNewMonitors(list);
        log.info("生成匹配成功的因子信息 = {}",monitorVO);
        return monitorVO;
    }

    /**
     *
     * 判断上报的检测因子的检测值是否在 物联设备/物联因子 配置的阈值内
     * @param factorSensorInFo
     * @param factorSensorDto                  检测因子编码_检测因子值 map
     * @param orderCustomerVO        租户信息
     * @param receiveIotAlarmConfigDto
     * @param alarmTime
     * @param receiveIotAlarmVO
     * @return  SensorMonitorConfigVO 返回对象不为空则有匹配上的阈值，返回对象为空则没有再配置的阈值内
     */
    private ReceiveIotAlarmFactorDto judgeThresholdValue(List<SensorMonitorRelInfoVo> factorSensorInFo,ReceiveIotAlarmFactorDto factorSensorDto, OrderCustomerVO orderCustomerVO,ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto,ReceiveIotAlarmGroupsDto groupsDto,String alarmTime,ReceiveIotAlarmVO receiveIotAlarmVO) {
        //上报检测因子编码
        String monitorCode = factorSensorDto.getFactorCode();
        //上报检测因子值
        String monitorValue = String.valueOf(factorSensorDto.getFactorValue());
        String deviceCode = orderCustomerVO.getDeviceCode();
        String sensorCode = factorSensorDto.getSensorCode();
        //再判断物联因子方式配置,及物联因子阈值是否满足生成告警事件
        return this.judgeThresholdValue(factorSensorInFo,receiveIotAlarmConfigDto, groupsDto,sensorCode, monitorValue,monitorCode,alarmTime,deviceCode,receiveIotAlarmVO);
    }

    /**
     * 判断告警上报的检测因子的值是否再配置的阈值区间
     * @param receiveIotAlarmConfigDto  物联配置信息
     * @param sensorCode   传感器编码
     * @param monitorValue   上报的因子检测值
     * @param monitorCode
     * @param alarmTime
     * @param deviceCode
     * @param receiveIotAlarmVO
     * @return SensorMonitorConfigVO 返回对象不为空则有匹配上的阈值，返回对象为空则没有再配置的阈值内
     */
    private ReceiveIotAlarmFactorDto judgeThresholdValue(List<SensorMonitorRelInfoVo> factorSensorInFo,ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto, ReceiveIotAlarmGroupsDto groupsDto,String sensorCode, String monitorValue,String monitorCode,String alarmTime,String deviceCode,ReceiveIotAlarmVO receiveIotAlarmVO) {
        ReceiveIotAlarmFactorDto factorDto1 = new ReceiveIotAlarmFactorDto();
        //上报物联因子传感器编码
        String sensorTypeCode = "";
        String sensorName ="";
        SensorMonitorRelInfoVo sensorMonitorRelInfoVo = new SensorMonitorRelInfoVo();
        //通过传感器编码过滤 获取传感器名称
        if (!CollectionUtils.isEmpty(factorSensorInFo)){
            sensorMonitorRelInfoVo = factorSensorInFo.stream().filter(v -> sensorCode.equals(v.getSensorCode())).findFirst().orElse(null);
        }
        //获取传感器名称不为空
        if (ObjectUtil.isNotNull(sensorMonitorRelInfoVo)){
            sensorTypeCode = sensorMonitorRelInfoVo.getSensorTypeCode();
            sensorName = sensorMonitorRelInfoVo.getSensorName();
        }
            for (ReceiveIotAlarmFactorDto factorDto : groupsDto.getFactorList()) {
                factorDto.setIotAlarmType(receiveIotAlarmConfigDto.getIotAlarmType());
                factorDto.setRelation(groupsDto.getRelation());
                factorDto.setTrue(false);
                //匹配因子
                if (factorDto.getFactorCode().equals(monitorCode) && sensorTypeCode.equals(factorDto.getSensorTypeCode())) {
                    factorDto.setSensorCode(sensorCode);
                    if (StringUtils.isNotEmpty(sensorName)){
                        factorDto.setSensorName(sensorName);
                    }else {
                        factorDto.setSensorName(sensorCode);
                    }
                    if (judgeThresholdValue(factorDto, monitorValue,deviceCode,alarmTime,receiveIotAlarmVO)) {
                        factorDto.setTrue(true);
                        return factorDto;
                    }
                    factorDto1 = factorDto;
                }
            }
        return factorDto1;
    }


    /**
     * 判断上报的检测因子的指标是否在物联因子配置的阈值中,预防上报的因子值不为数字导致程序报错,这里做一个兼容处理
     * @param factorDto
     * @param monitorValue
     * @param deviceCode
     * @param alarmTime
     * @param receiveIotAlarmVO
     * @return
     */
    private boolean judgeThresholdValue(ReceiveIotAlarmFactorDto factorDto, String monitorValue,String deviceCode,String alarmTime,ReceiveIotAlarmVO receiveIotAlarmVO) {
        try {
            if (StringUtils.isEmpty(factorDto.getMinNum()) && StringUtils.isEmpty(factorDto.getMaxNum())) {
                return false;
            }
            //时间频次  判断频次和物联配置是否一样
            String timeFrequency = receiveIotAlarmVO.getTimeFrequency();
            //监测均值条件是否满足
            if ((!StringUtils.isEmpty(timeFrequency) && !Constants.TWO.equals(factorDto.getValueType())) || (StringUtils.isEmpty(timeFrequency) && Constants.TWO.equals(factorDto.getValueType()))){
                return false;
            }
            if (!StringUtils.isEmpty(timeFrequency)){
                if (Constants.TWO.equals(factorDto.getValueType())){
                    if ("1".equals(timeFrequency)){
                        timeFrequency = "min";
                    }
                    else if ("2".equals(timeFrequency)){
                        timeFrequency = "h";
                    }
                    else if ("3".equals(timeFrequency)){
                        timeFrequency = "day";
                    }
                    if (!timeFrequency.equals(factorDto.getSumValueUnit())){
                        return false;
                    }
                }else {
                    return false;
                }
            }
            //判断是否累计值  查询MongoDB
            if (Constants.ONE.equals(factorDto.getValueType())){
                String key = factorDto.getGroupId()+"_"+factorDto.getFactorCode()+"_"+factorDto.getSumValue()+"_"+factorDto.getSumValueUnit()+"_"+deviceCode;
                String mongoMonitorValue = cacheService.getIotFactory(factorDto, deviceCode, alarmTime);
                if (!StringUtils.isEmpty(mongoMonitorValue)){
                    log.info("cache by key = {} get value = {}",key,mongoMonitorValue);
                    monitorValue = String.valueOf(new BigDecimal(mongoMonitorValue).add(new BigDecimal(monitorValue)));
                }
                factorDto.setSumMongoValue(monitorValue);
            }
            //mixFlag true 不满足  false满足
            boolean mixFlag = !StringUtils.isEmpty(factorDto.getMinNum()) && !this.judgeMinNum(factorDto, monitorValue);
            //maxFlag true 不满足  false满足
            boolean maxFlag = !StringUtils.isEmpty(factorDto.getMaxNum()) && !this.judgeMaxNum(factorDto, monitorValue);
            return !mixFlag && !maxFlag;
        } catch (Exception e) {
            log.error("判断检测因子值错误", e);
            return false;
        }
    }

    /**
     * 判断最小值
     * @param vo     物联因子配置信息
     * @param monitorValue    因子上报检测值
     * @return boolean true 包含  false 不包含
     */
    private boolean judgeMinNum(ReceiveIotAlarmFactorDto vo, String monitorValue) {
        //最小值比较结果: minCompare > 0 检测值大于最小阈值, minCompare = 0 检测值等于最小阈值, minCompare < 0 检测值小于最小阈值
        int minCompare = new BigDecimal(monitorValue).compareTo(new BigDecimal(vo.getMinNum()));
        //最小值 判断 包含边界值和不包含边界值
        return ((Objects.equals(Constants.OPERATOR_LESS, vo.getMinChar()) && minCompare > 0)
                || (Objects.equals(Constants.OPERATOR_LE, vo.getMinChar()) && minCompare >= 0));
    }

    /**
     * 判断最大值
     * @param vo 物联因子配置信息
     * @param monitorValue  因子上报检测值
     * @return
     */
    private boolean judgeMaxNum(ReceiveIotAlarmFactorDto vo, String monitorValue) {
        //最大值比较结果:maxCompare > 0 检测表大于最大阈值, maxCompare = 0 检测值等于最大阈值, maxCompare < 0 检测值小于最大阈值
        int maxCompare = new BigDecimal(monitorValue).compareTo(new BigDecimal(vo.getMaxNum()));
        return ((Objects.equals(Constants.OPERATOR_LESS, vo.getMaxChar()) && maxCompare < 0)
                || (Objects.equals(Constants.OPERATOR_LE, vo.getMaxChar()) && maxCompare <= 0));
    }

    private boolean judgeAlarmTime(ReceiveIotAlarmConfigDto configVO,ReceiveIotAlarmVO receiveIotAlarmVO){
        String alarmTime = receiveIotAlarmVO.getAlarmTime();
        boolean judgeTime = true;
        //判断是否配置了告警时间,告警时间，时间段是否符合
        if (!StringUtils.isEmpty(configVO.getReceivePeriodTime())){
            String periodTime = configVO.getReceivePeriodTime();//配置时间段
            int alarmHour = Integer.parseInt(alarmTime.substring(Constants.ELEVEN_NUM, Constants.THIRTEEN_NUM));//接收告警时间(H)
            //判断时间段是否符合
            if(Arrays.stream(periodTime.split(Constants.COMMA)).map(Integer::parseInt).collect(Collectors.toList()).contains(alarmHour)){
                if (!StringUtils.isEmpty(configVO.getReceiveStartTime())){
                    Date startTime = DateUtils.stringToDateYMDHMS(configVO.getReceiveStartTime());
                    Date endTime = DateUtils.stringToDateYMDHMS(configVO.getReceiveEndTime());
                    Date receiveTime = DateUtils.stringToDateYMDHMS(alarmTime.substring(Constants.ZERO_NUM, Constants.TEN_NUM));
                    //判断时间范围
                    if (receiveTime.compareTo(startTime) >= 0 && receiveTime.compareTo(endTime) <= 0) {
                        judgeTime = true;
                    }else {judgeTime = false;}
                }else {
                    judgeTime = true;
                }
            }
        }

        return judgeTime;
    }

    private void deleteKey(Map<String, ReceiveAlarmVO> receiveAlarmMap, ReceiveIotAlarmFactorDto configVO, OrderCustomerVO orderCustomerVO, ReceiveIotAlarmVO receiveIotAlarmVO,String uuid) {
        String receiveAlarmKey = orderCustomerVO.getTenantId() + "_" + orderCustomerVO.getIndustryCode() + "_" + orderCustomerVO.getDeviceCode() + "_" + configVO.getIotAlarmType() + "_" + receiveIotAlarmVO.getAlarmTime() + "_" + uuid;
        receiveAlarmMap.remove(receiveAlarmKey);
    }

    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.IOT_ALARM.getAlarmStrategyBean();
    }
}

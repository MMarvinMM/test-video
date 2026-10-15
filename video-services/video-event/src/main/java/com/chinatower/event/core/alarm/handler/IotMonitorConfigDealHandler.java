package com.chinatower.event.core.alarm.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.asyncTask.AsyncCommon;
import com.chinatower.event.common.service.CacheService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.dto.ReceiveIotAlarmBooleanDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmConfigDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmGroupsDto;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mongodb.IotConfigMongoMapper;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.vo.SensorMonitorConfigVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 根据配置的物联因子阈值来筛选需要给哪些租户发送告警信息
 * @Author luo.zhenghong
 * @Date 2022/7/21
 * @Version release_v1.5
 **/
@Component
@Slf4j
@Scope("prototype")
public class IotMonitorConfigDealHandler extends AbstractHandler {


    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IotConfigMongoMapper iotConfigMongoMapper;

    /**
     * 根据查询出来的租户信息、设备上报的因子检测、租户配置的物联因子阈值来生成告警信息
     * 处理步骤：
     *       1.根据租户id集合和物联设备集合查询物联设备配置方式的笛卡尔积
     *       2.根据租户id集合和物联因子集合查询物联因子配置方式的笛卡尔积
     *       3.循环处理上报的告警信息,根据租户_行业版本_因子阈值判断该告警信息是否满足生成告警事件条件
     *       4.满足生成告警信息的因子根据配置的告警类型判断是生成一条告警信息还是需要生成多条告警信息
     * @param receiveAlarmDTO
     * @return
     * @throws Exception
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("根据配置的物联因子阈值来筛选需要给哪些租户发送告警信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            long beginTime = System.currentTimeMillis();
            //物联配置信息
            List<ReceiveIotAlarmConfigDto> iotConfigList = receiveAlarmDTO.getIotConfigList();
            //获取订购物联设备租户信息
            List<OrderCustomerVO> customerVOList = receiveAlarmDTO.getCustomers();
            //接收物联信息
            List<ReceiveIotAlarmVO> receiveIotAlarmVOList = receiveAlarmDTO.getReceiveIotAlarmVOList();
            /*//获取去重后的所有租户id
            List<String> tenantIdList = new ArrayList<>();
            //获取去重后的所有物联设备编码
            List<String> deviceCodeList = new ArrayList<>();
            this.getMonitorCodeList(customerVOList, deviceCodeList, tenantIdList);*/
            //生成告警事件的map
            Map<String, ReceiveAlarmVO> receiveAlarmVOMap = this.matchSensorMonitorConfig(receiveAlarmDTO,iotConfigList, receiveIotAlarmVOList, customerVOList);
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
                List<AntiShakeValueVO> antiShakeSaveRedisList = antiShakeSaveList.stream().filter(v -> "1".equals(v.getValueType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(antiShakeSaveRedisList)){
                    asyncCommon.antiShakeValueRedisHandle(antiShakeSaveRedisList);
                }
                //获取告警防抖次数需要Mongo存储数据
                List<AntiShakeValueVO> antiShakeSaveMongoList = antiShakeSaveList.stream().filter(v -> "2".equals(v.getValueType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(antiShakeSaveMongoList)){
                    asyncCommon.antiShakeValueMongoHandle(antiShakeSaveMongoList);
                }

            }
            //获取设备池数需要redis存储数据
            List<IotDeviceGroupsVO> deviceGroupsSaveList = receiveAlarmDTO.getDeviceGroupsSaveList();
            if (!CollectionUtils.isEmpty(antiShakeSaveList)){
                asyncCommon.deviceGroupsValueRedisHandle(deviceGroupsSaveList);
            }
            List<IotAlarmSuppressVO> alarmSuppressVOList = receiveAlarmDTO.getIotAlarmSuppressVOList();
            //获取配置告警压制时效需要redis存储数据
            if (!CollectionUtils.isEmpty(alarmSuppressVOList)){
                asyncCommon.iotAlarmSuppressRedisHandle(alarmSuppressVOList);
            }
            if (MapUtils.isEmpty(receiveAlarmVOMap)) {
                log.info("没有需要生成告警的物联设备!");
                return false;
            }
            List<ReceiveAlarmVO> valueList = receiveAlarmVOMap.values().stream().collect(Collectors.toList());
            log.info("-----------------IotAlarmConfigDealHandler receiveAlarmList------------:{}", valueList);
            receiveAlarmDTO.setReceiveAlarmVO(null);
            receiveAlarmDTO.setReceiveIotAlarmVOList(null);
            receiveAlarmDTO.setCustomers(null);
            receiveAlarmDTO.setList(valueList);

            long endTime = System.currentTimeMillis();
            log.info("IotMonitorConfigDealHandler excute cost: {}", (endTime - beginTime));
        }catch(Exception e){
            log.error("根据配置的物联因子阈值来筛选需要给哪些租户发送告警信息异常：{}", e);
        }

        //调用下一个执行器
        return this.excuteNextHandler(receiveAlarmDTO);
    }

    /**
     * 获取所有去重后的租户id和物联设备编码
     * @param customerVOList
     * @param deviceCodeList
     * @param tenantIdList
     * @return
     */
    private void getMonitorCodeList(List<OrderCustomerVO> customerVOList, List<String> deviceCodeList, List<String> tenantIdList) {
        Set<String> tenantIdSet = new HashSet<>();
        Set<String> deviceCodeSet = new HashSet<>();
        for (OrderCustomerVO orderCustomerVO :customerVOList) {
            tenantIdSet.add(orderCustomerVO.getTenantId());
            deviceCodeSet.add(orderCustomerVO.getDeviceCode());
        }
        tenantIdList.addAll(Arrays.asList(tenantIdSet.toArray(new String[0])));
        deviceCodeList.addAll(Arrays.asList(deviceCodeSet.toArray(new String[0])));
    }

    /**
     * 根据 物联设备/物联因子 配置阈值生成告警数量
     * @param receiveIotAlarmVOList
     * @param customerVOList
     * @return
     */
    private Map<String, ReceiveAlarmVO> matchSensorMonitorConfig(ReceiveAlarmDTO receiveAlarmDTO,List<ReceiveIotAlarmConfigDto> iotConfigList, List<ReceiveIotAlarmVO> receiveIotAlarmVOList, List<OrderCustomerVO> customerVOList) {
        //处理物联设备上报的告警信息，根据配置的阈值规则和告警类型判断是否需要生成告警以及生成一条告警还是生成多条告警
        return this.dealAlarmInfo(receiveAlarmDTO,iotConfigList,receiveIotAlarmVOList, customerVOList);

    }


    /**
     * 判断告警上报的检测因子的值是否再配置的阈值区间
     * @param receiveIotAlarmConfigDto  物联配置信息
     * @param monitorValue   上报的因子检测值
     * @return SensorMonitorConfigVO 返回对象不为空则有匹配上的阈值，返回对象为空则没有再配置的阈值内
     */
    private ReceiveIotAlarmFactorDto judgeThresholdValue(ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto ,ReceiveIotAlarmGroupsDto groupsDto, String monitorValue,String monitorCode,String alarmTime,String deviceCode,ReceiveIotAlarmVO receiveIotAlarmVO) {
        ReceiveIotAlarmFactorDto factorDto1 = new ReceiveIotAlarmFactorDto();
            factorDto1.setIotAlarmType(receiveIotAlarmConfigDto.getIotAlarmType());
            factorDto1.setRelation(groupsDto.getRelation());
            boolean judgeTime = true;
            //判断是否配置了告警时间,告警时间，时间段是否符合
            if (!StringUtils.isEmpty(receiveIotAlarmConfigDto.getReceivePeriodTime())){
                judgeTime=this.judgeAlarmTime(receiveIotAlarmConfigDto,alarmTime);
            }

            if (judgeTime){
                for (ReceiveIotAlarmFactorDto factorDto : groupsDto.getFactorList()) {
                    //匹配因子
                    if (factorDto.getFactorCode().equals(monitorCode)) {
                        if (judgeThresholdValue(factorDto, monitorValue,deviceCode,alarmTime,receiveIotAlarmVO)) {
                            factorDto.setIotAlarmType(receiveIotAlarmConfigDto.getIotAlarmType());
                            factorDto.setRelation(groupsDto.getRelation());
                            factorDto.setTrue(true);
                            return factorDto;
                        }
                    }
                }
            }
        return factorDto1;
    }

    private boolean judgeAlarmTime(ReceiveIotAlarmConfigDto configVO,String alarmTime){
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
                    return true;
                }else {return false;}
            }else {
                return true;
            }
        }
        return false;
    };

    /**
     * 循环处理物联设备上报的告警信息，根据配置的阈值规则和告警类型判断是否需要生成告警以及生成一条告警还是生成多条告警
     * @param iotConfigList 物联配置信息
     * @param receiveIotAlarmVOList 物联设备批量上报的节点信息
     * @param customerVOList 物联设备与租户信息
     * @return Map<String, ReceiveAlarmVO> 所有租户应生成的告警事件
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
             //租户下此设备下的物联配置 ， 物联规则
             for (ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto : iotFilterConfigList) {
                 //物联告警判断值
                 ReceiveIotAlarmBooleanDto iotAlarmBooleanDto = new ReceiveIotAlarmBooleanDto();
                 //查询存储告警生效物联设备池
                 IotDeviceGroupsVO iotDeviceGroupsVO = new IotDeviceGroupsVO();
                 this.getIotDeviceGroup(iotDeviceGroupsVO,receiveIotAlarmConfigDto);
                 //是否符合告警生效设备组合
                 iotAlarmBooleanDto.setIotDeviceGroup(false);
                 for (ReceiveIotAlarmGroupsDto groupsDto : receiveIotAlarmConfigDto.getIotAlarmConfigGroupsList()) {
                     AntiShakeValueVO antiShakeValue = new AntiShakeValueVO();
                     //查询存储告警防抖次数
                     this.getAntiShake(antiShakeValue,groupsDto);
                     //租户订购设备下上报的物联设备信息
                 here:
                 for (ReceiveIotAlarmVO receiveIotAlarmVO : receiveIotAlarmVOMap.get(orderCustomerVO.getDeviceCode())) {
                     //存放透传字段物联设备信息 默认当前物联设备
                     String fitIotDeviceGroup = orderCustomerVO.getDeviceCode();
                     //判断是否符合告警防抖规则
                     iotAlarmBooleanDto.setAntiShake(this.judgeAntiShake(antiShakeValue, groupsDto));
                     //如果告警防抖规则符合则判断是否符合告警生效设备组合,防抖和设备组合都满组才可生成告警
                     if (iotAlarmBooleanDto.isAntiShake()){
                          //如果满足告警生效物联设备组合 返回逗号分割多个物联设备
                          fitIotDeviceGroup = this.judgeIotDeviceGroups(iotAlarmBooleanDto, iotDeviceGroupsVO, receiveIotAlarmConfigDto, orderCustomerVO.getDeviceCode());
                     }
                     String alarmTime = receiveIotAlarmVO.getAlarmTime();
                     String uuid = UUID.randomUUID().toString();
                     //是否符合物联规则下的阈值范围  关系为且则有一条因子阈值范围不符合即为false，为或有一条因子阈值范围符合即为true
                     iotAlarmBooleanDto.setTrueGroup(false);
                     //上报的因子信息
                     for (Map.Entry<String, Object> entry : receiveIotAlarmVO.getMonitor().entrySet()) {
                         ReceiveIotAlarmFactorDto configVO = this.judgeThresholdValue(entry, orderCustomerVO, receiveIotAlarmConfigDto,groupsDto, alarmTime, receiveIotAlarmVO);
                         //因子关系为且 删除这一条对应租户数据
                         if ("1".equals(configVO.getRelation()) && !configVO.isTrue()) {
                             iotAlarmBooleanDto.setTrueGroup(false);
                             this.deleteKey(receiveAlarmMap, configVO, orderCustomerVO, receiveIotAlarmVO, String.valueOf(entry.getValue()), uuid);
                             //防抖次数计算，用于后续存储防抖次数累计数据
                             this.insertAntiShake(antiShakeValue,groupsDto,iotAlarmBooleanDto,antiShakeSaveList);
                             break here;
                         }
                         //组装生成的告警事件信息,注意一个告警信息如果上报了多个检测因子,需要判断这些检测因子关联的告警类型是否相同,如果相同则生成
                         this.buildAlarmInfo(receiveIotAlarmConfigDto,iotAlarmBooleanDto,receiveAlarmMap, configVO, orderCustomerVO, receiveIotAlarmVO, String.valueOf(entry.getValue()), uuid);
                     }
                     //防抖次数计算，用于后续存储防抖次数累计数据
                     this.insertAntiShake(antiShakeValue,groupsDto,iotAlarmBooleanDto,antiShakeSaveList);
                     //告警生效设备，用于后续存储设备池
                     this.insertDeviceGroups(receiveIotAlarmConfigDto,iotDeviceGroupsVO,iotAlarmBooleanDto,iotDeviceGroupsVOList, orderCustomerVO.getDeviceCode());
                     //告警压制，用于后续存储告警压制时效
                     this.insertIotAlarmSuppress(receiveIotAlarmConfigDto,iotAlarmBooleanDto,iotAlarmSuppressVOList);
                     //构建透传字段里的 物联设备信息
                     this.buildIotDeviceExtraField(receiveAlarmMap,orderCustomerVO,receiveIotAlarmConfigDto, receiveIotAlarmVO, uuid,fitIotDeviceGroup);
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
    //查询存储告警防抖次数
    private void getAntiShake (AntiShakeValueVO antiShakeValue,ReceiveIotAlarmGroupsDto groupsDto){
        //查询存储告警防抖次数
        if ("1".equals(groupsDto.getAntiShakeType())){
            //key 配置id+规则id
            String key = Constants.ANTI_SHAKE_REDIS_KEY+groupsDto.getIotConfigId()+groupsDto.getId();
            //取出防抖时间内触发次数 再加一放入缓存中
            BigDecimal num = RedisUtil.get(key, BigDecimal.class, RedisDBType.PRIMARY);
            //如果历史存储过该规则防抖数值，则用历史存储中的值，否则从0开始
            antiShakeValue.setTriggerValue(num!=null?num:new BigDecimal(0));
        }else if ("2".equals(groupsDto.getAntiShakeType()) || "3".equals(groupsDto.getAntiShakeType())){
            AntiShakeValueVO shakeValue = iotConfigMongoMapper.getMongoAntiShakeValue(groupsDto);
            //如果历史存储过该规则防抖数值，则用历史存储中的值，否则从0开始
            antiShakeValue.setContinueValue(shakeValue.getContinueValue()!=null?shakeValue.getContinueValue():new BigDecimal(0));
            antiShakeValue.setSuddenValue(shakeValue.getSuddenValue()!=null?shakeValue.getSuddenValue():new BigDecimal(0));
        }
    }

    //查询设备池
    private void getIotDeviceGroup (IotDeviceGroupsVO iotDeviceGroupsVO,ReceiveIotAlarmConfigDto configDto){
        //查询存储设备池
        if (StringUtils.isNotEmpty(configDto.getDeviceGroupsJson()) && !Constants.EMPTY_STRING.equals(configDto.getDeviceGroupsJson())){
        String key = Constants.IOT_DEVICE_GROUPS_REDIS_KEY+configDto.getConfigId();
        List<IotDeviceGroupsVO.DeviceGroup> list = RedisUtil.get(key, List.class, RedisDBType.PRIMARY);
        iotDeviceGroupsVO.setDeviceGroupList(list);
        iotDeviceGroupsVO.setConfigId(configDto.getConfigId());
        }
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
           if (compare > 0){
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
            if (ObjectUtil.isNotNull(iotDeviceGroupsVO)){
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
                }
                antiShakeValue.setTriggerValue(numValue);
                antiShakeValue.setValueType("1");
                //通过告警防抖时间 和 时间单位 转换为秒作为redis过期时间
                long seconds = this.convertToSeconds(groupsDto.getAntiShakeTime(), groupsDto.getAntiShakeTimeUnit());
                antiShakeValue.setTimeoutValue(seconds);
            }else if ("2".equals(groupsDto.getAntiShakeType())){
                numValue = antiShakeValue.getContinueValue();
                //比较实际防抖次数 和 规则中的防抖次数比较  大于则符合否则不符合告警防抖
                compare = numValue.compareTo(new BigDecimal(groupsDto.getAntiShakeNum()));
                //isTrueGroup为false 说明不符合阈值，将不再连续符合阈值
                //或者 连续次数等于规则中的次数(如果符合阈值将大于规则中次数产生告警) 此时连续次数将置为0
                //否则 连续次数＋1
                if (!iotAlarmBooleanDto.isTrueGroup() || compare==0){
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
            //过滤该规则Id旧数据， 放入该规则Id新的数据
            antiShakeSaveList = antiShakeSaveList.stream().filter(v -> !v.getGroupId().equals(groupsDto.getId())).collect(Collectors.toList());
            antiShakeSaveList.add(antiShakeValue);
        }
    }


    //告警生效设备池，用于后续存储告警生效设备
    private void insertDeviceGroups (ReceiveIotAlarmConfigDto configDto,IotDeviceGroupsVO iotDeviceGroupsVO,ReceiveIotAlarmBooleanDto iotAlarmBooleanDto,List<IotDeviceGroupsVO> iotDeviceGroupsVOList,String deviceCode){
        //如果符合规则 更新设备池
        if (StringUtils.isNotEmpty(configDto.getDeviceGroupsJson()) && !Constants.EMPTY_STRING.equals(configDto.getDeviceGroupsJson()) && iotAlarmBooleanDto.isAntiShake() && iotAlarmBooleanDto.isTrueGroup()){
            //获取当前时间
            LocalDateTime nowDate = LocalDateTime.now();
            String insertTime = nowDate.format(DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS));
            //判断设备池中是否存在上报设备，有则更新，无则新增
            boolean isHave = iotDeviceGroupsVO.getDeviceGroupList().stream().anyMatch(v -> deviceCode.equals(v.getDeviceCode()));
            if (isHave){
                iotDeviceGroupsVO.getDeviceGroupList().forEach(v->{
                    if (deviceCode.equals(v.getDeviceCode())){
                        v.setInsertTime(insertTime);
                    }
                });
            }else {
                IotDeviceGroupsVO.DeviceGroup deviceGroup = new IotDeviceGroupsVO.DeviceGroup();
                deviceGroup.setDeviceCode(deviceCode);
                deviceGroup.setInsertTime(insertTime);
                iotDeviceGroupsVO.getDeviceGroupList().add(deviceGroup);
            }
            iotDeviceGroupsVOList.add(iotDeviceGroupsVO);
        }
    }


    //告警生效设备池，用于后续存储告警生效设备
    private void insertIotAlarmSuppress (ReceiveIotAlarmConfigDto configDto ,ReceiveIotAlarmBooleanDto iotAlarmBooleanDto,List<IotAlarmSuppressVO> iotAlarmSuppressVOList) {
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
    private void buildIotDeviceExtraField (Map<String, ReceiveAlarmVO> receiveAlarmMap,OrderCustomerVO orderCustomerVO,ReceiveIotAlarmConfigDto configVO, ReceiveIotAlarmVO receiveIotAlarmVO,String uuid,String fitIotDeviceGroup) {
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

    private void deleteKey(Map<String, ReceiveAlarmVO> receiveAlarmMap, ReceiveIotAlarmFactorDto configVO, OrderCustomerVO orderCustomerVO, ReceiveIotAlarmVO receiveIotAlarmVO, String monitorValue, String uuid) {
        String receiveAlarmKey = orderCustomerVO.getTenantId() + "_" + orderCustomerVO.getIndustryCode() + "_" + orderCustomerVO.getDeviceCode() + "_" + configVO.getIotAlarmType() + "_" + receiveIotAlarmVO.getAlarmTime() + "_" + uuid;
        receiveAlarmMap.remove(receiveAlarmKey);
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
        if (!iotAlarmBooleanDto.isIotDeviceGroup() && !iotAlarmBooleanDto.isAntiShake()){
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
     * @param orderCustomerVO
     * @param receiveIotAlarmVO
     * @param configVO
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
     * 判断上报的检测因子的检测值是否在 物联设备/物联因子 配置的阈值内
     * @param entry                  检测因子编码_检测因子值 map
     * @param orderCustomerVO        租户信息
     * @return  SensorMonitorConfigVO 返回对象不为空则有匹配上的阈值，返回对象为空则没有再配置的阈值内
     */
    private ReceiveIotAlarmFactorDto judgeThresholdValue(Map.Entry<String, Object> entry, OrderCustomerVO orderCustomerVO,ReceiveIotAlarmConfigDto receiveIotAlarmConfigDto,ReceiveIotAlarmGroupsDto groupsDto,String alarmTime,ReceiveIotAlarmVO receiveIotAlarmVO) {
        //上报检测因子编码
        String monitorCode = entry.getKey();
        //上报检测因子值
        String monitorValue = String.valueOf(entry.getValue());
        String deviceCode = orderCustomerVO.getDeviceCode();
        //再判断物联因子方式配置的告警时间,及物联因子阈值是否满足生成告警事件
        return this.judgeThresholdValue(receiveIotAlarmConfigDto,groupsDto, monitorValue,monitorCode,alarmTime,deviceCode,receiveIotAlarmVO);
    }

    /**
     * 构建租户下挂靠检测因子信息数据
     * @param configVO 配置的因子阈值
     * @param monitorValue 检测值
     * @return
     */
    private WarningSensorMonitorVO buildSensorMonitor(ReceiveIotAlarmFactorDto configVO, String monitorValue) {
        WarningSensorMonitorVO monitorVO = new WarningSensorMonitorVO();
        //前端物联因子展示集合
        List<OneFactoryVO> list = new ArrayList<>();
        list.add(new OneFactoryVO("监测指标编码", configVO.getFactorCode()));
        if (StringUtils.isNotEmpty(configVO.getMonitorUnits())){
            list.add(new OneFactoryVO("监测指标", configVO.getFactorName()+" ("+configVO.getMonitorUnits()+")"));
        }else {
            list.add(new OneFactoryVO("监测指标", configVO.getFactorName()));
        }
        if ("1".equals(configVO.getValueType())){
            list.add(new OneFactoryVO("累计值", configVO.getSumMongoValue()));
        }else if ("0".equals(configVO.getValueType())){
            list.add(new OneFactoryVO("监测值", monitorValue));
        }else if ("2".equals(configVO.getValueType())){
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
     * 判断上报的检测因子的指标是否在物联因子配置的阈值中,预防上报的因子值不为数字导致程序报错,这里做一个兼容处理
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
            if ((!StringUtils.isEmpty(timeFrequency) && !"2".equals(factorDto.getValueType())) || (StringUtils.isEmpty(timeFrequency) && "2".equals(factorDto.getValueType()))){
                return false;
            }
            if (!StringUtils.isEmpty(timeFrequency)){
                if ("2".equals(factorDto.getValueType())){
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
            if ("1".equals(factorDto.getValueType())){
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
        }
        catch (Exception e) {
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


    /**
     * 构建查询物联因子请求参数
     * @param monitorCodeList  物联因子编码
     * @param tenantIdList 租户id
     * @param deviceCodeList 设备编码
     * @param configMethod  配置方式 0 物联因子  1 物联设备
     * @return
     */
    private SensorMonitorConfigVO buildParam(List<String> monitorCodeList, List<String> tenantIdList, List<String> deviceCodeList, String configMethod) {
        SensorMonitorConfigVO param = new SensorMonitorConfigVO();
        param.setConfigMethod(configMethod);
        param.setDeviceCodeList(deviceCodeList);
        param.setMonitorCodeList(monitorCodeList);
        param.setTenantIdList(tenantIdList);
        return param;
    }
}

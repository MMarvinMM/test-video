package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.utils.StringUtils;

import com.chinatower.event.common.service.CacheService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.event.mapper.IotAlarmConfigMapper;
import com.chinatower.event.mongodb.IotConfigMongoMapper;
import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 物联设备批量处理接收告警的租户信息
 * @Author luo.zhenghong
 * @Date 2022/8/4
 * @Version release_v1.5
 **/
@Component
@Slf4j
@Scope("prototype")
public class IotAlarmConfigHandler extends AbstractHandler{

    @Autowired
    private IotAlarmConfigMapper iotAlarmConfigMapper;

    @Autowired
    private CacheService cacheService;

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
                    //cacheService.deleteCache();
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

    /**
     * 处理告警配置内存信息，MongoDB因子累计值
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备批量处理物联告警配置内存及MongoDB因子累计值信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            long beginTime = System.currentTimeMillis();
            //获取物联上报信息
            List<ReceiveIotAlarmVO> iotAlarmVOList = receiveAlarmDTO.getReceiveIotAlarmVOList();
            //获取物联上报设备编码集合
            List<String> deviceCodeList = iotAlarmVOList.stream().map(ReceiveIotAlarmVO::getDeviceCode).distinct().collect(Collectors.toList());
            //获取客户信息
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();
            //内存取全量物联配置信息为null 则自动加载内存 DB更新信息
            String cacheKey = Constants.IOT_ALARM_CONFIG_CACHE;
            String iotConfigCache = CACHE.get(cacheKey);
            log.info("物理配置内存信息：{}", JSON.toJSONString(iotConfigCache));
            List<ReceiveIotAlarmConfigDto> iotConfigList = JSONArray.parseArray(iotConfigCache, ReceiveIotAlarmConfigDto.class);
            //用接收设备编码过滤后配置
            List<ReceiveIotAlarmConfigDto> iotFilterConfigList = new ArrayList<>();
            //用接收设备编码过滤后配置下所有规则总合
            List<ReceiveIotAlarmGroupsDto> iotFilterAllGroupList = new ArrayList<>();
            //去除物联规则中不包含这个物联设备的
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
            if (iotConfigList.isEmpty()) {
                log.info("没有需要生成告警的物联设备!");
                return false;
            }
            //后续判断配置使用
            receiveAlarmDTO.setIotConfigList(iotFilterConfigList);
            //MongoDB累计值 数据处理
            this.mongoDataHandle(receiveAlarmDTO,iotFilterAllGroupList,iotAlarmVOList);
            long endTime = System.currentTimeMillis();
            log.info("IotAlarmConfigHandler excute cost: {}", (endTime - beginTime));
        }catch(Exception e){
            log.error("物联设备批量处理物联告警配置内存及MongoDB因子累计值信息异常：{}", e);
        }
        return this.excuteNextHandler(receiveAlarmDTO);
    }

    /**
     * MongoDB累计值 数据处理
     *
     * @param
     * @return
     */
    public void mongoDataHandle(ReceiveAlarmDTO receiveAlarmDTO,List<ReceiveIotAlarmGroupsDto> iotFilterAllGroupList,List<ReceiveIotAlarmVO> iotAlarmVOList) {
        //取出物联配置中需要累计的因子编码并去重
        List<String> factoryCode = iotFilterAllGroupList.stream()
                .flatMap(group -> group.getFactorList().stream())
                .filter(factor -> "1".equals(factor.getValueType()))
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
                    map.put("mergeTime",StringUtils.substring(receiveIotAlarmVO.getAlarmTime(), 0,16));
                    map.put(key,String.valueOf(receiveIotAlarmVO.getMonitor().get(key)));
                }
            }
            if (!CollectionUtils.isEmpty(map)){
                mongoFactoryList.add(map);
            }
        }
        receiveAlarmDTO.setMongoFactoryList(mongoFactoryList);
    }

}

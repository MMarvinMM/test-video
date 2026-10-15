package com.chinatower.receive.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.order.api.IWeatherApiService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.order.QueryWeatherDTO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.core.intercept.BaseAlarmSuppressStategy;
import com.chinatower.receive.core.intercept.TimePeriodSuppressionStrategy;
import com.chinatower.receive.core.util.ProxyClassUtils;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.mapper.VmAlarmAreaSuppressMapper;
import com.chinatower.receive.mapper.VmAlarmSuppressStrategyMapper;
import com.chinatower.receive.mapper.VmAlarmTypeSuppressMapper;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.ISuppressStrategyService;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.entity.*;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警压制实现类
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:49
 * @version release_v1.6.90
 */

@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class SuppressStrategyServiceImpl implements ISuppressStrategyService {

    @Autowired
    private VmAlarmSuppressStrategyMapper vmAlarmSuppressStrategyMapper;

    @Autowired
    private VmAlarmAreaSuppressMapper vmAlarmAreaSuppressMapper;

    @Autowired
    private VmAlarmTypeSuppressMapper vmAlarmTypeSuppressMapper;

    @Autowired
    private IWeatherApiService weatherApiService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private List<BaseAlarmSuppressStategy> suppressionStrategyList;


    private static final List<String> SUPPRESS_NAMES = Arrays.asList(Constants.TIME_PERIOD_SUPPRESSION_STRATEGY,"ConfidenceStrategyBySatellite","RepeatedAlarmStrategyBySatellite");
    private static final List<String> UAV_SUPPRESS_NAMES = Arrays.asList("AlgorithmThresholdSuppressionStrategy","UavAreaShieldingSuppressionStrategy","ConfidenceSuppressionStrategy","RepeatedAlarmSuppressionStrategy",Constants.TIME_PERIOD_SUPPRESSION_STRATEGY,"UavFlyTimeSuppressionStrategy");
    private static final List<String> Device_SUPPRESS_NAMES = Arrays.asList("AlgorithmThresholdSuppressionStrategy","AreaShieldingSuppressionStrategy","ConfidenceSuppressionStrategy","RepeatedAlarmSuppressionStrategy",Constants.TIME_PERIOD_SUPPRESSION_STRATEGY);
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public List<VmAlarmSuppressStrategyVO> queryStrategyList(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customers) {
        try{
            //告警压制基础策略信息查询
            List<Integer> strategyIdList = vmAlarmSuppressStrategyMapper.selectSuppressStrategy(receiveAlarmVO, customers);
            if(strategyIdList != null && !strategyIdList.isEmpty()) {
                VmAlarmSuppressStrategyVO strategyVo =  new VmAlarmSuppressStrategyVO();
                strategyVo.setStrategyIdList(strategyIdList);
                List<VmAlarmSuppressStrategyVO> strategyVoList = vmAlarmSuppressStrategyMapper.selectList(strategyVo);
                //查询告警压制区域信息
                VmAlarmAreaSuppressVO areaVo =  new VmAlarmAreaSuppressVO();
                areaVo.setStrategyIdList(strategyIdList);
                areaVo.setDeviceCode(receiveAlarmVO.getDeviceCode());
                List<VmAlarmAreaSuppressVO> areaList = vmAlarmAreaSuppressMapper.selectAll(areaVo);
                List<VmAlarmUavAreaSuppressVO> uavAreaList = new ArrayList<>();
                if(!StringUtils.isEmpty(receiveAlarmVO.getFlightRouteId())){
                    areaVo.setFlightRouteId(receiveAlarmVO.getFlightRouteId());
                    uavAreaList = vmAlarmAreaSuppressMapper.selectUavAll(areaVo);
                }
                //查询告警压制区域信息
                VmAlarmTypeSuppressVO alarmTypeVo =  new VmAlarmTypeSuppressVO();
                alarmTypeVo.setStrategyIdList(strategyIdList);
                List<VmAlarmTypeSuppressVO> vmAlarmTypeSuppressVOS = vmAlarmTypeSuppressMapper.selectAll(alarmTypeVo);

                Map<Integer,List<VmAlarmAreaSuppressVO>> areaMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(areaList)) {
                    areaMap = areaList.stream().collect(Collectors.groupingBy(VmAlarmAreaSuppressVO::getAlarmSuppressId));
                    for(VmAlarmSuppressStrategyVO item:strategyVoList){
                        if(areaMap!=null && !areaMap.isEmpty()){
                            item.setAreaSuppressVoList(areaMap.get(item.getId()));
                        }
                    }
                }
                Map<Integer,List<VmAlarmUavAreaSuppressVO>> uavAreaMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(uavAreaList)) {
                    uavAreaMap = uavAreaList.stream().collect(Collectors.groupingBy(VmAlarmUavAreaSuppressVO::getAlarmSuppressId));
                    for(VmAlarmSuppressStrategyVO item:strategyVoList){
                        if(uavAreaMap!=null && !uavAreaMap.isEmpty()){
                            item.setUavAreaSuppressVoList(uavAreaMap.get(item.getId()));
                        }
                    }
                }
                Map<Integer,List<VmAlarmTypeSuppressVO>> typeMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(vmAlarmTypeSuppressVOS)) {
                    typeMap = vmAlarmTypeSuppressVOS.stream().collect(Collectors.groupingBy(VmAlarmTypeSuppressVO::getAlarmSuppressId));
                    for(VmAlarmSuppressStrategyVO item:strategyVoList){
                        if(typeMap!=null && !typeMap.isEmpty()){
                            item.setTypeSuppressVoList(typeMap.get(item.getId()));
                        }
                    }
                }

                return strategyVoList;
            }
        }catch(Exception e){
            log.error("告警压制策略查询异常：{}",e);
        }
        return null;
    }

    @Override
    public boolean doStrategyIntercept(ReceiveAlarmVO receiveAlarmVO, List<VmAlarmSuppressStrategyVO> strategyVoList) {
        try {
            //检查告警压制策略符合的数据
            List<VmAlarmSuppressStrategyVO> validStrategyList = this.checkValidStrategy(receiveAlarmVO,strategyVoList);
            if(!validStrategyList.isEmpty()){
                log.info("告警编码:{},匹配到的策略:{}",receiveAlarmVO.getAlarmCode(),validStrategyList);
                // 获取策略字典映射
                Map<String, String> dictMap = getDictMap(systemFeignClient.dictDataType("alarm_suppress_method_sequence_" + receiveAlarmVO.getTenantId()+receiveAlarmVO.getIndustryCode()));
                // 加载并排序策略
                List<BaseAlarmSuppressStategy> strategies = loadStrategiesFromDictMap(dictMap, receiveAlarmVO.getAlarmSource());
                log.info("排序后的压制策略:{}", JSON.toJSONString(strategies));
                // 应用策略
                for (BaseAlarmSuppressStategy strategy : strategies) {
                    if (strategy == null) {
                        log.error("策略实例为 null！");
                        continue;
                    }
                    for (VmAlarmSuppressStrategyVO item : validStrategyList) {
                        try {
                            boolean result = strategy.apply(receiveAlarmVO, item);
                            log.info("策略 {} 应用结果: {}", ProxyClassUtils.getOriginalSimpleName(strategy), result);
                            if (result) {
                                log.info("告警编码:{}, 使用策略:{}", receiveAlarmVO.getAlarmCode(), ProxyClassUtils.getOriginalSimpleName(strategy));
                                return true;
                            }
                        } catch (Exception e) {
                            log.error("应用策略 {} 时发生异常：", ProxyClassUtils.getOriginalSimpleName(strategy), e);
                        }
                    }
                }
            }else{
                return false;
            }
        }catch (Exception e){
            log.info("处理告警压制数据异常：" + e);
        }
        return false;
    }

    /**
     * 根据配置对压制策略进行排序处理
     * @param dictMap 字典配置中配置的策略顺序
     * @param alarmSource 告警来源
     * @return
     */
    private List<BaseAlarmSuppressStategy> loadStrategiesFromDictMap(Map<String, String> dictMap, String alarmSource) {
        log.info("开始加载策略，字典中配置的策略：{}，告警来源: {}", JSON.toJSONString(dictMap), alarmSource);

        // 根据告警来源区分字典配置内容
        if (Constants.NINE.equals(alarmSource)) {
            dictMap = dictMap.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(Constants.SATELLITE))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else if(Constants.WARNING_SOURCE_12.equals(alarmSource)){
            dictMap =null;
        } else {
            dictMap = dictMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().contains(Constants.SATELLITE))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        // 如果 dictMap 为空，则返回所有策略的默认顺序
        if (dictMap == null || dictMap.isEmpty()) {
            List<BaseAlarmSuppressStategy> defaultStrategies = getDefaultStrategies(alarmSource);
            log.info("未指定策略执行顺序，返回所有策略的默认顺序：{}", defaultStrategies.stream()
                    .map(strategy -> ProxyClassUtils.getOriginalSimpleName(strategy))
                    .collect(Collectors.joining(", ")));
            return defaultStrategies;
        }

        Map<String, String> convertedMap = new HashMap<>();
        // 创建策略实例及其顺序映射
        List<BaseAlarmSuppressStategy> sortedStrategies = new ArrayList<>();
        // 遍历 dictMap 来决定需要加载哪些策略
        for (Map.Entry<String, String> entry : dictMap.entrySet()) {
            String strategyTypeDisplayName = entry.getKey(); // 策略类型
            //int order = Integer.parseInt(entry.getValue()); // 策略顺序

            StrategyTypeEnum strategyType = StrategyTypeEnum.fromDisplayName(strategyTypeDisplayName);
            if(strategyType == null){
                log.info("策略类型不合法: {}", strategyTypeDisplayName);
                continue;
            }
            // 类名与中文名称的映射
            convertedMap.put(strategyType.getInstanceName(), strategyTypeDisplayName);

            // 遍历所有策略实例，找到匹配的策略
            for (BaseAlarmSuppressStategy strategy : suppressionStrategyList) {
                if (strategy.getStrategyBeanName(strategyTypeDisplayName)) {
                    boolean isSatelliteSource = Constants.NINE.equals(alarmSource);
                    boolean isSuppressStrategy = SUPPRESS_NAMES.contains(ProxyClassUtils.getOriginalSimpleName(strategy));

                    // 兼容卫星告警的逻辑
                    if ((isSatelliteSource && isSuppressStrategy && strategyTypeDisplayName.contains(Constants.SATELLITE)) || (!isSatelliteSource && (!isSuppressStrategy || !strategyTypeDisplayName.contains(Constants.SATELLITE)))) {
                        sortedStrategies.add(strategy);
                    }
                    break; // 找到匹配的策略后跳出循环
                }
            }
        }

        // 策略实例按配置的顺序进行排序
        Map<String, String> finalDictMap = dictMap;
        sortedStrategies.sort(Comparator.comparingInt(strategy -> {
            String strategyBeanName = ProxyClassUtils.getOriginalSimpleName(strategy); // 获取压制策略类名
            String strategyTypeDisplayName = convertedMap != null ? convertedMap.get(strategyBeanName) : null;
            // 处理找不到中文名称的情况
            if (strategyTypeDisplayName == null) {
                return Integer.MAX_VALUE; // 默认顺序，放在最后
            }
            return Integer.parseInt(finalDictMap.get(strategyTypeDisplayName)); // 获取策略顺序
        }));

        log.info("加载排序后的策略: {}", sortedStrategies.stream()
                .map(BaseAlarmSuppressStategy::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", ")));

        return sortedStrategies;
    }

    /**
     * 获取默认的策略列表
     * @param alarmSource 告警来源
     * @return
     */
    private List<BaseAlarmSuppressStategy> getDefaultStrategies(String alarmSource) {
        List<BaseAlarmSuppressStategy> filteredStrategies;
        if (Constants.NINE.equals(alarmSource)) {
            filteredStrategies = suppressionStrategyList.stream()
                    .filter(strategy -> SUPPRESS_NAMES.contains(ProxyClassUtils.getOriginalSimpleName(strategy)))
                    .collect(Collectors.toList());
        } else if(Constants.WARNING_SOURCE_12.equals(alarmSource)){
            filteredStrategies = suppressionStrategyList.stream()
                    .filter(strategy -> UAV_SUPPRESS_NAMES.contains(ProxyClassUtils.getOriginalSimpleName(strategy)))
                    .collect(Collectors.toList());
        } else {
            filteredStrategies = suppressionStrategyList.stream()
                    .filter(strategy -> Device_SUPPRESS_NAMES.contains(ProxyClassUtils.getOriginalSimpleName(strategy)))
                    .collect(Collectors.toList());
        }
        // 根据 getOrder 方法排序
        return filteredStrategies.stream()
                .sorted(Comparator.comparingInt(BaseAlarmSuppressStategy::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 获取字典map
     * @param dictDataType
     * @return
     */
    private Map<String, String> getDictMap(Result<List<SysDictDataResponse>> dictDataType) {
        return dictDataType.getData().stream().collect(Collectors.toMap(SysDictDataResponse::getDictLabel, SysDictDataResponse::getDictValue));
    }

    @DS(EbikeDataBaseConstant.BASE)
    @Override
    public ReceiveAlarmVO dealPtzByIconArea(ReceiveAlarmVO vo) {
        ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
        try {
            // 查询摄像机的PTZ以及最小焦距，传感器高度，传感器宽度等信息   用以计算水平视场角和垂直视场角
            SysLntlagConfigVO sysLntlagConfigVO = deviceRepository.getLntlagConfig(vo.getDeviceCode());
            if (Objects.isNull(sysLntlagConfigVO) || Objects.isNull(sysLntlagConfigVO.getF0()) || Objects.isNull(sysLntlagConfigVO.getC()) || Objects.isNull(sysLntlagConfigVO.getD())) {
                log.info("设备厂商对应参数信息不全,无法获取最小焦距、摄像机图像高度和宽度，无法进行PTZ处理");
                return receiveAlarmVO;
            }
            log.info("查询摄像机的PTZ以及最小焦距，传感器高度，传感器宽度等信息,F0：{},C：{},D:{}", sysLntlagConfigVO.getF0(), sysLntlagConfigVO.getC(),sysLntlagConfigVO.getD());
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setMinFocus(sysLntlagConfigVO.getF0());
            deviceModelVO.setPicSensorLength(sysLntlagConfigVO.getC());
            deviceModelVO.setPicSensorWidth(sysLntlagConfigVO.getD());
            deviceModelVO.setMfgrCode(sysLntlagConfigVO.getModelId());
            deviceModelVO.setVertiDownNeg(sysLntlagConfigVO.getVertiDownNeg());

            BigDecimal u = BigDecimal.valueOf(Constants.ZERO_FIVE_NUM);
            BigDecimal v = BigDecimal.valueOf(Constants.ZERO_FIVE_NUM);
            // ---有压制区域则取压制区域中的框图中心点位   若前端告警没有压制区域 或者 后端告警框图点位不够三个 则取框图中心点(0.5,0.5)
            if(vo.getAlarmArea() != null && !vo.getAlarmArea().isEmpty()){
                // 获取置信度最大的告警区域的框图点位  没有点位或者点位不足三个则无法转义
                AlarmOriginalAreaVO alarmOriginalAreaVO = vo.getAlarmArea().stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO());
                if(alarmOriginalAreaVO.getAlarmPointList() != null && alarmOriginalAreaVO.getAlarmPointList().size() > 2) {
                    List<AlarmOriginalPointVO> alarmPointList = alarmOriginalAreaVO.getAlarmPointList();  // 取置信度最大的告警区域的框图坐标
                    // 百分比坐标系下的值u
                    u = (new BigDecimal(alarmPointList.get(0).getPercentX()).add(new BigDecimal(alarmPointList.get(2).getPercentX()))).divide(new BigDecimal(Constants.TWO), Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP);
                    // 百分比坐标系下的值v
                    v = (new BigDecimal(alarmPointList.get(0).getPercentY()).add(new BigDecimal(alarmPointList.get(2).getPercentY()))).divide(new BigDecimal(Constants.TWO), Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP);
                }
            }
            log.info("对PTZ进行处理,u的值为：{}，v的值为：{}", u, v);
            // 处理PT 使用转义后的PT值
            String panDeal = LongLatAngleUtils.paramTrans(vo.getPan(), deviceModelVO, Constants.TYPE_P);
            String tileDeal = LongLatAngleUtils.paramTrans(vo.getTilt(), deviceModelVO, Constants.TYPE_T);
            String zoom = LongLatAngleUtils.paramTrans(vo.getZoom(), deviceModelVO, Constants.TYPE_Z);
            // zoom为null 为空 为0  当1处理
            if(zoom == null || "".equals(zoom) || Constants.ZERO_STRING.equals(zoom) || Constants.ZERO_ZERO_STRING.equals(zoom) || String.valueOf(Constants.ZERO_NUM).equals(zoom)){
                zoom = "1";
            }

            //最小焦距
            BigDecimal fo = new BigDecimal(sysLntlagConfigVO.getF0());
            BigDecimal f = fo.multiply(new BigDecimal(zoom));
            log.info("对PTZ进行处理,f的值为：{}", f);
            //水平视场角
            BigDecimal aErFa = BigDecimal.valueOf(Constants.TWO_NUM).multiply(
                    BigDecimal.valueOf(Math.atan(new BigDecimal(sysLntlagConfigVO.getC()).divide(new BigDecimal(Constants.TWO).multiply(f), Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP).doubleValue()) * 180 / Math.PI));
            //垂直视场角
            BigDecimal beiTa = BigDecimal.valueOf(Constants.TWO_NUM).multiply(
                    BigDecimal.valueOf(Math.atan(new BigDecimal(sysLntlagConfigVO.getD()).divide(new BigDecimal(Constants.TWO).multiply(f), Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP).doubleValue()) * 180 / Math.PI));

            log.info("对PTZ进行处理,水平视场角aErFa：{},垂直视场角beiTa：{}", aErFa, beiTa);
            // 开始计算PTZ
            BigDecimal pan = ((u.subtract(new BigDecimal("0.5")))
                    .multiply(aErFa)).add(new BigDecimal(panDeal)).setScale(Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP);
            BigDecimal tile = ((new BigDecimal("0.5").subtract(v))
                    .multiply(beiTa)).add(new BigDecimal(tileDeal)).setScale(Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP);
            if(pan.compareTo(new BigDecimal(0))<0){
                pan = pan.add(new BigDecimal(360)).setScale(Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP);
            }else if(pan.compareTo(new BigDecimal(360))>=0){
                pan = pan.subtract(new BigDecimal(360)).setScale(Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP);
            }
            receiveAlarmVO.setPan(String.valueOf(pan));
            receiveAlarmVO.setTilt(String.valueOf(tile));
            log.info("对PTZ进行处理,原始PT：{}，处理后PT：{}",vo.getPan()+Constants.COMMA+vo.getTilt(),receiveAlarmVO.getPan()+Constants.COMMA+receiveAlarmVO.getTilt());

        }catch (Exception e){
            log.error(MessageConstants.PTZ_ERROR_MESSAGE,e);
        }
        return receiveAlarmVO;

    }

    /**
     * @description: 有效告警压制策略基础查询
     * @date: 2022/5/25
     * @param receiveAlarmVO,strategyVoList
     * @return: List<VmAlarmSuppressStrategyVO>
     */
    private List<VmAlarmSuppressStrategyVO> checkValidStrategy(ReceiveAlarmVO receiveAlarmVO,List<VmAlarmSuppressStrategyVO> strategyVoList) {
        List<VmAlarmSuppressStrategyVO> validList = new ArrayList<>();
        //String noticeResource = String.valueOf(receiveAlarmVO.getExtraInfo().getNoticeResource());
        //String msgType = receiveAlarmVO.getMsgType()==null?"":receiveAlarmVO.getMsgType();

        int alarmHour = Integer.parseInt(receiveAlarmVO.getAlarmTime().substring(Constants.ELEVEN_NUM, Constants.THIRTEEN_NUM));
        for(VmAlarmSuppressStrategyVO item:strategyVoList){
            /**
             * 如果压制基础策略中AI告警来源、天气状况和时间段不为空必须同时满足才为有效压制策略
             * 1、AI告警来源判断处理
             * 2、天气状况判断处理
             * 3、时间段判断处理
             */
            String icon = "";
            if(item.getWeatherConditions() != null && !StringUtils.isEmpty(item.getWeatherConditions())){
                QueryWeatherDTO dto = new QueryWeatherDTO();
                dto.setLongitude(receiveAlarmVO.getLongitude());
                dto.setLatitude(receiveAlarmVO.getLatitude());
                dto.setAdcode(receiveAlarmVO.getCityCode());
                Result<JSONObject> result = weatherApiService.getWeatherNow(dto);
                log.info("告警编码:{},请求天气信息入参:{},获取到的天气信息{}",receiveAlarmVO.getAlarmCode(), JSON.toJSONString(dto),JSON.toJSONString(result));
                if (ResultCode.SUCCESS.getCode() == result.getCode() && ObjectUtil.isNotNull(result.getData()) && !StringUtils.isEmpty(result.getData().get("icon"))) {
                    icon = result.getData().get("icon").toString();
                }
            }
            //告警来源匹配 AI告警来源判断处理
            Boolean alarmSourceFlag = true;
            if(item.getAlarmSource() != null && !StringUtils.isEmpty(item.getAlarmSource()) ){
                if(Arrays.asList(item.getAlarmSource().split(Constants.COMMA)).contains(receiveAlarmVO.getAlarmSource())){
                    if(Constants.WARNING_SOURCE_4.equals(receiveAlarmVO.getAlarmSource())){
                        //二次研判判断处理
                        if((Constants.ZERO.equals(receiveAlarmVO.getIsSecondJudge())&& Objects.nonNull(item.getAlarmIfJudge()) && item.getAlarmIfJudge().contains(Constants.ZERO)) ||  //不进行二次研判
                                (Constants.ONE.equals(receiveAlarmVO.getIsSecondJudge()) && Objects.nonNull(item.getAlarmJudgeResult()) && item.getAlarmJudgeResult().contains(receiveAlarmVO.getSecondJudgeResult()))){  //二次研判结果
                            alarmSourceFlag = true;
                        }else{
                            alarmSourceFlag = false;
                        }
                    }
                }else{
                    alarmSourceFlag = false;
                }
            }

            if( alarmSourceFlag &&
                    (item.getWeatherConditions() == null || StringUtils.isEmpty(item.getWeatherConditions()) || Arrays.asList(item.getWeatherConditions().split(Constants.COMMA)).contains(icon)) &&  // 天气状况判断处理
                    (item.getPeriodTime() == null || StringUtils.isEmpty(item.getPeriodTime()) || Arrays.stream(item.getPeriodTime().split(Constants.COMMA)).filter(record -> !StringUtils.isEmpty(record)).map(Integer::parseInt).collect(Collectors.toList()).contains(alarmHour))){  // 时间段判断处理
                validList.add(item);
            }
        }
        return validList;
    }

}

package com.chinatower.event.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.mapper.DeviceMapper;
import com.chinatower.event.mapper.VmAlarmAreaSuppressMapper;
import com.chinatower.event.mapper.VmAlarmSuppressStrategyMapper;
import com.chinatower.event.mapper.VmAlarmTypeSuppressMapper;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.ISuppressStrategyService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.DistanceUtils;
import com.chinatower.event.utils.PTZUtils;
import com.chinatower.event.vo.*;
import com.chinatower.event.vo.entity.*;
import com.chinatower.order.api.IWeatherApiService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.order.QueryWeatherDTO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.overlay.OverlayOp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @className: SuppressStrategyService
 * @description: 压制策略处理service
 * @author: luo.zhenghong
 * @date: 2022/5/25
 * @requestID 需求单号及需求名称
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class SuppressStrategyServiceImpl implements ISuppressStrategyService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private VmAlarmTypeSuppressMapper vmAlarmTypeSuppressMapper;
    @Resource
    private VmAlarmSuppressStrategyMapper vmAlarmSuppressStrategyMapper;
    @Resource
    private VmAlarmAreaSuppressMapper vmAlarmAreaSuppressMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private IDeviceRepository deviceRepository;
    @Autowired
    private IWeatherApiService weatherApiService;
    @Resource
    private StrategyServiceImpl strategyService;

    private final String TRAFFIC_FLOW_CODE = "1313101";
    private final String PEOPLE_FLOW_CODE = "1212116";
    private final String PEOPLE_DENSITY_CODE = "1212128";


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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean doStrategyIntercept(ReceiveAlarmVO receiveAlarmVO,List<VmAlarmSuppressStrategyVO> strategyVoList) {
        //检查告警压制策略符合的数据
        try {
            List<VmAlarmSuppressStrategyVO> validStrategyList = this.checkValidStrategy(receiveAlarmVO,strategyVoList);
            if(!validStrategyList.isEmpty()){
                //进行告警压制数据压制判断处理
                log.info("告警编码:{},匹配到的策略:{}",receiveAlarmVO.getAlarmCode(),validStrategyList);
                if(Constants.NINE.equals(receiveAlarmVO.getAlarmSource())){
                    return this.checkSuppressBySatellite(receiveAlarmVO,validStrategyList);
                }else{
                    return this.checkSuppress(receiveAlarmVO,validStrategyList);
                }

            }else{
                return false;
            }
        }catch (Exception e){
            log.info("处理告警压制数据异常：", e);
        }
        return false;
    }

    /**
     * @description: 告警压制处理
     * @date: 2022/5/25
     * @param validStrategyList
     * @return: boolean
     */
    private boolean checkSuppress(ReceiveAlarmVO receiveAlarmVO,List<VmAlarmSuppressStrategyVO> validStrategyList) {

        boolean suppressFlag = false;
        for(VmAlarmSuppressStrategyVO item:validStrategyList){
            try{
                /**
                 * 如果压制方式中时间段、置信度压制、区域屏蔽压制和重复告警压制 按照优先级只要有一个符合则跳出循环进行压制
                 * 1、压制时间、时间段判断处理
                 * 2、置信度压制处理
                 * 3、区域屏蔽压制判断处理
                 * 4、重复告警压制判断处理
                 */
                log.info("告警编码:{},当前匹配的策略名称:{},压制时间段:{},置信度阈值:{},重复告警时间:{}",receiveAlarmVO.getAlarmCode(),item.getAlarmSuppressName(),item.getSuppressPeriodTime(),
                        item.getSuppressThresh(),item.getAlarmInterval()+item.getAlarmIntervalType());
                // 时间段判断处理

                int alarmHour = Integer.parseInt(receiveAlarmVO.getAlarmTime().substring(Constants.ELEVEN_NUM, Constants.THIRTEEN_NUM));
                //获取到告警时间（yyyy-MM-dd格式）
                Date alarmTime = DateUtils.stringToDateYMDHMS(receiveAlarmVO.getAlarmTime().substring(Constants.ZERO_NUM, Constants.TEN_NUM));
                if(!StringUtils.isEmpty(item.getSuppressPeriodTime()) && Arrays.stream(item.getSuppressPeriodTime().split(Constants.COMMA)).map(Integer::parseInt).collect(Collectors.toList()).contains(alarmHour)){
                    if(StringUtils.isEmpty(item.getSuppressStartTime()) || ((alarmTime.after(item.getSuppressStartTime()) || alarmTime.equals(item.getSuppressStartTime())) && (alarmTime.before(item.getSuppressEndTime()) || alarmTime.equals(item.getSuppressEndTime())))){
                        receiveAlarmVO.setAlgorithmTime(item.getSuppressPeriodTime());
                        receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALGORITHM_TIME);
                        suppressFlag = true;
                        log.info("告警编码:{},时间段告警压制",receiveAlarmVO.getAlarmCode());
                        break;
                    }
                }

                // 置信度压制处理
                if(item.getSuppressThresh() != null){
                    suppressFlag = doInterceptByThresh(item,receiveAlarmVO);
                    if(suppressFlag){
                        break;
                    }
                }

                // 区域屏蔽压制判断处理
                if(item.getAreaSuppressVoList()!=null && !item.getAreaSuppressVoList().isEmpty()){
                    suppressFlag = doInterceptByArea(item,receiveAlarmVO);
                    if(suppressFlag){
                        break;
                    }
                }

                // 重复告警压制判断处理  重复告警坐标范围、重复告警时间间隔
                if(item.getAlarmInterval() != null && item.getAlarmIntervalType() != null){
                    int alarmInterval = item.getAlarmInterval();
                    String alarmIntervalType = item.getAlarmIntervalType();
                    if(alarmInterval != 0 && !StringUtils.isEmpty(alarmIntervalType)){
                        suppressFlag = doInterceptByTime(item,receiveAlarmVO);
                        if(suppressFlag){
                            break;
                        }
                    }
                }

                //算法阈值压制处理
                if( item.getTypeSuppressVoList() != null){
                    for (VmAlarmTypeSuppressVO temp : item.getTypeSuppressVoList()) {
                        if(!StringUtils.isEmpty(temp.getThresholdValue())){
                            suppressFlag = doInterceptByAlgorithm(receiveAlarmVO,temp.getThresholdValue());
                            if(suppressFlag){
                                break;
                            }
                        }
                    }

                }
            }catch (Exception e){
                log.info("处理告警压制数据异常：", e);
            }
        }
        return suppressFlag;
    }

    /**
     * @description: 告警压制处理(卫星告警单独处理)
     * @date: 2022/5/25
     * @param validStrategyList
     * @return: boolean
     */
    private boolean checkSuppressBySatellite(ReceiveAlarmVO receiveAlarmVO,List<VmAlarmSuppressStrategyVO> validStrategyList) {

        boolean suppressFlag = false;
        for(VmAlarmSuppressStrategyVO item:validStrategyList){
            /**
             * 如果压制方式中时间段、置信度压制、区域屏蔽压制和重复告警压制 按照优先级只要有一个符合则跳出循环进行压制
             * 1、压制时间、时间段判断处理
             * 2、置信度压制处理
             * 3、区域屏蔽压制判断处理
             * 4、重复告警压制判断处理
             */
            log.info("告警编码:{},当前匹配的策略名称:{},压制时间段:{},置信度阈值:{},重复告警时间:{}",receiveAlarmVO.getAlarmCode(),item.getAlarmSuppressName(),item.getSuppressPeriodTime(),
                    item.getSuppressThresh(),item.getAlarmInterval()+item.getAlarmIntervalType());
            // 时间段判断处理

            int alarmHour = Integer.parseInt(receiveAlarmVO.getAlarmTime().substring(Constants.ELEVEN_NUM, Constants.THIRTEEN_NUM));
            //获取到告警时间（yyyy-MM-dd格式）
            Date alarmTime = DateUtils.stringToDateYMDHMS(receiveAlarmVO.getAlarmTime().substring(Constants.ZERO_NUM, Constants.TEN_NUM));
            if(!StringUtils.isEmpty(item.getSuppressPeriodTime()) && Arrays.stream(item.getSuppressPeriodTime().split(Constants.COMMA)).map(Integer::parseInt).collect(Collectors.toList()).contains(alarmHour)){
                if(StringUtils.isEmpty(item.getSuppressStartTime()) || ((alarmTime.after(item.getSuppressStartTime()) || alarmTime.equals(item.getSuppressStartTime())) && (alarmTime.before(item.getSuppressEndTime()) || alarmTime.equals(item.getSuppressEndTime())))){
                    receiveAlarmVO.setAlgorithmTime(item.getSuppressPeriodTime());
                    receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALGORITHM_TIME);
                    suppressFlag = true;
                    log.info("告警编码:{},时间段告警压制",receiveAlarmVO.getAlarmCode());
                    break;
                }
            }

            // 置信度压制处理
            if(item.getSuppressThresh() != null){
                suppressFlag = doInterceptByThreshBySatellite(item,receiveAlarmVO);
                if(suppressFlag){
                    break;
                }
            }
            // 重复告警压制判断处理  重复告警坐标范围、重复告警时间间隔
            if(item.getAlarmInterval() != null && item.getAlarmIntervalType() != null){
                int alarmInterval = item.getAlarmInterval();
                String alarmIntervalType = item.getAlarmIntervalType();
                if(alarmInterval != 0 && !StringUtils.isEmpty(alarmIntervalType)){
                    suppressFlag = doInterceptByTimeBySatellite(item,receiveAlarmVO);
                    if(suppressFlag){
                        break;
                    }
                }
            }
        }
        return suppressFlag;
    }

    private boolean doInterceptByAlgorithm( ReceiveAlarmVO receiveAlarmVO, String thresholdValue){
        boolean suppressFlag = false;
        String alarmBody = receiveAlarmVO.getAlarmBody();
        //获取告警内容中()中的数字
        if(alarmBody.contains("(") && alarmBody.contains(")")){
            int alarmNum = Integer.parseInt(alarmBody.substring(alarmBody.indexOf("(")+1, alarmBody.indexOf(")")));
            //告警阈值
            int thresholdNum = Integer.parseInt(thresholdValue);
            //告警数量小于设置的阈值时进行压制
            if(thresholdNum>alarmNum){
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALGORITHM);
                suppressFlag = true;
                log.info("告警编码:{},算法阈值压制",receiveAlarmVO.getAlarmCode());
            }
        }
        return suppressFlag;
    }

    private boolean doInterceptByTimeBySatellite(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        boolean suppressFlag = false;
        log.info("重复告警压制-告警编码:{},所匹配的重复告警压制策略:{}",receiveAlarmVO.getAlarmCode(),JSON.toJSONString(item));

        // 获取最新告警时间
        String cacheKey = Constants.ALARM_TIME_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode();

        String alarmTime = (String)redisUtil.get(cacheKey, RedisDBType.PRIMARY);
        log.info("重复告警压制-redisKey:{},redisValue:{}",Constants.ALARM_TIME_KEY+receiveAlarmVO.getTenantId()+ receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode(),alarmTime);
        if(!StringUtils.isEmpty(alarmTime)) { //没有查到则为首次上报 不压制
            // 延用之前的时间判断方法
            int alarmInterval = item.getAlarmInterval();
            String alarmIntervalType = item.getAlarmIntervalType();
            // 将时间转化为秒
            int timeLimit = alarmInterval * EnumUtil.TimeUnit.getSecond(alarmIntervalType);
            suppressFlag = checkIntercept(timeLimit, DateUtils.string2Date(alarmTime), DateUtils.string2Date(receiveAlarmVO.getAlarmTime()));
            if(suppressFlag){
                List<RedisPositionVo> redisPositionList = new ArrayList<>();
                //告警时间有经纬度，取重复告警中redis中得所有经纬度
                if(!StringUtils.isEmpty(receiveAlarmVO.getLatitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLongitude())) {
                    redisPositionList = reGetRedisPositionList(receiveAlarmVO,item);
                }else{
                    suppressFlag = false;
                }
                if(!StringUtils.isEmpty(item.getAlarmSpaceRange())){
                    //判断经纬度是否符合压制
                    suppressFlag = this.compareDistance(receiveAlarmVO,redisPositionList,item.getAlarmSpaceRange());
                    if(suppressFlag){
                        log.info("doInterceptByTimeBySatellite-工单被告警空间压制:"+receiveAlarmVO.getAlarmCode());
                    }
                }else{
                    suppressFlag = true;
                }
            }
            if(suppressFlag) {
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALARM_INTERVAL);
                receiveAlarmVO.setPrevAlarmTime(alarmTime);
                log.info("告警编码:{},重复告警压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }


    private boolean doInterceptByTime(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        boolean suppressFlag = false;
        log.info("重复告警压制-告警编码:{},所匹配的重复告警压制策略:{}",receiveAlarmVO.getAlarmCode(),JSON.toJSONString(item));

        // 对PTZ转义处理
        ReceiveAlarmVO alarmVO = new ReceiveAlarmVO();
        List<RedisPTVo> redisPTList = new ArrayList<>();
        if(!StringUtils.isEmpty(item.getAlarmRangeP()) && !StringUtils.isEmpty(item.getAlarmRangeT())) {
            alarmVO = dealPtzByIconArea(receiveAlarmVO);   // 没有查到摄像机厂商则走时间的判断
            redisPTList = reGetRedisPtList(receiveAlarmVO,item);  // 有设置PT范围时，从Redis获取该租户、行业、摄像机、告警类型下该事件的历史未超期的PT信息
        }
        List<RedisPositionVo> redisPositionList = new ArrayList<>();
        //告警时间有经纬度，取重复告警中redis中得所有经纬度
        if(!StringUtils.isEmpty(receiveAlarmVO.getLatitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLongitude())) {
            redisPositionList = reGetRedisPositionList(receiveAlarmVO,item);
        }

        // 获取最新告警时间
        String cacheKey = Constants.ALARM_TIME_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode();


        String alarmTime = (String)redisUtil.get(cacheKey, RedisDBType.PRIMARY);
        log.info("重复告警压制-redisKey:{},redisValue:{}",Constants.ALARM_TIME_KEY+receiveAlarmVO.getTenantId()+ receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode(),alarmTime);
        if(!StringUtils.isEmpty(alarmTime)) { //没有查到则为首次上报 不压制
            // 延用之前的时间判断方法
            int alarmInterval = item.getAlarmInterval();
            String alarmIntervalType = item.getAlarmIntervalType();
            // 将时间转化为秒
            int timeLimit = alarmInterval * EnumUtil.TimeUnit.getSecond(alarmIntervalType);
            suppressFlag = checkIntercept(timeLimit, DateUtils.string2Date(alarmTime), DateUtils.string2Date(receiveAlarmVO.getAlarmTime()));
            boolean timeFlag = suppressFlag;
            log.info("doInterceptByTime-timeFlag:{},alarmCode:{}",JSON.toJSONString(timeFlag),JSON.toJSONString(receiveAlarmVO.getAlarmCode()));
            // 时间验证需要压制，则判断是否符合PT范围内  在范围内则压制  范围外则不压制  若查询redis中pan tile 为空  或者 处理PTZ返回为空  则走时间的判断规则
            if(suppressFlag && !CollectionUtils.isEmpty(redisPTList) && !redisPTList.isEmpty() && !StringUtils.isEmpty(alarmVO.getPan()) && !StringUtils.isEmpty(alarmVO.getTilt())
                    && !StringUtils.isEmpty(item.getAlarmRangeP()) && !StringUtils.isEmpty(item.getAlarmRangeT())){
                suppressFlag = false;
                for (RedisPTVo redisPTVo : redisPTList) {
                    String redisPan = redisPTVo.getP();
                    String redisTile = redisPTVo.getT();

                    BigDecimal alarmRangeP = new BigDecimal(item.getAlarmRangeP());
                    BigDecimal alarmRangeT = new BigDecimal(item.getAlarmRangeT());
                    BigDecimal alarmRangePMin = new BigDecimal(redisPan).subtract(alarmRangeP);

                    BigDecimal reAlarmRangePMin = alarmRangePMin.compareTo(new BigDecimal(Constants.ZERO_NUM)) < 0?
                            alarmRangePMin.add(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)):new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360);

                    BigDecimal alarmRangePMax = new BigDecimal(redisPan).add(alarmRangeP);

                    BigDecimal reAlarmRangePMax = alarmRangePMax.compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) > 0?
                            alarmRangePMax.subtract(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)):new BigDecimal(Constants.ZERO_NUM);

                    BigDecimal alarmRangeTMin = new BigDecimal(redisTile).subtract(alarmRangeT);
                    BigDecimal alarmRangeTMax = new BigDecimal(redisTile).add(alarmRangeT);
                    log.info("重复告警压制-告警编码:{},redis:{},alarmRangeP:{},alarmRangeT:{},alarmRangePMin:{},alarmRangePMax:{},alarmRangeTMin:{},alarmRangeTMax:{}",receiveAlarmVO.getAlarmCode(),alarmTime,alarmRangeP,alarmRangeT,alarmRangePMin,alarmRangePMax,alarmRangeTMin,alarmRangeTMax);
                    // 若处理后的PTZ与上一个告警的PTZ不在alarmRangeP、alarmRangeT的范围内 则不压制  视为新的告警
                    if((
                            (new BigDecimal(alarmVO.getPan()).compareTo(alarmRangePMin) >= 0 && new BigDecimal(alarmVO.getPan()).compareTo(alarmRangePMax) <= 0)
                                    || (new BigDecimal(alarmVO.getPan()).compareTo(reAlarmRangePMin) > 0 && new BigDecimal(alarmVO.getPan()).compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) < 0)
                                    || (new BigDecimal(alarmVO.getPan()).compareTo(new BigDecimal(Constants.ZERO_NUM)) > 0 && new BigDecimal(alarmVO.getPan()).compareTo(reAlarmRangePMax) < 0)
                        )
                            && new BigDecimal(alarmVO.getTilt()).compareTo(alarmRangeTMin) >= 0 && new BigDecimal(alarmVO.getTilt()).compareTo(alarmRangeTMax) <= 0){
                        suppressFlag = true;
                        break;
                    }
                }
            }
            //告警范围关系是并且且PT不符合压制，不压制
            if(timeFlag && Constants.ONE.equals(item.getAlarmRangeStatus()) && !suppressFlag){
                suppressFlag = false;
            }
            //告警范围关系是并且且PT符合压制，及需要判断经纬度是否符合压制
            if(timeFlag && suppressFlag && Constants.ONE.equals(item.getAlarmRangeStatus()) && !CollectionUtils.isEmpty(redisPositionList)){
                if(!StringUtils.isEmpty(item.getAlarmSpaceRange())){
                    //判断经纬度是否符合压制
                    suppressFlag = this.compareDistance(receiveAlarmVO,redisPositionList,item.getAlarmSpaceRange());
                    if(suppressFlag){
                        log.info("doInterceptByTime-工单被告警空间范围压制-且:"+receiveAlarmVO.getAlarmCode());
                    }
                }else{
                    suppressFlag = true;
                }

            }
            //告警范围关系是或者且PT符合压制，压制
            if(timeFlag && Constants.ZERO.equals(item.getAlarmRangeStatus()) && suppressFlag ){
                if(!StringUtils.isEmpty(item.getAlarmRangeP()) && !StringUtils.isEmpty(item.getAlarmRangeT())){
                    suppressFlag = true;
                    if(suppressFlag){
                        log.info("doInterceptByTime-工单被PT范围压制-或:"+receiveAlarmVO.getAlarmCode());
                    }
                }else{
                    //PT范围未配置，以空间范围是否符合为准
                    //判断经纬度是否符合压制
                    if(!StringUtils.isEmpty(item.getAlarmSpaceRange())){
                        suppressFlag = this.compareDistance(receiveAlarmVO,redisPositionList,item.getAlarmSpaceRange());
                    }else{
                        log.info("doInterceptByTime-工单被配置项为空压制-或:"+receiveAlarmVO.getAlarmCode());
                        suppressFlag = true;
                    }
                    if(suppressFlag){
                        log.info("doInterceptByTime-工单被告警空间范围压制-或:"+receiveAlarmVO.getAlarmCode());
                    }
                }
            }
            //告警范围关系是或者且PT不符合压制，判断经纬度是否符合压制
            if(timeFlag && Constants.ZERO.equals(item.getAlarmRangeStatus()) && !suppressFlag ){
                if(!StringUtils.isEmpty(item.getAlarmSpaceRange())){
                    //判断经纬度是否符合压制
                    suppressFlag = this.compareDistance(receiveAlarmVO,redisPositionList,item.getAlarmSpaceRange());
                    if(suppressFlag){
                        log.info("doInterceptByTime-工单被告警空间范围压制-或:"+receiveAlarmVO.getAlarmCode());
                    }
                }else{
                    //空间范围配置为空，PT有配置不压制，PT配置为空，压制
                    if(!StringUtils.isEmpty(item.getAlarmRangeP()) && !StringUtils.isEmpty(item.getAlarmRangeT())){
                        suppressFlag = false;
                    }else{
                        log.info("doInterceptByTime-工单被配置项为空压制-或:"+receiveAlarmVO.getAlarmCode());
                        suppressFlag = true;
                    }
                }
            }
            if(suppressFlag) {
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALARM_INTERVAL);
                receiveAlarmVO.setPrevAlarmTime(alarmTime);
                log.info("告警编码:{},重复告警压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }

    public boolean compareDistance(ReceiveAlarmVO receiveAlarmVO,List<RedisPositionVo> redisPositionList,String alarmSpaceRange){
        boolean flag = false;
        for (RedisPositionVo vo:redisPositionList) {
            if(!StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude())){
                //计算两个将维度之间得距离
                double distance = DistanceUtils.getDistance(Double.parseDouble(receiveAlarmVO.getLongitude()), Double.parseDouble(receiveAlarmVO.getLatitude()),
                        Double.parseDouble(vo.getLongitude()), Double.parseDouble(vo.getLatitude()));
                String realDistance = String.format("%.2f", distance);
                if(new BigDecimal(realDistance).compareTo(new BigDecimal(alarmSpaceRange)) <=0){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private List<RedisPTVo> reGetRedisPtList(ReceiveAlarmVO receiveAlarmVO,VmAlarmSuppressStrategyVO item) {
        // 配置了PT范围则将设置的时间限制存储用于redis更新
        int timeLimit = item.getAlarmInterval() * EnumUtil.TimeUnit.getSecond(item.getAlarmIntervalType()) * Constants.ONE_THOUSAND;
        receiveAlarmVO.setOrderTimeLimit(timeLimit);

        String cachePtKey = Constants.ALARM_PT_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode();

        String value = RedisUtil.get(cachePtKey, RedisDBType.PRIMARY);

        log.info("告警编码：{}，redisKey：{}，告警重复压制获取到的redisValue：{}",receiveAlarmVO.getAlarmCode(),cachePtKey,value);

        if(!StringUtils.isEmpty(value)){
            List<RedisPTVo> redisPTList = JSON.parseArray(value,RedisPTVo.class);
            redisPTList = redisPTList.stream().filter(temp ->
                    DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAt()).getTime() <= timeLimit ).collect(Collectors.toList());

            //RedisUtil.set(cachePtKey, JSON.toJSONString(redisPTList), RedisDBType.PRIMARY);
            return redisPTList;
        }
        return Collections.emptyList();
    }

    /**
     * 获取redis存储得重复告警经纬度
     * @param receiveAlarmVO
     * @param item
     * @return
     */
    private List<RedisPositionVo> reGetRedisPositionList(ReceiveAlarmVO receiveAlarmVO,VmAlarmSuppressStrategyVO item) {
        // 配置了PT范围则将设置的时间限制存储用于redis更新
        int timeLimit = item.getAlarmInterval() * EnumUtil.TimeUnit.getSecond(item.getAlarmIntervalType()) * Constants.ONE_THOUSAND;
        receiveAlarmVO.setOrderTimeLimit(timeLimit);

        String cachePtKey = Constants.ALARM_POSITION_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode();

        String value = RedisUtil.get(cachePtKey, RedisDBType.PRIMARY);

        log.info("告警编码：{}，redisKey：{}，告警重复压制获取到的redisValue：{}",receiveAlarmVO.getAlarmCode(),cachePtKey,value);

        if(!StringUtils.isEmpty(value)){
            List<RedisPositionVo> redisPositionList = JSON.parseArray(value,RedisPositionVo.class);
            redisPositionList = redisPositionList.stream().filter(temp ->
                    DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAlarmTime()).getTime() <= timeLimit ).collect(Collectors.toList());
            return redisPositionList;
        }
        return Collections.emptyList();
    }

    /**
     * @description: 通过压制区域来判断是否压制 true为压制，false为不压制
     * @date: 2022/5/16
     * @param item,receiveAlarmVO
     * @return: boolean
     */
    private boolean doInterceptByArea(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        // 该告警策略下所有匹配的压制区域
        List<VmAlarmAreaSuppressVO> areaSuppressVoList = item.getAreaSuppressVoList();

        SysLntlagConfigVO config = deviceRepository.getLntlagConfig(receiveAlarmVO.getDeviceCode());
        // 设备厂商对应参数信息不全则跳过区域屏蔽压制
        if (!Objects.isNull(config) && !Objects.isNull(config.getF0()) && !Objects.isNull(config.getC()) && !Objects.isNull(config.getD())) {
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setMinFocus(config.getF0());
            deviceModelVO.setPicSensorLength(config.getC());
            deviceModelVO.setPicSensorWidth(config.getD());
            deviceModelVO.setMfgrCode(config.getModelId());
            deviceModelVO.setFocusRange(config.getFocusRange());
            deviceModelVO.setVertiDownNeg(config.getVertiDownNeg());

            String pan = LongLatAngleUtils.paramTrans(receiveAlarmVO.getPan(), deviceModelVO, Constants.TYPE_P);
            String tile = LongLatAngleUtils.paramTrans(receiveAlarmVO.getTilt(), deviceModelVO, Constants.TYPE_T);
            String zoom = LongLatAngleUtils.paramTrans(receiveAlarmVO.getZoom(), deviceModelVO, Constants.TYPE_Z);
            // zoom为null 为空 为0  当1处理
            if(zoom == null || "".equals(zoom) || Constants.ZERO_STRING.equals(zoom) || Constants.ZERO_ZERO_STRING.equals(zoom) ||  String.valueOf(Constants.ZERO_NUM).equals(zoom)){
                zoom = "1";
            }
            VmAlarmAreaSuppressVO alarmAreaSuppressVo = new VmAlarmAreaSuppressVO();
            alarmAreaSuppressVo.setPointsP(pan);
            alarmAreaSuppressVo.setPointsT(tile);
            alarmAreaSuppressVo.setPointsZ(zoom);

            boolean flag = false;
            for (VmAlarmAreaSuppressVO vmAlarmAreaSuppressVo : areaSuppressVoList) {
                // 获取压制区域的所有点位  PT
                List<SuppressPointPTVO> pointPTVos = JSON.parseArray(vmAlarmAreaSuppressVo.getPointsPt(), SuppressPointPTVO.class);
                if(pointPTVos == null || pointPTVos.size() < 3 ){   //没有查到标绘点位或者点位不足三个则不进行区域压制判断
                    // 未获取到压制区域的PT坐标信息 或者信息有误 则尝试重新获取
                    log.info("未获取到该压制区域的pointPT:{}",vmAlarmAreaSuppressVo);

                    List<SuppressPointVO> pointsList = JSON.parseArray(Constants.PARAM_NULL.equals(vmAlarmAreaSuppressVo.getPoints())?"":vmAlarmAreaSuppressVo.getPoints(), SuppressPointVO.class);
                    if(Constants.ONE.equals(vmAlarmAreaSuppressVo.getAreaType()) && pointsList!=null && !pointsList.isEmpty()){

                        DeviceModelVO modelVo = new DeviceModelVO();
                        modelVo.setMinFocus(config.getF0());
                        modelVo.setPicSensorLength(config.getC());
                        modelVo.setPicSensorWidth(config.getD());

                        List<SuppressPointPTVO> listPT = new ArrayList<>();
                        for(SuppressPointVO pointTemp:pointsList){
                            SuppressPointPTVO pointPTVo = PTZUtils.pointTranformPT(pointTemp,vmAlarmAreaSuppressVo,modelVo);
                            listPT.add(pointPTVo);
                        }
                        String jsonPointPT = JSON.toJSONString(listPT);
                        vmAlarmAreaSuppressVo.setPointsPt(jsonPointPT);
                    }else if(Constants.TWO.equals(vmAlarmAreaSuppressVo.getAreaType())){
                        strategyService.groupPT(vmAlarmAreaSuppressVo);
                        if(StringUtils.isEmpty(vmAlarmAreaSuppressVo.getPointsPt())){
                            continue;
                        }
                    }else{
                        continue;
                    }
                    // 重新获取后重新验证
                    pointPTVos = JSON.parseArray(vmAlarmAreaSuppressVo.getPointsPt(), SuppressPointPTVO.class);
                    log.info("重新获取到该压制区域的pointPT:{}",vmAlarmAreaSuppressVo);
                    if(pointPTVos == null || pointPTVos.size() < 3 ){
                        continue;
                    }
                }
                StringBuilder suppressPointPt = new StringBuilder();
                suppressPointPt.append(Constants.PARAM_POLYGON).append("((");
                for (SuppressPointPTVO suppressPointPTVo : pointPTVos) {
                    suppressPointPt.append(suppressPointPTVo.getPointP()).append(" ").append(suppressPointPTVo.getPointT()).append(Constants.COMMA);
                }
                suppressPointPt = new StringBuilder(suppressPointPt.substring(0,suppressPointPt.length()-1));
                suppressPointPt.append("))");
                log.info("告警编码:{},压制区域点位拼接：{}",receiveAlarmVO.getAlarmCode(),suppressPointPt);



                // 循环转义坐标为PTZ 若框图存在获取框图的所有点位
                StringBuilder pointPtIcon = new StringBuilder();
                // 告警框图点位 x y
                boolean isHasIcon = false;

                if(receiveAlarmVO.getAlarmArea() != null && !receiveAlarmVO.getAlarmArea().isEmpty()) {
                    // 获取置信度最大的告警区域的框图点位
                    AlarmOriginalAreaVO alarmOriginalAreaVO = receiveAlarmVO.getAlarmArea().stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO());
                    if(alarmOriginalAreaVO.getAlarmPointList() != null && alarmOriginalAreaVO.getAlarmPointList().size() > 2){
                        List<AlarmOriginalPointVO> alarmPointList = alarmOriginalAreaVO.getAlarmPointList();
                        pointPtIcon.append(Constants.PARAM_POLYGON).append("((");
                        String pointPOne = "";
                        String pointTOne = "";
                        for (AlarmOriginalPointVO alarmOriginalPointVO : alarmPointList) {
                            SuppressPointVO suppressPointVo = new SuppressPointVO();
                            suppressPointVo.setSuppressPointId(alarmOriginalPointVO.getAlarmPointId());
                            suppressPointVo.setPercentX(Float.parseFloat(alarmOriginalPointVO.getPercentX()));
                            suppressPointVo.setPercentY(Float.parseFloat(alarmOriginalPointVO.getPercentY()));

                            SuppressPointPTVO suppressPointPTVo = PTZUtils.pointTranformPT(suppressPointVo, alarmAreaSuppressVo, deviceModelVO);
                            if(alarmPointList.indexOf(alarmOriginalPointVO) == 0){
                                pointPOne = suppressPointPTVo.getPointP();
                                pointTOne = suppressPointPTVo.getPointT();
                            }
                            pointPtIcon.append(suppressPointPTVo.getPointP()).append(" ").append(suppressPointPTVo.getPointT()).append(Constants.COMMA);
                        }
                        // 重新增加第一个点位 形成闭环图形
                        pointPtIcon.append(pointPOne).append(" ").append(pointTOne).append("))");
                        isHasIcon = true;
                    }
                }
                log.info("告警编码:{},告警框图点位拼接：{}",receiveAlarmVO.getAlarmCode(),pointPtIcon);
                try {
                    // 获取告警区域与框图区域相交面积
                    GeometryFactory geometryFactory = new GeometryFactory();
                    WKTReader reader = new WKTReader(geometryFactory);
                    Polygon iconArea = null;
                    Polygon suppressArea = null;
                    iconArea = (Polygon) reader.read(pointPtIcon.toString());
                    suppressArea = (Polygon) reader.read(suppressPointPt.toString());
                    if(suppressArea != null){
                        if(isHasIcon && iconArea != null){  //存在框图  则对比重叠面积 对比覆盖率  完全覆盖完全被覆盖都要压制
                            OverlayOp op = new OverlayOp(iconArea,suppressArea);
                            Geometry g = op.getResultGeometry(OverlayOp.INTERSECTION);
                            double overlapArea = g.getArea();
                            BigDecimal scalingRate = BigDecimal.valueOf(overlapArea).divide(BigDecimal.valueOf(iconArea.getArea()),Constants.TWO_NUM,BigDecimal.ROUND_HALF_UP);
                            log.info("告警区域压制，alarmCode:{},框图与压制区域的重叠面积:{},框图面积:{},压制区域面积:{},比例:{}",receiveAlarmVO.getAlarmCode(),overlapArea,iconArea.getArea(),suppressArea.getArea(),scalingRate);
                            if(scalingRate.compareTo(new BigDecimal(vmAlarmAreaSuppressVo.getOverlapRate())) > -1 || overlapArea == iconArea.getArea() || overlapArea == suppressArea.getArea()){
                                flag = true;
                                break;
                            }
                        }else{
                            Coordinate coordinate = new Coordinate(Double.parseDouble(pan),Double.parseDouble(tile));
                            Point point = geometryFactory.createPoint(coordinate);
                            log.info("告警区域压制，alarmCode:{},点位实体:{},PT值:{}",receiveAlarmVO.getAlarmCode(),point,pan+Constants.COMMA+tile);
                            if(point.within(suppressArea) || point.intersects(suppressArea)){
                                flag = true;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.info("通过坐标获取告警框图面积以及重叠区域面积出错: ",e);
                }
            }
            if(flag){
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_AREA);
                log.info("告警编码:{},区域告警压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }

    /**
     * @description: 通过置信度阈值判断是否压制 true为压制，false为不压制
     * @date: 2022/5/25
     * @param item,receiveAlarmVO
     * @return: boolean
     */
    private boolean doInterceptByThreshBySatellite(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        float suppressThresh = item.getSuppressThresh();
        StringBuilder nowThresh = new StringBuilder();
        if (!StringUtils.isEmpty(receiveAlarmVO.getConfidenceLevel())) {
            //存在综合置信度，用综合置信度压制
            log.info("告警编码:{},综合置信度:{}.策略中置信度阈值:{}",receiveAlarmVO.getAlarmCode(),receiveAlarmVO.getConfidenceLevel(),suppressThresh);
            nowThresh.append(receiveAlarmVO.getConfidenceLevel());
            if (Float.parseFloat(receiveAlarmVO.getConfidenceLevel()) < suppressThresh) {
                receiveAlarmVO.setThresh(suppressThresh);
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_THRESH);
                receiveAlarmVO.setNowThresh(nowThresh.toString());
                log.info("告警编码:{},综合置信度压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }

    /**
     * @description: 通过置信度阈值判断是否压制 true为压制，false为不压制
     * @date: 2022/5/25
     * @param item,receiveAlarmVO
     * @return: boolean
     */
    private boolean doInterceptByThresh(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        float suppressThresh = item.getSuppressThresh();
        StringBuilder nowThresh = new StringBuilder();
        if (!StringUtils.isEmpty(receiveAlarmVO.getCompConfidenceLevel())) {
            //存在综合置信度，用综合置信度压制
            log.info("告警编码:{},综合置信度:{}.策略中置信度阈值:{}",receiveAlarmVO.getAlarmCode(),receiveAlarmVO.getCompConfidenceLevel(),suppressThresh);
            nowThresh.append(receiveAlarmVO.getCompConfidenceLevel());
            if (Float.parseFloat(receiveAlarmVO.getCompConfidenceLevel()) < suppressThresh) {
                receiveAlarmVO.setThresh(suppressThresh);
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_THRESH);
                receiveAlarmVO.setNowThresh(nowThresh.toString());
                log.info("告警编码:{},综合置信度压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        } else if(!CollectionUtils.isEmpty(receiveAlarmVO.getAlarmArea())){
            // 不存在综合置信度，用最大置信度压制
            List<AlarmOriginalAreaVO> alarmOriginalAreaVOS = receiveAlarmVO.getAlarmArea();
            boolean flag = true;
            for (AlarmOriginalAreaVO alarmOriginalAreaVO : alarmOriginalAreaVOS) {
                nowThresh.append(alarmOriginalAreaVO.getConfidenceLevel()).append(Constants.COMMA);
                // 其中一个值大于置信度，直接跳出
                if (Float.parseFloat(alarmOriginalAreaVO.getConfidenceLevel()) >= suppressThresh) {
                    log.info("告警编码:{},最大置信度:{}.策略中置信度阈值:{}",receiveAlarmVO.getAlarmCode(),alarmOriginalAreaVO.getConfidenceLevel(),suppressThresh);
                    flag = false;
                    break;
                }
            }
            if(flag){
                receiveAlarmVO.setThresh(suppressThresh);
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_THRESH);
                receiveAlarmVO.setNowThresh(nowThresh.substring(Constants.ZERO_NUM, nowThresh.length() - Constants.ONE_NUM));
                log.info("告警编码:{},置信度压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
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
                log.info("告警编码:{},请求天气信息入参:{},获取到的天气信息{}",receiveAlarmVO.getAlarmCode(),JSON.toJSONString(dto),JSON.toJSONString(result));
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

    /**
     * 校验是否超出时间段限制，超出不做压制：返回false；在时间范围内需要压制：返回true
     * @param timeLimit 告警间隔时长
     * @param oldTime 上次告警时间
     * @param newTime 本次告警时间
     * @return
     */
    private boolean checkIntercept(Integer timeLimit, Date oldTime, Date newTime) {
        if (newTime.getTime() - oldTime.getTime() > timeLimit * Constants.ONE_THOUSAND) {
            return false;
        }
        return true;
    }

    @DS(EbikeDataBaseConstant.BASE)
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

}

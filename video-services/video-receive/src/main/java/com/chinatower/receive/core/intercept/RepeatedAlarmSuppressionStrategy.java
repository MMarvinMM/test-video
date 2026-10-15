package com.chinatower.receive.core.intercept;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.util.DistanceUtils;
import com.chinatower.receive.vo.entity.RedisPTVo;
import com.chinatower.receive.vo.entity.RedisPositionVo;
import com.chinatower.receive.vo.entity.SysLntlagConfigVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: RepeatedAlarmSuppressionStrategy
 * @description: 压制方式-重复告警压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("repeatedAlarmStrategy")
@Slf4j
public class RepeatedAlarmSuppressionStrategy extends BaseAlarmSuppressStategy {

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        Boolean suppressFlag = false;
        // 重复告警压制判断处理  重复告警坐标范围、重复告警时间间隔
        if(strategy.getAlarmInterval() != null && strategy.getAlarmIntervalType() != null){
            int alarmInterval = strategy.getAlarmInterval();
            String alarmIntervalType = strategy.getAlarmIntervalType();
            if(alarmInterval != 0 && !StringUtils.isEmpty(alarmIntervalType)){
                suppressFlag = doInterceptByTime(strategy,receiveAlarmVO);
            }
        }
        return suppressFlag;
    }

    @Override
    public int getOrder() {
        return Constants.FOUR_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.REPEATED_ALARM_STRATEGY.getDisplayName().equals(stategyType);
    }

    private boolean doInterceptByTime(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        boolean suppressFlag = false;
        log.info("重复告警压制-告警编码:{},所匹配的重复告警压制策略:{}",receiveAlarmVO.getAlarmCode(), JSON.toJSONString(item));

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

    @DS(EbikeDataBaseConstant.BASE)
    public ReceiveAlarmVO dealPtzByIconArea(ReceiveAlarmVO vo) {
        ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
        try {
            //DeviceRepository deviceRepository = SpringUtil.getBean(DeviceRepository.class);
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
}

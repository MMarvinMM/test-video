package com.chinatower.receive.core.intercept;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.util.DistanceUtils;
import com.chinatower.receive.vo.entity.RedisPositionVo;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: RepeatedAlarmStrategyBySatellite
 * @description: 卫星告警压制方式-重复告警压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("wxRepeatedAlarmStrategy")
@Slf4j
public class RepeatedAlarmStrategyBySatellite extends BaseAlarmSuppressStategy {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        Boolean suppressFlag = false;
        // 重复告警压制判断处理  重复告警坐标范围、重复告警时间间隔
        if(strategy.getAlarmInterval() != null && strategy.getAlarmIntervalType() != null){
            int alarmInterval = strategy.getAlarmInterval();
            String alarmIntervalType = strategy.getAlarmIntervalType();
            if(alarmInterval != 0 && !StringUtils.isEmpty(alarmIntervalType)){
                suppressFlag = doInterceptByTimeBySatellite(strategy,receiveAlarmVO);
            }
        }
        return suppressFlag;
    }

    private Boolean doInterceptByTimeBySatellite(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
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

    @Override
    public int getOrder() {
        return Constants.THREE_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.WX_REPEATED_ALARM_STRATEGY.getDisplayName().equals(stategyType);
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

}

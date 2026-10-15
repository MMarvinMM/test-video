package com.chinatower.receive.core.intercept;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.vo.dto.UavFlyInfoVo;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 无人机起飞时间压制
 * @Author: songxinfeng
 * @Date: 2025/9/18 11:16
 */

@Component("uavFlyTimeStrategy")
@Slf4j
public class UavFlyTimeSuppressionStrategy extends BaseAlarmSuppressStategy{

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        log.info("开始匹配无人机起飞时间压制规则:{}", JSON.toJSONString(strategy));
        String alarmTime = receiveAlarmVO.getAlarmTime();
        if((!StringUtils.isEmpty(strategy.getBeginRecognizeTime()) && !StringUtils.isEmpty(strategy.getBeginRecognizeTimeType())) ||
                (!StringUtils.isEmpty(strategy.getEndRecognizeTime()) && !StringUtils.isEmpty(strategy.getEndRecognizeTimeType()))){
            //压制配置有该压制规则，判断当前告警时间是否在配置的识别时间区间（起飞时间~开始识别时间  结束识别时间~飞行结束时间）
            //查询无人机起飞时间及飞行时长
            UavFlyInfoVo uavFlyInfoVo = deviceRepository.queryUavFlyInfo(receiveAlarmVO.getTaskRecordId());
            log.info("无人机告警压制查询飞行任务起飞时长:",JSON.toJSONString(uavFlyInfoVo));
            if(uavFlyInfoVo != null && StringUtils.isEmpty(uavFlyInfoVo.getRealityTime())){
                uavFlyInfoVo.setRealityTime(uavFlyInfoVo.getPlanTime());
            }
            if(uavFlyInfoVo != null &&  !StringUtils.isEmpty(uavFlyInfoVo.getRealityTime())){
                //获取起飞时间秒数
                Integer flyTimeSecond = getSecondsFromDateString(uavFlyInfoVo.getRealityTime());
                //获取当前告警的秒数
                Integer alarmTimeSecond = getSecondsFromDateString(alarmTime);
                if(flyTimeSecond != null  && alarmTimeSecond != null){
                    Integer beginTimeSecond = null;
                    //开始识别时间秒数
                    if(!StringUtils.isEmpty(strategy.getBeginRecognizeTime())){
                        beginTimeSecond = Integer.valueOf(strategy.getBeginRecognizeTime());
                        if(Constants.ONE.equals(strategy.getBeginRecognizeTimeType())){
                            //配置问分钟
                            beginTimeSecond = Integer.valueOf(strategy.getBeginRecognizeTime()) * 60;
                        }
                    }
                    //结束识别时间秒数
                    Integer endTimeSecond = null;
                    if(!StringUtils.isEmpty(strategy.getEndRecognizeTime())){
                        //结束识别时间秒数
                        endTimeSecond = Integer.valueOf(strategy.getEndRecognizeTime());
                        if(Constants.ONE.equals(strategy.getEndRecognizeTimeType())){
                            //配置问分钟
                            endTimeSecond = Integer.valueOf(strategy.getEndRecognizeTime()) * 60;
                        }
                    }
                   if(beginTimeSecond != null){
                       //开始判断当前告警时间是否在（起飞时间~开始识别时间  结束识别时间~飞行结束时间）
                       if((alarmTimeSecond-flyTimeSecond) <= beginTimeSecond){
                           log.info("该告警在起飞时间到开始识别时间之间:",JSON.toJSONString(receiveAlarmVO.getTaskRecordId()));
                           receiveAlarmVO.setIntercept(AlgorithmEventVO.UAV_FLY_TIME_INTERVAL);
                           return true;
                       }
                   }
                    if(endTimeSecond != null){
                        if((alarmTimeSecond-flyTimeSecond) >= endTimeSecond ){
                            log.info("该告警在结束识别时间到飞行结束时间之间:",JSON.toJSONString(receiveAlarmVO.getTaskRecordId()));
                            receiveAlarmVO.setIntercept(AlgorithmEventVO.UAV_FLY_TIME_INTERVAL);
                            return true;
                        }
                    }
                }else{
                    log.info("计算时间秒数异常，检查日志查看数据,配置:{},告警:{}",JSON.toJSONString(JSON.toJSONString(strategy)),JSON.toJSONString(receiveAlarmVO));
                }
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return Constants.ONE_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return false;
    }

    /**
     * 根据日期格式字符串返回对应秒数
     * @param dateStr
     * @return
     */
    public Integer getSecondsFromDateString(String dateStr){
        try {
            // 定义日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateStr);
            // 获取毫秒数，转换为秒数（除以1000）
            return (int) (date.getTime() / 1000);
        }catch (Exception e){
            log.error("获取描述异常:"+e);
        }
        return null;
    }

}

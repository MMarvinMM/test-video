package com.chinatower.receive.core.intercept;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.receive.vo.entity.VmAlarmTypeSuppressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @className: AlgorithmThresholdSuppressionStrategy
 * @description: 压制方式-算法阈值压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("algorithmThresholdStrategy")
@Slf4j
public class AlgorithmThresholdSuppressionStrategy extends BaseAlarmSuppressStategy {

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        log.info("AlgorithmThresholdSuppressionStrategy-apply-开始算法阈值压制:{}",JSON.toJSONString(strategy));
        boolean suppressFlag = false;
        // 重复告警压制判断处理  重复告警坐标范围、重复告警时间间隔
        if( strategy.getTypeSuppressVoList() != null){
            for (VmAlarmTypeSuppressVO temp : strategy.getTypeSuppressVoList()) {
                if(!StringUtils.isEmpty(temp.getThresholdValue())){
                    suppressFlag = doInterceptByAlgorithm(receiveAlarmVO,temp.getThresholdValue(),temp.getTypeValue());
                    if(suppressFlag){
                        break;
                    }
                }
            }

        }
        return suppressFlag;
    }

    @Override
    public int getOrder() {
        return Constants.FIVE_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.ALGORITHM_THRESHOLD_STRATEGY.getDisplayName().equals(stategyType);
    }

    public static void main(String[] args) {
        String a = "[{\\\\\\\"alarmAreaId\\\\\\\":\\\\\\\"3888498154018981\\\\\\\",\\\\\\\"attrList\\\\\\\":[{\\\\\\\"attrValue\\\\\\\":4,\\\\\\\"attrType\\\\\\\":\\\\\\\"objectCount\\\\\\\"}]}]";
        String correctJson = a.trim().replaceAll("^\"|\"$", "");
        // 3. 第二步：取消内部双引号的转义（\" → "）
        correctJson = correctJson.replace("\\", "");
        List<Map> extendFieldList = JSON.parseArray(correctJson, Map.class);
        System.out.println(JSON.toJSONString(extendFieldList));

    }

    private boolean doInterceptByAlgorithm( ReceiveAlarmVO receiveAlarmVO, String thresholdValue,String warningTypeId){
        log.info("doInterceptByAlgorithm-thresholdValue:{},receiveVo:{}",thresholdValue,JSON.toJSONString(receiveAlarmVO));
        boolean suppressFlag = false;
        //无人机算法阈值压制
        if("12".equals(receiveAlarmVO.getAlarmSource())){
            log.info("doInterceptByAlgorithm-无人机阈值压制");
            if(warningTypeId.equals(receiveAlarmVO.getAlarmType()) && !StringUtils.isEmpty(receiveAlarmVO.getExtraField())){
                // 2. 第一步：去掉外层多余的双引号（用trim()避免前后空格影响）
                String correctJson = receiveAlarmVO.getExtraField().trim().replaceAll("^\"|\"$", "");
                // 3. 第二步：取消内部双引号的转义（\" → "）
                correctJson = correctJson.replace("\\", "");
                List<Map> extendFieldList = JSON.parseArray(correctJson, Map.class);
                //获取到extendField的attrList
                List<Map<String, Object>> attrList = extendFieldList.stream()
                        .findFirst()
                        .map(item -> (List<Map<String, Object>>) item.get(Constants.PARAM_ATTR_LIST))
                        .orElse(new ArrayList<>());
                log.info("AlgorithmThresholdSuppressionStrategy-doInterceptByAlgorithm-attrList:{}",JSON.toJSONString(attrList));
                for (Map<String,Object> map:attrList) {
                    if(map != null){
                        if("objectCount".equals(map.get("attrType"))){
                            if(!StringUtils.isEmpty(map.get("attrValue"))){
                                int uavAlarmNum = Integer.valueOf(String.valueOf(map.get("attrValue")));
                                //告警阈值
                                int thresholdNum = Integer.parseInt(thresholdValue);
                                //告警数量小于设置的阈值时进行压制
                                if(thresholdNum > uavAlarmNum){
                                    receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALGORITHM);
                                    suppressFlag = true;
                                    log.info("告警编码:{},算法阈值压制",receiveAlarmVO.getAlarmCode());
                                }
                            }
                        }
                    }
                }
            }
        }else{
            log.info("doInterceptByAlgorithm-摄像机阈值压制");
            //摄像机算法阈值压制
            String alarmBody = receiveAlarmVO.getAlarmBody();
            //获取告警内容中()中的数字
            if(warningTypeId.equals(receiveAlarmVO.getAlarmType()) && alarmBody.contains("(") && alarmBody.contains(")")){
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
        }
        return suppressFlag;
    }
}

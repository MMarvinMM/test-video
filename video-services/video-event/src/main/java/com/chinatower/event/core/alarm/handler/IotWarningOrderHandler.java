package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.dto.ReceiveIotAlarmConfigDto;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.WarningSensorMonitorVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @Description 物联设备生成告警事件
 * @Author luo.zhenghong
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Component
@Slf4j
@Scope("prototype")
public class IotWarningOrderHandler extends AbstractHandler {

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    /**
     * 执行逻辑
     *
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备生成告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            long beginTime = System.currentTimeMillis();
            List<ReceiveAlarmVO> receiveAlarmVOList = receiveAlarmDTO.getList();
            // 接收告警集合
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            // 运营研判告警集合
            List<ReceiveAlarmVO> operations = new ArrayList<>();
            // 告警压制集合
            List<ReceiveAlarmVO> intercepts = new ArrayList<>();
            //从redis获取告警类型名称列表
            //List<Map> alarmTypeList = warningOrderRepository.getAlarmTypeList();
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
                if (this.getIotAlarmSuppress(receiveAlarmVO, intercepts)){
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

            receiveAlarmDTO.setIntercepts(intercepts);
            receiveAlarmDTO.setOperations(operations);
            receiveAlarmDTO.setList(alarms);

            long endTime = System.currentTimeMillis();
            log.info("IotWarningOrderHandler excute cost: {}", (endTime - beginTime));
        }catch(Exception e){
            log.info("物联设备生成告警事件异常：", e);
        }

        return this.excuteNextHandler(receiveAlarmDTO);
    }

    //查询配置下是否存在告警压制时效
    private boolean getIotAlarmSuppress (ReceiveAlarmVO receiveAlarmVO,List<ReceiveAlarmVO> intercepts){
        boolean isSuppress = false;
        ReceiveIotAlarmConfigDto iotAlarmConfigDto = receiveAlarmVO.getIotAlarmConfigDto();
        //查询告警压制时效
        if (StringUtils.isNotEmpty(iotAlarmConfigDto.getInterceptTime())){
            String key = Constants.IOT_ALARM_SUPPRESS_REDIS_KEY+iotAlarmConfigDto.getConfigId();
            Long expire = RedisUtil.getExpire(key, RedisDBType.PRIMARY);
            if (expire<=0){
                isSuppress =  false;
            }else {
                isSuppress =  true;
            }
        }
        //如果为true 存在告警压制时效放入回收站
        if (isSuppress){
            intercepts.add(receiveAlarmVO);
            return true;
        }
        return false;
    }
    /**
     * 构建告警对象
     *
     * @param receiveAlarmVO
     * @return
     */
    private ReceiveAlarmVO buildAlarmVo(ReceiveAlarmVO receiveAlarmVO, List<Map> alarmTypeList) {
        //更改工单编码生成规则 add by hued 20211103
        receiveAlarmVO.setWarningOrderId(PKUtils.getWarnOrderId(receiveAlarmVO.getTenantId(), receiveAlarmVO.getAlarmTime()));
        receiveAlarmVO.setNotifyType(Constants.YES_FLAG);
        receiveAlarmVO.setAlarmCode(receiveAlarmVO.getWarningOrderId());
        receiveAlarmVO.setWarningSource(receiveAlarmVO.getAlarmSource());

        //alarmTypeList.stream().filter(alarmTypeMap -> Objects.equals(String.valueOf(receiveAlarmVO.getAlarmType()), String.valueOf(alarmTypeMap.get(Constants.PARAM_TYPE_VALUE_)))).forEach(map -> receiveAlarmVO.setWarningTypeName((String) map.get(Constants.PARAM_TYPENAME)));
        receiveAlarmVO.setWarningTypeId(String.valueOf(receiveAlarmVO.getAlarmType()));
        receiveAlarmVO.setEmergencyLevel(EnumUtil.AlarmLevel.GENERALLY.getCode());

        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
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

}

package com.chinatower.event.core.alarm.strategy;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IWarningEventService;
import com.chinatower.device.entity.vo.DeviceErrorVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.handler.AbstractHandler;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 处理设备告警
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("deviceAlarmStrategy")
@Slf4j
public class DeviceAlarmStrategy  extends AlarmBaseStategy {

    @Autowired
    private IWarningEventService warningEventService;


    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        String alarmType = String.valueOf(receiveAlarmVO.getAlarmType());
        return alarmType.startsWith(Constants.IMPORTANCE_LEVEL_2) && StringUtils.isEmpty(receiveAlarmVO.getAlarmSource());
    }

    /**
     * 重写父类中的执行逻辑
     * @param receiveAlarmDTO
     */
    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        // 故障消息
        DeviceErrorVO deviceErrorVO = new DeviceErrorVO();
        BeanUtils.copyProperties(receiveAlarmDTO.getReceiveAlarmVO(), deviceErrorVO);

        Result result = warningEventService.insertDeviceError(deviceErrorVO);

        if (result != null && ResultCode.SUCCESS.getCode() != result.getCode()) {
            throw new Exception("处理摄像机设备告警失败");
        }
    }

    /**
     * 执行特有处理器逻辑
     * @return
     */
    @Override
    public AbstractHandler specialHandle() {
        return null;
    }


    @Override
    public String getStrategyName(){
        return "deviceAlarmStrategy";
    }
}

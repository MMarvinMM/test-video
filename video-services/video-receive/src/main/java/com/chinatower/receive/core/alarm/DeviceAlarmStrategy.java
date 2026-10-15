package com.chinatower.receive.core.alarm;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IWarningEventService;
import com.chinatower.device.entity.vo.DeviceErrorVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 故障消息接收策略实现类
 * @Author: songxinfeng
 * @Date: 2024/8/6 10:36
 * @version release_v1.6.90
 */

@Component("deviceAlarmStrategy")
@Slf4j
public class DeviceAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private IWarningEventService warningEventService;
    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        String alarmType = String.valueOf(receiveAlarmVO.getAlarmType());
        return alarmType.startsWith(Constants.IMPORTANCE_LEVEL_2) && StringUtils.isEmpty(receiveAlarmVO.getAlarmType());

    }

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

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        return null;
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        log.info("该策略无需重写此方法");
    }

    @Override
    public String getStrategyName() {
        return "deviceAlarmStrategy";
    }
}

package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description 物联设备生成告警后设备联动动作
 * @Author luo.zhenghong
 * @Date 2022/7/25
 * @Version release_v1.5
 **/
@Slf4j
@Component
@Scope("prototype")
public class IotSpecialHandler extends AbstractHandler {

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备生成告警后设备联动动作入参：{}", JSON.toJSONString(receiveAlarmDTO));
        // 批量保存告警信息
        try {
            Set<String> deviceCodeSet = this.getDistinctDeviceCode(receiveAlarmDTO);
            //每个设备单独开启一个异步线程去做联动抓取动作
            for (String deviceCode : deviceCodeSet) {
                //物联设备告警联动动作处理
                warningOrderUtil.iotDeviceDeal(receiveAlarmDTO, deviceCode);
            }

        }
        catch (Exception e) {
            log.error("物联设备联动动作失败!", e);
        }
        return true;
    }

    /**
     * 获取告警事件、运营研判、告警压制去重后的设备编码
     * @param receiveAlarmDTO
     */
    private Set<String> getDistinctDeviceCode(ReceiveAlarmDTO receiveAlarmDTO) {
        Set<String> deviceCodeSet = new HashSet<>();
        receiveAlarmDTO.getList().stream().forEach(alarm -> deviceCodeSet.add(alarm.getDeviceCode()));
        receiveAlarmDTO.getOperations().stream().forEach(operation -> deviceCodeSet.add(operation.getDeviceCode()));
        receiveAlarmDTO.getIntercepts().stream().forEach(intercept -> deviceCodeSet.add(intercept.getDeviceCode()));
        return deviceCodeSet;
    }
}

package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.consumer.service.AlarmOperateService;
import com.chinatower.platform.consumer.service.SensorAttributeService;
import com.chinatower.platform.consumer.service.WarningOrderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-out-data
 * @ClassName SensorAttributePushController
 * @Description 物联设备kafka消息模拟处理
 */
@Slf4j
@RestController
@Api(tags = "物联设备kafka消息模拟处理")
public class SensorAttributePushController {

    @Autowired
    private SensorAttributeService sensorAttributeService;

    @Autowired
    private WarningOrderService warningOrderService;

    @Autowired
    private AlarmOperateService alarmOperateService;

    /**
     * 物联设备kafka消息处理
     * @param sensorValue
     * @return
     */
    @PostMapping("/simulation/kafka/sensorAttributePush")
    public Result<String> sensorAttributePush(@RequestBody String sensorValue) {
        sensorAttributeService.dealMsg(sensorValue);
        return Result.success("物联设备kafka消息处理完成");
    }

    /**
     * 监听kafka中工单表消息
     * @param orderValue
     * @return
     */
    @PostMapping("/simulation/kafka/warningOrderInfo")
    public Result<String> warningOrderInfo(@RequestBody String orderValue) {
        warningOrderService.dealOrderMsg(orderValue);
        return Result.success("监听kafka中工单表消息成功！");
    }

    /**
     * 监听kafka中工单环节流程表消息
     * @param recordValue
     * @return
     */
    @PostMapping("/simulation/kafka/orderFlowRecordInfo")
    public Result<String> orderFlowRecordInfo(@RequestBody String recordValue) {
        warningOrderService.dealRecordMsg(recordValue);
        return Result.success("监听kafka中工单环节流程表消息成功！");
    }

    /**
     * 监听kafka中回收站表消息
     * @param orderValue
     * @return
     */
    @PostMapping("/simulation/kafka/warningInterceptInfo")
    public Result<String> warningInterceptInfo(@RequestBody String orderValue) {
        alarmOperateService.dealInterceptMsg(orderValue);
        return Result.success("监听kafka中回收站表消息成功！");
    }

    /**
     * 监听kafka中运营研判表消息
     * @param orderValue
     * @return
     */
    @PostMapping("/simulation/kafka/alarmOperaterInfo")
    public Result<String> alarmOperaterInfo(@RequestBody String orderValue) {
        alarmOperateService.dealOperateMsg(orderValue);
        return Result.success("监听kafka中运营研判表消息成功！");
    }
}

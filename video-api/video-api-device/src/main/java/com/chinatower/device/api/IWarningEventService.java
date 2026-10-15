package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.WarningEventFallbackFactory;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceErrorVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "warningEventService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = WarningEventFallbackFactory.class)
public interface IWarningEventService {

    /**
     * 功能描述: 预警事件-查询站址信息
     *
     * @param: siteRequestDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/3/10
     * @version: 1.0
     */
    @PostMapping("/video/warningEvent/querySitesForWE")
    Result querySitesForWE(@RequestBody SiteRequestDTO siteRequestDTO);


    /**
     * 故障告警消息报文
     * @apiNote 故障告警消息报文
     * @apiVersion 3.8
     * @tag 三、设备能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010100001180000011",
     * 	"channelCode": "11010100001310606880",
     * 	"alarmCode": "11010100001310606wqdsdfgvgregngmh",
     * 	"alermType": 1,
     * 	"deviceName": "设备名称",
     * 	"channelName": "通道名称",
     * 	"alarmTime": "2020-08-27 18:32:52",
     * 	"alarmBody ": "设备离线"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "",
     * 	"time": 1694759468892
     * }
     * @param deviceErrorVO
     * @return
     */
    @PostMapping("/video/message/insertDeviceError")
    Result insertDeviceError(@RequestBody DeviceErrorVO deviceErrorVO);

}

package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.ThirdPartyAlarmVo;
import com.chinatower.event.repository.ThirdPartyAlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-event
 * 第三方告警配置Controller
 */
@RestController
@RequestMapping("/thirdPartyAlarm")
@Slf4j
public class ThirdPartyAlarmController {


    @Autowired
    private ThirdPartyAlarmService thirdPartyAlarmService;

    /**
     * 功能描述：第三方告警配置批量添加
     * @param list
     * @return
     */
    @PostMapping("/insertBatchThirdPartyAlarm")
    public Result insertBatchThirdPartyAlarm(@RequestBody List<ThirdPartyAlarmVo> list){
        return Result.data(thirdPartyAlarmService.insertBatch(list));
    }

    /**
     * 功能描述：根据租户ID、行业编码、删除标识查询详情
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 查询详情
     */
    @GetMapping("/details")
    public Result details(@RequestParam("tenantId") String tenantId,
                          @RequestParam("industryCode") String industryCode
    ){
        return Result.data(thirdPartyAlarmService.details(tenantId,industryCode));
    }

    /**
     * 功能描述：判断该租户是否开启第三方告警
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @GetMapping("/isOpenThirdPartyAlarm")
    public Result isOpenThirdPartyAlarm(@RequestParam("tenantId") String tenantId,@RequestParam("industryCode") String industryCode){
        return Result.data(thirdPartyAlarmService.isOpenThirdPartyAlarm(tenantId,industryCode));
    }

    /**
     * 功能描述：判断第三方告警配置表中 该租户是否有第三方告警数据
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @GetMapping("/tenantThirdPartyAlarm")
    public Result tenantThirdPartyAlarm(@RequestParam("tenantId") String tenantId,@RequestParam("industryCode") String industryCode){
        return Result.data(thirdPartyAlarmService.tenantThirdPartyAlarm(tenantId,industryCode));
    }

    /**
     * 功能描述：根据告警类型、租户ID和行业编码在流程模板关联告警类型表中查找对应的数据
     * @param alarmType 告警类型
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @GetMapping("/isThirdAlarmType")
    public Result isThirdAlarmType(@RequestParam("alarmType") String alarmType,@RequestParam("tenantId") String tenantId,@RequestParam("industryCode") String industryCode){
        return Result.data(thirdPartyAlarmService.isThirdAlarmType(alarmType,tenantId,industryCode));
    }
}

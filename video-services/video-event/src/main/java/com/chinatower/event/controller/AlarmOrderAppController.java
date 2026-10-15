package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.dto.AppOrderUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.repository.IAlarmOrderAppRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * video-event
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOrderAppController.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/19 16:16
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/order/app")
public class AlarmOrderAppController extends TowerBaseController {

    @Autowired
    private IAlarmOrderAppRepository alarmOrderAppRepository;

    /**
     * 功能描述：获取当前登录人上报的事件列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 11:00
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderListByUser")
    public TableDataInfo getOrderListByUser(@RequestBody AppOrderListByUserDTO dto) {
        return alarmOrderAppRepository.getOrderListByUser(dto);
    }

    /**
     * 功能描述：更新告警工单 告警类型和事发地点
     * @author: yongqi.han
     * @date: 2022/8/18
     * @param dto
     * @return: TableDataInfo
     */
    @PostMapping("/updateOrderAlarmType")
    public Result updateOrderAlarmType(@RequestBody AppOrderUpdateDto dto) {
        return alarmOrderAppRepository.updateOrderAlarmType(dto);
    }



}

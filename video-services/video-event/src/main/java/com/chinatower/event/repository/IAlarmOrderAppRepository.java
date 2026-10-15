package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.dto.AppOrderUpdateDto;

public interface IAlarmOrderAppRepository {

    /**
     * 获取当前登录人上报的事件列表
     * @param dto
     * @return
     */
    TableDataInfo getOrderListByUser(AppOrderListByUserDTO dto);

    /**
     * @description: 功能描述 更新告警工单 告警类型和事发地点
     * @author: yongqi.han
     * @date: 2022/8/18
     * @param dto
     * @return: TableDataInfo
     */
    Result updateOrderAlarmType(AppOrderUpdateDto dto);
}

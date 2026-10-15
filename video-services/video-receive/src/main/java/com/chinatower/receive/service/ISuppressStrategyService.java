package com.chinatower.receive.service;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:48
 */
public interface ISuppressStrategyService {

    /**
     * 告警压制策略查询接口
     * @param receiveAlarmVO
     * @param customers
     * @return
     */
    List<VmAlarmSuppressStrategyVO> queryStrategyList(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customers);


    /**
     * @description: 告警压制策略处理接口
     * @author: luo.zhenghong
     * @date: 2022/5/25
     * @param receiveAlarmVO 告警数据对象
     * @param strategyVoList 告警压制策略对象
     * @return: boolean
     */
    boolean doStrategyIntercept(ReceiveAlarmVO receiveAlarmVO, List<VmAlarmSuppressStrategyVO> strategyVoList);

    ReceiveAlarmVO dealPtzByIconArea(ReceiveAlarmVO vo);
}

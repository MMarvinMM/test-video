package com.chinatower.event.repository;

import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;

import java.util.List;


/**
 * @className: SuppressStrategyService
 * @description: 压制策略策略api
 * @author: luo.zhenghong
 * @date: 2022/5/25
 * @requestID 需求单号及需求名称
 */
public interface ISuppressStrategyService {

    /**
     * @description: 告警压制策略查询接口
     * @author: luo.zhenghong
     * @date: 2022/5/25
     * @param receiveAlarmVO 告警数据对象
     * @return: List<VmAlarmSuppressStrategyVo>
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

}

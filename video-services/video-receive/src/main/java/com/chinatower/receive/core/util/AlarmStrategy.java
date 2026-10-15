package com.chinatower.receive.core.util;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;

import java.util.List;

/**
 * @Description 处理告警定义策略接口
 * @Author lyy
 * @Date 2024/08/06 9:49
 * @Version release_v1.6.90
 **/
public interface AlarmStrategy {

    /**
     * 判断不同告警类型的方法
     * @param receiveAlarmVO
     * @return
     */
    boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO);

    /**
     * 处理告警核心方法
     * @param receiveAlarmDTO
     * @return
     */
    void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception;

    /**
     * 匹配租户
     * @param receiveAlarmVO
     * @return
     * @throws Exception
     */
    List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception;


    /**
     * 告警的地址和经纬度数据
     * @param receiveAlarmVO
     */
    void getAddress(ReceiveAlarmVO receiveAlarmVO);
}

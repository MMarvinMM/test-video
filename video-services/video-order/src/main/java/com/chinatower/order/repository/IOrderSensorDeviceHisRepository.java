package com.chinatower.order.repository;



import com.chinatower.common.core.domain.OrderSensorDeviceHisDO;

import java.util.List;

/**
 * @ClassName IOrderDeviceHisRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderSensorDeviceHisRepository {
    
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceHisList]
     * @return
     */
    void saveListBatch(List<OrderSensorDeviceHisDO> orderSensorDeviceHisList);
    
    /**
     * 功能描述 根据订单编码和迁移标识获取迁移数据
     * @author chenmf
     * @date 2022/5/26
     * [orderCode, moveSign]
     * @return
     */
    List<OrderSensorDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign);
}

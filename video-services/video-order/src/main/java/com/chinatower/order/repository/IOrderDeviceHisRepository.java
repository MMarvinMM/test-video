package com.chinatower.order.repository;

import com.chinatower.common.core.domain.OrderDeviceHisDO;

import java.util.List;

/**
 * @ClassName IOrderDeviceHisRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderDeviceHisRepository {
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceHisList]
     * @return
     */
    void saveListBatch(List<OrderDeviceHisDO> orderDeviceHisList);

    int batchInsert(List<OrderDeviceHisDO> orderDeviceDOList);

    /**
     * 功能描述 根据订单编码和迁移标识获取迁移数据
     * @author chenmf
     * @date 2022/1/11
     * [orderCode, moveSign]
     * @return
     */
    List<OrderDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign);
}

package com.chinatower.order.repository;

import com.chinatower.common.core.domain.OrderServiceRelHisDO;

import java.util.List;

public interface IOrderServiceRelHisRepository {

    /**
     * 功能描述
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderServicDOList]
     */
    void saveListBatch(List<OrderServiceRelHisDO> orderServiceRelDOList);

    /**
     * 功能描述 根据订单编码和迁移标识获取迁移数据
     *
     * @return
     * @author chenmf
     * @date 2022/5/26
     * [orderCode, moveSign]
     */
    List<OrderServiceRelHisDO> getOldOrderServiceRelHisByOrderCode(String orderCode, int moveSign);
}

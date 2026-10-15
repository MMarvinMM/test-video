package com.chinatower.order.repository;

import com.chinatower.common.core.domain.OrderHornDeviceHisDO;

import java.util.List;

public interface IOrderHornDeviceHisRepository {


    /**
     * 批量保存
     * @param list 设备数据信息
     */
    void saveListBatch(List<OrderHornDeviceHisDO> list);

    /**
     * 根据订单号查询设备信息
     * @param orderCode 订单编号
     * @param moveSign 移动次数
     * @return 设备信息
     */
    List<OrderHornDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign);
}

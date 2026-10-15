package com.chinatower.order.repository;

import com.chinatower.common.core.domain.OrderUavNestDeviceDO;
import com.chinatower.system.api.model.DeviceTreeVO;

import java.util.List;
import java.util.Map;

public interface IOrderUavNestDeviceRepository {

    /**
     * 根据订单编号和套餐编号查询设备信息
     * @param orderCodeList 订单编号
     * @param packageCodeList 套餐编号
     * @return 设备信息
     */
    List<OrderUavNestDeviceDO> getOrderDeviceByList(List<String> orderCodeList, List<String> packageCodeList);

    /**
     * 批量保存设备数据
     * @param list 设备数据信息
     */
    void saveListBatch(List<OrderUavNestDeviceDO> list);

    /**
     * 删除订单数据
     * @param orderCode 订单编号
     */
    void deleteByOrderCode(String orderCode);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);

    /**
     * 根据订单号获取无人机设备订购信息
     * @param orderCode 订单号
     * @return 无人机设备订购信息
     */
    Map<String, List<DeviceTreeVO>> getTenantUavDeviceMap(String orderCode);
}

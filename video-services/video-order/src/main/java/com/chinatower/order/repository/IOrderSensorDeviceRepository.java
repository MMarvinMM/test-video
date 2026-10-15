package com.chinatower.order.repository;



import com.chinatower.common.core.domain.OrderSensorDeviceDO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.system.api.model.DeviceTreeVO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IOrderDeviceRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderSensorDeviceRepository {
    
    /**
     * 功能描述 根据订单编码查询订购设备信息列表
     * @author chenmf
     * @date 2022/1/10
     * [orderCodeList, packageCodeList]
     * @return
     */
    List<OrderSensorDeviceDO> getOrderDeviceByList(List<String> orderCodeList, List<String> packageCodeList);
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceDOList]
     * @return
     */
    void saveListBatch(List<OrderSensorDeviceDO> orderSensorDeviceDOList);
    
    /**
     * 功能描述 删除订单数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    void deleteByOrderCode(String orderCode);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);

    Map<String, List<DeviceTreeVO>> getTenantSensorDeviceMap(String orderCode, Integer categoryCode);

    List<OrderDevicesVO> getOrderSensorDeviceByOrderCodeAndPackageCode(QueryOrderRelationshipVO req);

    List<OrderSensorDeviceDO> getOrderRadarDeviceByOrderCodesAndPackageCodes(List<String> orderCodeList, List<String> packageCodeList);

}

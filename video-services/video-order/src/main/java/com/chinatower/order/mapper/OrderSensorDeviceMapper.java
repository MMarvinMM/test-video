package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderSensorDeviceDO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.system.api.model.DeviceTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderDeviceMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderSensorDeviceMapper extends BaseMapper<OrderSensorDeviceDO> {

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode") String orderCode);

    /**
     * 根据订单号查设备列表
     * @param orderCode
     * @return
     */
    List<DeviceTreeVO> getTenantSensorDeviceList(String orderCode, Integer categoryCode);

    List<OrderDevicesVO> getOrderSensorDeviceByOrderCodeAndPackageCode(QueryOrderRelationshipVO req);

    List<OrderSensorDeviceDO> getOrderRadarDeviceByOrderCodesAndPackageCodes(@Param("orderCodeList")List<String> orderCodeList, @Param("packageCodeList")List<String> packageCodeList);
}

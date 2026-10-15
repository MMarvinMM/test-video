package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderHornDeviceDO;
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
public interface OrderHornDeviceMapper extends BaseMapper<OrderHornDeviceDO> {

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode") String orderCode);

    /**
     * 根据订单号查设备列表
     * @param orderCode 订单编号
     * @return 设备列表
     */
    List<DeviceTreeVO> getTenantHornDeviceList(@Param("orderCode") String orderCode);
}

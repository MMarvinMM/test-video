package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName OrderSensorDeviceHisDO
 * @Deacription 订单传感器设备通道历史表
 * @Author chenmf
 * @Date 2022/1/6 10:54
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_order_sensor_devices_his")
public class OrderSensorDeviceHisDO extends OrderSensorDeviceDomain {

}

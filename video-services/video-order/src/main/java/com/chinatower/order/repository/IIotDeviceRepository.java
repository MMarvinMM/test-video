package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.order.domain.IotDeviceDO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 物联设备表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
public interface IIotDeviceRepository extends IService<IotDeviceDO> {
    
    /**
     * 根据设备编码获取设备
     * @param: iotDeviceCodes
     * @return java.util.List<com.chinatower.order.domain.IotDeviceDO>
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    List<IotDeviceDO> listIotDeviceByCodes(List<String> iotDeviceCodes);


    List<IotDeviceDO> listIotDeviceBySet(Set<String> sensorCodeSet);
    
    /**
     * 构建物联设备信息
     * @param: iotDeviceMsg
     * @param: sameDeviceInfoList
     * @return com.chinatower.order.domain.IotDeviceDO
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    IotDeviceDO buildIotDeviceDo(IotDeviceMsgDTO iotDeviceMsg, List<IotDeviceDO> sameDeviceInfoList);
}

package com.chinatower.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.dataobject.VmDeviceIotRelDO;
import com.chinatower.device.dto.IotDeviceConfigVO;

import java.util.List;

/**
 * @ClassName IotDeviceConfigService
 * @Description
 * @Author lzt
 * @Date 2024/1/19
 **/
public interface IotDeviceConfigService extends IService<VmDeviceIotRelDO> {
    
    /**
     * 列表分页查询物联设备配置
     * @param iotDeviceConfigVO
     * @return
     */
    List<IotDeviceConfigVO> queryIotDeviceConfigList(IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 绑定物联设备
     * @param iotDeviceConfigVO
     */
    void bindIotDeviceConfig(IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 解绑物联设备
     * @param iotDeviceConfigVO
     */
     void unbindIotDeviceConfig(IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 更换物联设备
     * @param iotDeviceConfigVO
     */
     void changeIotDeviceConfig(IotDeviceConfigVO iotDeviceConfigVO);


}

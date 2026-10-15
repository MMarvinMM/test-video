package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.order.domain.HornDeviceDO;

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
public interface IHornDeviceRepository extends IService<HornDeviceDO> {

    /**
     * 查询已经存在的设备编码
     * @param deviceCodeList 设备编码
     * @return 已经存在的设备编码
     */
    List<String> selectExistDeviceCodeByCodes(List<String> deviceCodeList);


    List<HornDeviceDO> listDeviceInfoBySet(Set<String> sensorCodeSet);
}

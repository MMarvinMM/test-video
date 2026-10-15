package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.order.domain.UavNestDeviceDO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
public interface IUavNestDeviceRepository extends IService<UavNestDeviceDO> {

    /**
     * 根据设备编码获取设备
     * @param deviceCodeSet 设备编码
     * @return 设备信息
     */
    List<UavNestDeviceDO> listDeviceInfoBySet(Set<String> deviceCodeSet);
}

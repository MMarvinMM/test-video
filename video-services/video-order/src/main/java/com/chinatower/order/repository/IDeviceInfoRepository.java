package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.entity.dto.BasicDeviceDTO;
import com.chinatower.order.domain.DeviceInfoDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
public interface IDeviceInfoRepository extends IService<DeviceInfoDO> {
    
    
    /**
     * 查询该设备归属管理区域对应省市区
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    BasicDeviceDTO queryOrderDeviceAreaInfo(String areaCode);

    List<BasicDeviceDTO> queryOrderDeviceAreaInfoList(List<String> areaCodeList);
    
    /**
     * 根据设备编码获取设备
     *
     * @param deviceCode
     * @return DeviceInfoDO
     * @author zheng.yan
     * @date 2022/1/24
     */
    DeviceInfoDO getDeviceByCode(String deviceCode);

    List<DeviceInfoDO> getDeviceByCodeList(List<String> deviceCodeList);

    Map<String, BasicDeviceDTO> queryByDeviceCodes(List<String> deviceCodeList);
}

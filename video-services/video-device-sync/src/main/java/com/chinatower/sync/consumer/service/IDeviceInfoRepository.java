package com.chinatower.sync.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.core.dto.SiteInfoDto;
import com.chinatower.common.entity.Result;
import com.chinatower.sync.consumer.entity.DeviceInfoDO;

import java.util.List;

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
     * 根据站址信息更新设备中的站址相关信息
     *
     * @param siteInfoDto
     * @return com.chinatower.common.entity.Result
     * @author zheng.yan
     * @date 2022/1/18
     */
    @Deprecated
    void updateDeviceInfoBySiteInfo(SiteInfoDto siteInfoDto);


    /**
     * 根据设备编码获取设备
     *
     * @param deviceCodes
     * @return List<DeviceInfoDO>
     * @author zheng.yan
     * @date 2022/2/11
     * @version:
     */
    List<DeviceInfoDO> listDeviceByCodes(List<String> deviceCodes);

    /**
     * 视频质量诊断结果
     * @return
     */
    Result<String> dealVmsVodResult(List<String> messages);
}

package com.chinatower.platform.repository;

import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.TenantDeviceRelDTO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.video.api.entity.VideoPresetDO;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: IDeviceInfoRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/28 09:33
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface IDeviceRepository {

    /**
     * 查询设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    List<AlarmOrderByDeviceDTO> queryAlarmOrderByDevice(AlarmOrderDTO dto);

    /**
     * 通过网格id获取网格员
     * @param gridId
     * @return
     */
    String getGridKeeperByGridId(String gridId);

    /**
     * 根据用户ID查询用户信息
     * @param userIdList
     * @return
     */
    List<SysUserVO> getUserInfoByUserIds(List<String> userIdList);

    /**
     * 查询摄像机信息
     * @author lyy
     * @param deviceCodeList
     * @return
     */
    List<DeviceSiteVO> queryDevice(List<String> deviceCodeList);

    /**
     * 查询物联网设备信息
     *
     * @return
     * @author lyy
     * @date 2023/05/04
     * @version
     */
    List<DeviceSiteVO> queryIotDevice(List<String> deviceCodeList);

    /**
     * 根据预置位ID获取预置位名称
     * @param presetIdList
     * @return
     */
    List<VideoPresetDO> selectVideoPreById(List<String> presetIdList, List<String> deviceCodes);

    /**
     * 查询设备别名
     * @param tenantDeviceRelDTOList
     * @return
     */
    List<TenantDeviceEventRelVO> getDeviceRel(List<TenantDeviceRelDTO> tenantDeviceRelDTOList);

}

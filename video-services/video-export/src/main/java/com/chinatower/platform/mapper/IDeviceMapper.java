package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.TenantDeviceRelDTO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.entity.VmVideoPresetSnapshotMongo;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.enums.TableEnum;
import com.chinatower.video.api.entity.PresetDTO;
import com.chinatower.video.api.entity.VideoPresetDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: DeviceMapper.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/28 09:56
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Mapper
public interface IDeviceMapper {

    /**
     * 查询设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    List<AlarmOrderByDeviceDTO> queryAlarmOrderByDevice(@Param("param") AlarmOrderDTO dto);

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
    List<SysUserVO> getUserInfoByUserIds(@Param("userIdList")List<String> userIdList);

    /**
     * 查询摄像机信息
     * @param deviceCodeList
     * @return
     */
    List<DeviceSiteVO> queryDevice(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 查询物联网设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/19
     * @version
     */
    List<DeviceSiteVO> queryIotDevice(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 根据预置位ID获取预置位名称
     * @param presetIdList
     * @return
     */
    @DS(TableEnum.BASE)
        List<VideoPresetDO> selectVideoPreById(@Param("presetIdList") List<String> presetIdList, @Param("deviceCodes") List<String> deviceCodes);

    /**
     * 查询设备别名
     * @param tenantDeviceRelDTO
     * @return
     */
    List<TenantDeviceEventRelVO> getDeviceRel(TenantDeviceRelDTO tenantDeviceRelDTO);

    List<DeviceInfoVO> queryChannelInfoBatch(List<VmVideoPresetSnapshotMongo> list);

    List<PresetDTO> queryPresetInfoBatch(List<VmVideoPresetSnapshotMongo> list);

}

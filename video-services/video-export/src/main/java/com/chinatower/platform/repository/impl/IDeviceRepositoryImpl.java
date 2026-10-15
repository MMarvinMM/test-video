package com.chinatower.platform.repository.impl;

import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.TenantDeviceRelDTO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.mapper.IDeviceMapper;
import com.chinatower.platform.repository.IDeviceRepository;

import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.video.api.entity.VideoPresetDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: DeviceInfoRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/28 09:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class IDeviceRepositoryImpl implements IDeviceRepository {

    @Autowired
    private IDeviceMapper deviceMapper;

    /**
     * 查询设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    @Override
    public List<AlarmOrderByDeviceDTO> queryAlarmOrderByDevice(AlarmOrderDTO dto) {

        if (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()) || !StringUtils.isEmpty(dto.getDeviceName()) ||
                !CollectionUtils.isEmpty(dto.getModelIds())) {
            return deviceMapper.queryAlarmOrderByDevice(dto);
        } else {
            return null;
        }
    }

    /**
     * 通过网格id获取网格员
     *
     * @param gridId
     * @return
     */
    @Override
    public String getGridKeeperByGridId(String gridId) {
        return deviceMapper.getGridKeeperByGridId(gridId);
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userIdList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUserVO> getUserInfoByUserIds(List<String> userIdList) {
        if (!CollectionUtils.isEmpty(userIdList)) {
            return deviceMapper.getUserInfoByUserIds(userIdList);
        }
        return new ArrayList<>();
    }

    /**
     * 查询摄像机信息
     *
     * @param deviceCodeList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceSiteVO> queryDevice(List<String> deviceCodeList) {
        return deviceMapper.queryDevice(deviceCodeList);
    }

    /**
     * 查询物联网设备信息
     *
     * @return
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceSiteVO> queryIotDevice(List<String> deviceCodeList) {
        return deviceMapper.queryIotDevice(deviceCodeList);
    }

    /**
     * 根据预置位ID获取预置位名称
     * @param presetIdList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<VideoPresetDO> selectVideoPreById(List<String> presetIdList, List<String> deviceCodes) {
        return deviceMapper.selectVideoPreById(presetIdList,deviceCodes);
    }

    /**
     * 查询设备别名
     *
     * @param tenantDeviceRelDTOList
     * @return
     */
    @Override
    public List<TenantDeviceEventRelVO> getDeviceRel(List<TenantDeviceRelDTO> tenantDeviceRelDTOList) {
        TenantDeviceRelDTO dto = new TenantDeviceRelDTO();
        dto.setDeviceCodes(
                tenantDeviceRelDTOList.stream().map(TenantDeviceRelDTO::getDeviceCode).collect(Collectors.toList())
        );
        dto.setChannelCodes(
                tenantDeviceRelDTOList.stream().map(TenantDeviceRelDTO::getChannelCode).collect(Collectors.toList())
        );
        if (!StringUtils.isEmpty(tenantDeviceRelDTOList) && tenantDeviceRelDTOList.size()>0){
            dto.setTenantId(tenantDeviceRelDTOList.get(0).getTenantId());
        }
        List<TenantDeviceEventRelVO> list = deviceMapper.getDeviceRel(dto);
        return list;
    }
}

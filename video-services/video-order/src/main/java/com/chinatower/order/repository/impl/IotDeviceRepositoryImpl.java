package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.order.domain.IotDeviceDO;
import com.chinatower.order.mapper.IotDeviceMapper;
import com.chinatower.order.repository.IIotDeviceRepository;
import com.chinatower.platform.constants.DeviceConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 物联设备表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Service
public class IotDeviceRepositoryImpl extends ServiceImpl<IotDeviceMapper, IotDeviceDO> implements IIotDeviceRepository {


    /**
     * 根据设备编码获取设备
     * @param: iotDeviceCodes
     * @return java.util.List<com.chinatower.order.domain.IotDeviceDO>
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public List<IotDeviceDO> listIotDeviceByCodes(List<String> iotDeviceCodes) {
        List<IotDeviceDO> deviceInfoDOList = new ArrayList<>(0);
        if (iotDeviceCodes == null || iotDeviceCodes.isEmpty()) {
            return deviceInfoDOList;
        }
        QueryWrapper<IotDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("iot_device_id", "device_code", "app_mod_flag", "category_code");
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL).in("device_code", iotDeviceCodes);
        deviceInfoDOList = this.baseMapper.selectList(queryWrapper);
        return deviceInfoDOList;
    }

    @Override
    public List<IotDeviceDO> listIotDeviceBySet(Set<String> sensorCodeSet) {
        LambdaQueryWrapper<IotDeviceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(IotDeviceDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        lambdaQueryWrapper.in(IotDeviceDO::getDeviceCode, sensorCodeSet);
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 构建物联设备信息
     * @param: iotDeviceMsg
     * @param: sameDeviceInfoList
     * @return com.chinatower.order.domain.IotDeviceDO
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public IotDeviceDO buildIotDeviceDo(IotDeviceMsgDTO iotDeviceMsg, List<IotDeviceDO> sameDeviceInfoList) {
        IotDeviceDO sameDevice = this.getSameIotDeviceByCode(iotDeviceMsg.getDeviceCode(), iotDeviceMsg.getCategoryCode(), sameDeviceInfoList);
        if (sameDevice != null) {
            // 如果数据库已经存在该设备，则不处理
            return null;
        } else {
            IotDeviceDO iotDeviceDO = new IotDeviceDO();
            BeanUtils.copyProperties(iotDeviceMsg, iotDeviceDO);
            iotDeviceDO.setDeviceType(iotDeviceMsg.getDeviceType());
            iotDeviceDO.setAccessNode(iotDeviceMsg.getAccessNode());
            iotDeviceDO.setCreator("system");
            iotDeviceDO.setCreateTime(DateUtils.getNowDate());
            iotDeviceDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
            return iotDeviceDO;
        }
    }

    private IotDeviceDO getSameIotDeviceByCode(String deviceCode, Integer categoryCode, List<IotDeviceDO> deviceInfoList) {
        IotDeviceDO sameDevice = null;
        if (StringUtils.isNotBlank(deviceCode)) {
            for (IotDeviceDO deviceInfoDO : deviceInfoList) {
                if (deviceCode.equalsIgnoreCase(deviceInfoDO.getDeviceCode()) && categoryCode.equals(deviceInfoDO.getCategoryCode()) ) {
                    sameDevice = deviceInfoDO;
                    break;
                }
            }
        }
        return sameDevice;
    }
}

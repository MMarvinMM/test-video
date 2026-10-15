package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.order.domain.RecorderDeviceDO;
import com.chinatower.order.mapper.RecorderDeviceMapper;
import com.chinatower.order.repository.IRecorderDeviceRepository;
import com.chinatower.platform.constants.DeviceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 设备服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Slf4j
@Service
public class RecorderDeviceRepositoryImpl extends ServiceImpl<RecorderDeviceMapper, RecorderDeviceDO> implements IRecorderDeviceRepository {

    /**
     * 根据设备编码获取设备
     *
     * @param deviceCode
     * @return DeviceInfoDO
     * @author zheng.yan
     * @date 2022/1/24
     */
    @Override
    public RecorderDeviceDO getDeviceByCode(String deviceCode) {
        RecorderDeviceDO deviceInfoDO = null;
        if (StringUtils.isNotBlank(deviceCode)) {
            LambdaQueryWrapper<RecorderDeviceDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RecorderDeviceDO::getDeviceCode, deviceCode).eq(RecorderDeviceDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
            List<RecorderDeviceDO> deviceInfoDOs = this.baseMapper.selectList(queryWrapper);
            if (!deviceInfoDOs.isEmpty()) {
                deviceInfoDO = deviceInfoDOs.get(0);
            }
        }
        return deviceInfoDO;
    }

    @Override
    public List<RecorderDeviceDO> listDeviceInfoBySet(Set<String> deviceCodeSet) {
        LambdaQueryWrapper<RecorderDeviceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RecorderDeviceDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        lambdaQueryWrapper.in(RecorderDeviceDO::getDeviceCode, deviceCodeSet);
        return baseMapper.selectList(lambdaQueryWrapper);
    }

}

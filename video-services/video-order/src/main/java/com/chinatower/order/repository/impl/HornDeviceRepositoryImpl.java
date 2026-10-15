package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.order.domain.HornDeviceDO;
import com.chinatower.order.domain.IotDeviceDO;
import com.chinatower.order.mapper.HornDeviceMapper;
import com.chinatower.order.mapper.IotDeviceMapper;
import com.chinatower.order.repository.IHornDeviceRepository;
import com.chinatower.order.repository.IIotDeviceRepository;
import com.chinatower.platform.constants.DeviceConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
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
public class HornDeviceRepositoryImpl extends ServiceImpl<HornDeviceMapper, HornDeviceDO> implements IHornDeviceRepository {

    @Override
    public List<String> selectExistDeviceCodeByCodes(List<String> deviceCodeList) {
        if (CollectionUtils.isEmpty(deviceCodeList)) {
            return Lists.newArrayList();
        }
        return this.baseMapper.selectExistDeviceCodeByCodes(deviceCodeList);
    }

    @Override
    public List<HornDeviceDO> listDeviceInfoBySet(Set<String> deviceCodeSet) {
        LambdaQueryWrapper<HornDeviceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HornDeviceDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        lambdaQueryWrapper.in(HornDeviceDO::getDeviceCode, deviceCodeSet);
        return baseMapper.selectList(lambdaQueryWrapper);
    }
}

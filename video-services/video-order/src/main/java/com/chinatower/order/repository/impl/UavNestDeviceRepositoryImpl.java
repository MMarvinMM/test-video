package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.order.domain.UavNestDeviceDO;
import com.chinatower.order.mapper.UavNestDeviceMapper;
import com.chinatower.order.repository.IUavNestDeviceRepository;
import com.chinatower.platform.constants.DeviceConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class UavNestDeviceRepositoryImpl extends ServiceImpl<UavNestDeviceMapper, UavNestDeviceDO> implements IUavNestDeviceRepository {

    @Override
    public List<UavNestDeviceDO> listDeviceInfoBySet(Set<String> deviceCodeSet) {
        if (CollectionUtils.isEmpty(deviceCodeSet)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<UavNestDeviceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UavNestDeviceDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        lambdaQueryWrapper.in(UavNestDeviceDO::getDeviceCode, deviceCodeSet);
        return baseMapper.selectList(lambdaQueryWrapper);
    }

}

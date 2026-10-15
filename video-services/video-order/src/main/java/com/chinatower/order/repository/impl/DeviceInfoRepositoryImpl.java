package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.device.entity.dto.BasicDeviceDTO;
import com.chinatower.order.domain.DeviceInfoDO;
import com.chinatower.order.mapper.DeviceInfoMapper;
import com.chinatower.order.repository.IDeviceInfoRepository;
import com.chinatower.platform.constants.DeviceConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
public class DeviceInfoRepositoryImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoDO> implements IDeviceInfoRepository {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    private static final int BATCH_SIZE = 2000;

    /**
     * 查询该设备归属管理区域对应省市区
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    @Override
    public BasicDeviceDTO queryOrderDeviceAreaInfo(String areaCode) {
        return deviceInfoMapper.queryOrderDeviceAreaInfo(areaCode);
    }

    @Override
    public List<BasicDeviceDTO> queryOrderDeviceAreaInfoList(List<String> areaCodeList) {
        return deviceInfoMapper.queryOrderDeviceAreaInfoList(areaCodeList);
    }


    /**
     * 根据设备编码获取设备
     *
     * @param deviceCode
     * @return DeviceInfoDO
     * @author zheng.yan
     * @date 2022/1/24
     */
    @Override
    public DeviceInfoDO getDeviceByCode(String deviceCode) {
        DeviceInfoDO deviceInfoDO = null;
        if (StringUtils.isNotBlank(deviceCode)) {
            LambdaQueryWrapper<DeviceInfoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeviceInfoDO::getDeviceCode, deviceCode).eq(DeviceInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
            List<DeviceInfoDO> deviceInfoDOs = this.baseMapper.selectList(queryWrapper);
            if (!deviceInfoDOs.isEmpty()) {
                deviceInfoDO = deviceInfoDOs.get(0);
            }
        }
        return deviceInfoDO;
    }

    @Override
    public List<DeviceInfoDO> getDeviceByCodeList(List<String> deviceCodeList) {
        List<DeviceInfoDO> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(deviceCodeList)) {
            LambdaQueryWrapper<DeviceInfoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DeviceInfoDO::getDeviceCode, deviceCodeList).eq(DeviceInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
            resultList = this.baseMapper.selectList(queryWrapper);
        }
        return resultList;
    }

    @Override
    public Map<String, BasicDeviceDTO> queryByDeviceCodes(List<String> deviceCodeList) {
        Map<String, BasicDeviceDTO> resultMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(deviceCodeList)) {
            return resultMap;
        }
        if (deviceCodeList.size() <= BATCH_SIZE) {
            Map<String, BasicDeviceDTO> subResultMap = deviceInfoMapper.queryByDeviceCodes(deviceCodeList);
            if (MapUtils.isNotEmpty(subResultMap)) {
                resultMap.putAll(subResultMap);
            }
        }
        else {
            List<List<String>> parDeviceCodeList = Lists.partition(deviceCodeList, BATCH_SIZE);
            List<Map<String, BasicDeviceDTO>> resultList = ThreadParallelUtil.run(parDeviceCodeList, part ->
                            deviceInfoMapper.queryByDeviceCodes(part),
                    ThreadParallelUtil.ConfigFactory.ORDER_DEVICE_DEAL);
            for (Map<String, BasicDeviceDTO> subMap : resultList) {
                resultMap.putAll(subMap);
            }
        }
        return resultMap;
    }


}

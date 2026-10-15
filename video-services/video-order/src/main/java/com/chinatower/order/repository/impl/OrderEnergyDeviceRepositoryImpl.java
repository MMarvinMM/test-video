package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderEnergyDeviceDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.mapper.OrderEnergyDeviceMapper;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.order.repository.IOrderEnergyDeviceRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.system.api.model.DeviceTreeVO;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OrderEnergyDeviceRepositoryImpl extends ServiceImpl<OrderEnergyDeviceMapper, OrderEnergyDeviceDO>
        implements IOrderEnergyDeviceRepository {
    
    @Autowired
    private OrderEnergyDeviceMapper orderEnergyDeviceMapper;

    @Autowired
    private IOrderDeviceRepository iOrderDeviceRepository;

    @Override
    public List<OrderEnergyDeviceDO> getOrderDeviceByList(List<String> orderCodeList, List<String> packageCodeList) {
    
        QueryWrapper<OrderEnergyDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_code", orderCodeList);
        if (StringUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderEnergyDeviceMapper.selectList(queryWrapper);
    }
    
    @Override
    public void saveListBatch(List<OrderEnergyDeviceDO> list) {
        this.saveBatch(list);
    }
    
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderEnergyDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderEnergyDeviceMapper.delete(queryWrapper);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderEnergyDeviceMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public Map<String, List<DeviceTreeVO>> getTenantRecorderDeviceMap(String orderCode) {
        List<DeviceTreeVO> uavDeviceList = orderEnergyDeviceMapper.getTenantRecorderDeviceList(orderCode);
        Map<String, List<DeviceTreeVO>> deviceTreeVOMap = Maps.newHashMap();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(uavDeviceList)) {
            Map<String, List<DeviceTreeVO>> collect = uavDeviceList.stream().collect(Collectors.groupingBy(x -> x.getIndustryCode() + ":" + x.getAppVersion()));
            collect.forEach((x, y) -> {
                List<DeviceTreeVO> list = Lists.newArrayList();
                iOrderDeviceRepository.assembleDeviceTree(y, list);
                deviceTreeVOMap.put(x, list);
            });
        }
        return deviceTreeVOMap;
    }
}

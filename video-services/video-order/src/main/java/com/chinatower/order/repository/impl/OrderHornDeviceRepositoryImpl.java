package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderHornDeviceDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.mapper.OrderHornDeviceMapper;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.order.repository.IOrderHornDeviceRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.system.api.model.DeviceTreeVO;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OrderHornDeviceRepositoryImpl extends ServiceImpl<OrderHornDeviceMapper, OrderHornDeviceDO> implements IOrderHornDeviceRepository {
    
    @Autowired
    private OrderHornDeviceMapper orderHornDeviceMapper;

    @Autowired
    private IOrderDeviceRepository iOrderDeviceRepository;
    
    @Override
    public List<OrderHornDeviceDO> getOrderDeviceByList(List<String> orderCodeList, List<String> packageCodeList) {
    
        QueryWrapper<OrderHornDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_code", orderCodeList);
        if (StringUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderHornDeviceMapper.selectList(queryWrapper);
    }
    
    @Override
    public void saveListBatch(List<OrderHornDeviceDO> list) {
        this.saveBatch(list);
    }
    
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderHornDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderHornDeviceMapper.delete(queryWrapper);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderHornDeviceMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public Map<String, List<DeviceTreeVO>> getTenantHornDeviceMap(String orderCode) {
        List<DeviceTreeVO> hornDeviceList = orderHornDeviceMapper.getTenantHornDeviceList(orderCode);
        Map<String, List<DeviceTreeVO>> deviceTreeVOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(hornDeviceList)) {
            Map<String, List<DeviceTreeVO>> collect = hornDeviceList.stream().collect(Collectors.groupingBy(x -> x.getIndustryCode() + ":" + x.getAppVersion()));
            collect.forEach((x, y) -> {
                List<DeviceTreeVO> list = Lists.newArrayList();
                iOrderDeviceRepository.assembleDeviceTree(y, list);
                deviceTreeVOMap.put(x, list);
            });
        }
        return deviceTreeVOMap;
    }
}

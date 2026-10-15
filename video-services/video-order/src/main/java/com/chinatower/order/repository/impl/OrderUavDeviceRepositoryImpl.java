package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderUavDeviceDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.mapper.OrderUavDeviceMapper;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.order.repository.IOrderUavDeviceRepository;
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
public class OrderUavDeviceRepositoryImpl extends ServiceImpl<OrderUavDeviceMapper, OrderUavDeviceDO> implements IOrderUavDeviceRepository {
    
    @Autowired
    private OrderUavDeviceMapper orderUavDeviceMapper;

    @Autowired
    private IOrderDeviceRepository iOrderDeviceRepository;
    
    @Override
    public List<OrderUavDeviceDO> getOrderDeviceByList(List<String> orderCodeList, List<String> packageCodeList) {
    
        QueryWrapper<OrderUavDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_code", orderCodeList);
        if (StringUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderUavDeviceMapper.selectList(queryWrapper);
    }
    
    @Override
    public void saveListBatch(List<OrderUavDeviceDO> list) {
        this.saveBatch(list);
    }
    
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderUavDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderUavDeviceMapper.delete(queryWrapper);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderUavDeviceMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public Map<String, List<DeviceTreeVO>> getTenantUavDeviceMap(String orderCode) {
        List<DeviceTreeVO> uavDeviceList = orderUavDeviceMapper.getTenantUavDeviceList(orderCode);
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

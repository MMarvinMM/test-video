package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderSensorDeviceDO;
import com.chinatower.order.mapper.OrderSensorDeviceMapper;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.order.repository.IOrderSensorDeviceRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.system.api.model.DeviceTreeVO;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OrderSensorDeviceRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderSensorDeviceRepositoryImpl extends ServiceImpl<OrderSensorDeviceMapper, OrderSensorDeviceDO> implements IOrderSensorDeviceRepository {
    
    @Autowired
    private OrderSensorDeviceMapper orderSensorDeviceMapper;

    @Autowired
    private IOrderDeviceRepository iOrderDeviceRepository;
    
    /**
     * 功能描述 根据订单编码查询订购设备信息列表
     * @author chenmf
     * @date 2022/1/10
     * [orderCode, packageCode]
     * @return
     */
    @Override
    public List<OrderSensorDeviceDO> getOrderDeviceByList(List<String> orderCodeList, List<String> packageCodeList) {
    
        QueryWrapper<OrderSensorDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_code", orderCodeList);
        if (CollectionUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderSensorDeviceMapper.selectList(queryWrapper);
    }
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceDOList]
     * @return
     */
    @Override
    public void saveListBatch(List<OrderSensorDeviceDO> orderSensorDeviceDOList) {
        this.saveBatch(orderSensorDeviceDOList);
    }
    
    /**
     * 功能描述 删除订单数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderSensorDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderSensorDeviceMapper.delete(queryWrapper);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderSensorDeviceMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public Map<String, List<DeviceTreeVO>> getTenantSensorDeviceMap(String orderCode,Integer categoryCode) {
        List<DeviceTreeVO> sensorDevices = orderSensorDeviceMapper.getTenantSensorDeviceList(orderCode, categoryCode);
        Map<String, List<DeviceTreeVO>> deviceTreeVOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(sensorDevices)) {
            Map<String, List<DeviceTreeVO>> collect = sensorDevices.stream().collect(Collectors.groupingBy(x -> x.getIndustryCode() + ":" + x.getAppVersion()));
            collect.forEach((x, y) -> {
                List<DeviceTreeVO> list = Lists.newArrayList();
                iOrderDeviceRepository.assembleDeviceTree(y, list);
                deviceTreeVOMap.put(x, list);
            });
        }
        return deviceTreeVOMap;
    }

    @Override
    public List<OrderDevicesVO> getOrderSensorDeviceByOrderCodeAndPackageCode(QueryOrderRelationshipVO req) {
        return orderSensorDeviceMapper.getOrderSensorDeviceByOrderCodeAndPackageCode(req);
    }

    @Override
    public List<OrderSensorDeviceDO> getOrderRadarDeviceByOrderCodesAndPackageCodes(List<String> orderCodeList, List<String> packageCodeList) {
        return orderSensorDeviceMapper.getOrderRadarDeviceByOrderCodesAndPackageCodes(orderCodeList, packageCodeList);
    }
}

package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderUavDeviceHisDO;
import com.chinatower.order.mapper.OrderUavDeviceHisMapper;
import com.chinatower.order.repository.IOrderUavDeviceHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderUavDeviceHisRepositoryImpl extends ServiceImpl<OrderUavDeviceHisMapper, OrderUavDeviceHisDO> implements IOrderUavDeviceHisRepository {
    
    @Autowired
    private OrderUavDeviceHisMapper orderUavDeviceHisMapper;
    
    @Override
    public void saveListBatch(List<OrderUavDeviceHisDO> orderSensorDeviceHisList) {
        this.saveBatch(orderSensorDeviceHisList);
    }
    
    @Override
    public List<OrderUavDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
        QueryWrapper<OrderUavDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderUavDeviceHisMapper.selectList(queryWrapper);
    }
    
}

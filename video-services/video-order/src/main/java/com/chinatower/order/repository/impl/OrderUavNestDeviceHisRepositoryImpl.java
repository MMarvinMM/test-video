package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderUavNestDeviceHisDO;
import com.chinatower.order.mapper.OrderUavNestDeviceHisMapper;
import com.chinatower.order.repository.IOrderUavNestDeviceHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderUavNestDeviceHisRepositoryImpl extends ServiceImpl<OrderUavNestDeviceHisMapper, OrderUavNestDeviceHisDO> implements IOrderUavNestDeviceHisRepository {
    
    @Autowired
    private OrderUavNestDeviceHisMapper orderUavNestDeviceHisMapper;
    
    @Override
    public void saveListBatch(List<OrderUavNestDeviceHisDO> orderSensorDeviceHisList) {
        this.saveBatch(orderSensorDeviceHisList);
    }
    
    @Override
    public List<OrderUavNestDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
        QueryWrapper<OrderUavNestDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderUavNestDeviceHisMapper.selectList(queryWrapper);
    }
    
}

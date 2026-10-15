package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderHornDeviceHisDO;
import com.chinatower.order.mapper.OrderHornDeviceHisMapper;
import com.chinatower.order.repository.IOrderHornDeviceHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderHornDeviceHisRepositoryImpl extends ServiceImpl<OrderHornDeviceHisMapper, OrderHornDeviceHisDO> implements IOrderHornDeviceHisRepository {
    
    @Autowired
    private OrderHornDeviceHisMapper orderHornDeviceHisMapper;
    
    @Override
    public void saveListBatch(List<OrderHornDeviceHisDO> list) {
        this.saveBatch(list);
    }
    
    @Override
    public List<OrderHornDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
    
        QueryWrapper<OrderHornDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderHornDeviceHisMapper.selectList(queryWrapper);
    }
    
}

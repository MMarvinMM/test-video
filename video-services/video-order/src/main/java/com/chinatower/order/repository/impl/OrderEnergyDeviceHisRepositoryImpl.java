package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderEnergyDeviceHisDO;
import com.chinatower.order.mapper.OrderEnergyDeviceHisMapper;
import com.chinatower.order.repository.IOrderEnergyDeviceHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderEnergyDeviceHisRepositoryImpl extends ServiceImpl<OrderEnergyDeviceHisMapper, OrderEnergyDeviceHisDO>
        implements IOrderEnergyDeviceHisRepository {
    
    @Autowired
    private OrderEnergyDeviceHisMapper orderEnergyDeviceHisMapper;
    
    @Override
    public void saveListBatch(List<OrderEnergyDeviceHisDO> list) {
        this.saveBatch(list);
    }
    
    @Override
    public List<OrderEnergyDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
    
        QueryWrapper<OrderEnergyDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderEnergyDeviceHisMapper.selectList(queryWrapper);
    }
    
}

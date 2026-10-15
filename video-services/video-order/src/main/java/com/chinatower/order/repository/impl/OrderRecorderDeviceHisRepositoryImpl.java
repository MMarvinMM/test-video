package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderRecorderDeviceHisDO;
import com.chinatower.order.mapper.OrderRecorderDeviceHisMapper;
import com.chinatower.order.repository.IOrderRecorderDeviceHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRecorderDeviceHisRepositoryImpl extends ServiceImpl<OrderRecorderDeviceHisMapper, OrderRecorderDeviceHisDO>
        implements IOrderRecorderDeviceHisRepository {
    
    @Autowired
    private OrderRecorderDeviceHisMapper orderRecorderDeviceHisMapper;
    
    @Override
    public void saveListBatch(List<OrderRecorderDeviceHisDO> list) {
        this.saveBatch(list);
    }
    
    @Override
    public List<OrderRecorderDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
    
        QueryWrapper<OrderRecorderDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderRecorderDeviceHisMapper.selectList(queryWrapper);
    }
    
}

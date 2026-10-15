package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderSensorDeviceHisDO;
import com.chinatower.order.mapper.OrderSensorDeviceHisMapper;
import com.chinatower.order.repository.IOrderSensorDeviceHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderSensorDeviceHisRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderSensorDeviceHisRepositoryImpl extends ServiceImpl<OrderSensorDeviceHisMapper, OrderSensorDeviceHisDO> implements IOrderSensorDeviceHisRepository {
    
    @Autowired
    private OrderSensorDeviceHisMapper orderSensorDeviceHisMapper;
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceHisList]
     * @return
     */
    @Override
    public void saveListBatch(List<OrderSensorDeviceHisDO> orderSensorDeviceHisList) {
        this.saveBatch(orderSensorDeviceHisList);
    }
    
    /**
     * 功能描述 根据订单编码和迁移标识获取迁移数据
     * @author chenmf
     * @date 2022/5/26
     * [orderCode, moveSign]
     * @return
     */
    @Override
    public List<OrderSensorDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
    
        QueryWrapper<OrderSensorDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderSensorDeviceHisMapper.selectList(queryWrapper);
    }
    
}

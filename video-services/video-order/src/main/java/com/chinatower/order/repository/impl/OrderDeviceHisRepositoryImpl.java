package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.mapper.OrderDeviceHisMapper;
import com.chinatower.order.repository.IOrderDeviceHisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderDeviceHisRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderDeviceHisRepositoryImpl extends ServiceImpl<OrderDeviceHisMapper, OrderDeviceHisDO> implements IOrderDeviceHisRepository {
    
    @Autowired
    private OrderDeviceHisMapper orderDeviceHisMapper;
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceHisList]
     * @return
     */
    @Override
    public void saveListBatch(List<OrderDeviceHisDO> orderDeviceHisList) {
        this.saveBatch(orderDeviceHisList, 5000);
    }

    @Override
    public int batchInsert(List<OrderDeviceHisDO> orderDeviceDOList) {
        return orderDeviceHisMapper.batchInsert(orderDeviceDOList);
    }
    
    /**
     * 功能描述 根据订单编码和迁移标识获取迁移数据
     * @author chenmf
     * @date 2022/1/11
     * [orderCode, moveSign]
     * @return
     */
    @Override
    public List<OrderDeviceHisDO> getOldOrderDeviceByOrderCode(String orderCode, int moveSign) {
    
        QueryWrapper<OrderDeviceHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderDeviceHisMapper.selectList(queryWrapper);
    }
}

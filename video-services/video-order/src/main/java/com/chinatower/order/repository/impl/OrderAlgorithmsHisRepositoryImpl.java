package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderAlgorithmsHisDO;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.mapper.OrderAlgorithmsHisMapper;
import com.chinatower.order.repository.IOrderAlgorithmsHisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderAlgorithmsHisRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderAlgorithmsHisRepositoryImpl extends ServiceImpl<OrderAlgorithmsHisMapper, OrderAlgorithmsHisDO> implements IOrderAlgorithmsHisRepository {
    
    @Autowired
    private OrderAlgorithmsHisMapper orderAlgorithmsHisMapper;
    
    /**
     * 功能描述 比例保存
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageList]
     * @return
     */
    @Override
    public void saveListBatch(List<OrderAlgorithmsHisDO> orderPackageList) {
    
        this.saveBatch(orderPackageList, 5000);
    }
    
    /**
     * 功能描述
     * @author chenmf
     * @date 2022/1/11
     * [orderCode, moveSign]
     * @return
     */
    @Override
    public List<OrderAlgorithmsHisDO> getOrderAlgorithmsByOrderCode(String orderCode, int moveSign) {
        QueryWrapper<OrderAlgorithmsHisDO> queryWrapper = new QueryWrapper<>();
    
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderAlgorithmsHisMapper.selectList(queryWrapper);
    }
}

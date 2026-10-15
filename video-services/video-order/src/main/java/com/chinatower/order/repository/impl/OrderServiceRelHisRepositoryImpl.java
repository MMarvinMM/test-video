package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderServiceRelHisDO;
import com.chinatower.order.mapper.OrderServiceRelHisMapper;
import com.chinatower.order.repository.IOrderServiceRelHisRepository;
import com.chinatower.platform.constants.OrderConstants;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderServiceRelHisRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderServiceRelHisRepositoryImpl extends ServiceImpl<OrderServiceRelHisMapper, OrderServiceRelHisDO> implements IOrderServiceRelHisRepository {

    /**
     * 功能描述
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderServicDOList]
     */
    @Override
    public void saveListBatch(List<OrderServiceRelHisDO> orderServiceRelHisDOList) {
        this.saveBatch(orderServiceRelHisDOList);
    }

    @Override
    public List<OrderServiceRelHisDO> getOldOrderServiceRelHisByOrderCode(String orderCode, int moveSign) {
        QueryWrapper<OrderServiceRelHisDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("move_sign", moveSign);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return this.baseMapper.selectList(queryWrapper);
    }
}

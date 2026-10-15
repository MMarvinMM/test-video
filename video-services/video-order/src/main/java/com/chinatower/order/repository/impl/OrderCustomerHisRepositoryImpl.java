package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderCustomerHisDO;
import com.chinatower.order.mapper.OrderCustomerHisMapper;
import com.chinatower.order.repository.IOrderCustomerHisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderCustomerHisRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderCustomerHisRepositoryImpl extends ServiceImpl<OrderCustomerHisMapper, OrderCustomerHisDO> implements IOrderCustomerHisRepository {

    @Autowired
    private OrderCustomerHisMapper orderCustomerHisMapper;

    /**
     * 功能描述 保存数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCustomerHisDO]
     * @return
     */
    @Override
    public void saveCustomer(List<OrderCustomerHisDO> orderCustomerHisDOList) {
        this.saveBatch(orderCustomerHisDOList);
    }
}

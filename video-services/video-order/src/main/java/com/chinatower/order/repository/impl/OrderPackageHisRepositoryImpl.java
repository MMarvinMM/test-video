package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderPackageHisDO;
import com.chinatower.order.mapper.OrderPackageHisMapper;
import com.chinatower.order.repository.IOrderPackageHisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderPackageHisRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderPackageHisRepositoryImpl extends ServiceImpl<OrderPackageHisMapper, OrderPackageHisDO> implements IOrderPackageHisRepository {

    @Autowired
    private OrderPackageHisMapper orderPackageHisMapper;

    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageHisDOList]
     * @return
     */
    @Override
    public void saveListBatch(List<OrderPackageHisDO> orderPackageHisDOList) {
        this.saveBatch(orderPackageHisDOList);
    }
}

package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderServiceRelDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.mapper.OrderServiceRelMapper;
import com.chinatower.order.repository.IOrderServiceRelRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.order.OrderServiceRelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderServiceRelRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderServiceRelRepositoryImpl extends ServiceImpl<OrderServiceRelMapper, OrderServiceRelDO> implements IOrderServiceRelRepository {

    @Autowired
    private OrderServiceRelMapper orderServiceRelMapper;

    /**
     * 功能描述
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode, packageCode]
     */
    @Override
    public List<OrderServiceRelDO> getOrderServiceRelByList(List<String> orderCodeList, List<String> packageCodeList) {
        QueryWrapper<OrderServiceRelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_code", orderCodeList);
        if (StringUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderServiceRelMapper.selectList(queryWrapper);
    }

    /**
     * 功能描述
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     */
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderServiceRelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderServiceRelMapper.delete(queryWrapper);
    }

    /**
     * 功能描述
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderServicDOList]
     */
    @Override
    public void saveListBatch(List<OrderServiceRelDO> orderServiceRelDOList) {
        this.saveBatch(orderServiceRelDOList);
    }

    /**
     * 获取省份地址信息
     * @param areaCodes
     * @return
     */
    @Override
    public List<OrderServiceRelVO> getAreaInfo(List<String> areaCodes) {
        return orderServiceRelMapper.getAreaInfo(areaCodes);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderServiceRelMapper.cancelByOrderCode(orderCode);
    }
}

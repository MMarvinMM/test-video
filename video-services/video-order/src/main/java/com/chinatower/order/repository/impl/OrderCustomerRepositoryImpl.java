package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderCustomerDO;
import com.chinatower.order.mapper.OrderCustomerMapper;
import com.chinatower.order.repository.IOrderCustomerRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.order.OrderInfoVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.google.common.base.Splitter;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @ClassName OrderCustomerRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderCustomerRepositoryImpl extends ServiceImpl<OrderCustomerMapper, OrderCustomerDO> implements IOrderCustomerRepository {

    @Autowired
    private OrderCustomerMapper orderCustomerMapper;


    /**
     * 功能描述 根据订单编码查询订购用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     */
    @Override
    public List<OrderCustomerDO> getOrderCustomerByOrderCode(String orderCode) {

        QueryWrapper<OrderCustomerDO> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("order_code", orderCode);
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        queryWrapper.orderByDesc("update_time");
        return orderCustomerMapper.selectList(queryWrapper);
    }

    /**
     * 功能描述 根据id删除数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [id]
     */
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderCustomerDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderCustomerMapper.delete(queryWrapper);
    }
    /**
     * 功能描述 保存数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/11
     * [orderCustomerDO]
     */
    @Override
    public void saveOrderCustomer(OrderCustomerDO orderCustomerDO) {
        orderCustomerMapper.insert(orderCustomerDO);
    }

    /**
     * 根据查询条件查询订购列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/18
     * @version
     */
    @Override
    public List<OrderInfoVO> getOrderCustomerByParam(QueryOrderRelationshipVO queryOrderRelationshipVO) {
        return orderCustomerMapper.getOrderCustomerByParam(queryOrderRelationshipVO);
    }

    /**
     * 验证行业编码和行业版本编码是否有效
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    @Override
    public boolean validAppCode(String appCode, String appVerCode, String acceptNode) {
        int count = orderCustomerMapper.validAppCode(appCode, appVerCode, acceptNode);
        return count > 0;
    }

    @Override
    public boolean validLocationAppCode(String appCode, String appVerCode, String acceptNode) {
        return orderCustomerMapper.validLocationAppCode(appCode, appVerCode, acceptNode) > 0;
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderCustomerMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public int countOrder(String tenantId) {
        return orderCustomerMapper.countOrder(tenantId);
    }

    @Override
    public List<String> getParentCodeListByLocationCode(String locationCode) {
        String parentCodeStr = orderCustomerMapper.getParentCodeListByLocationCode(locationCode);
        if (StringUtils.isEmpty(parentCodeStr)) {
            return Lists.newArrayList();
        }
        List<String> parentCodeList = Splitter.on(",").splitToList(parentCodeStr);
        return orderCustomerMapper.getLocationCodeWithoutCenterCode(parentCodeList);
    }

    @Override
    public List<String> queryValidAccessNodeList(String acceptNode) {
        return orderCustomerMapper.queryValidAccessNodeList(acceptNode);
    }
}

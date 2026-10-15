package com.chinatower.order.repository;


import com.chinatower.common.core.domain.OrderCustomerDO;
import com.chinatower.platform.entity.order.OrderInfoVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;

import java.util.List;

/**
 * @ClassName IOrderCustomerRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderCustomerRepository {

    /**
     * 功能描述 根据订单编码查询订购用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     */
    List<OrderCustomerDO> getOrderCustomerByOrderCode(String orderCode);

    /**
     * 功能描述 根据id删除数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [id]
     */
    void deleteByOrderCode(String orderCode);

    /**
     * 功能描述 保存数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/11
     * [orderCustomerDO]
     */
    void saveOrderCustomer(OrderCustomerDO orderCustomerDO);

    /**
     * 根据查询条件查询订购列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/18
     * @version
     */
    List<OrderInfoVO> getOrderCustomerByParam(QueryOrderRelationshipVO queryOrderRelationshipVO);

    /**
     * 验证行业编码和行业版本编码是否有效
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    boolean validAppCode(String appCode, String appVerCode, String acceptNode);

    /**
     * 校验属地版本行业编码和行业版本是否有效
     */
    boolean validLocationAppCode(String appCode, String appVerCode, String acceptNode);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);

    int countOrder(String tenantId);

    /**
     * 查询非中心节点的父节点列表
     * @param locationCode 节点编码
     * @return 非中心节点的父节点列表
     */
    List<String> getParentCodeListByLocationCode(String locationCode);

    /**
     * 根据受理节点查询有效的接入节点信息
     * @param acceptNode 受理节点
     * @return 有效接入节点信息
     */
    List<String> queryValidAccessNodeList(String acceptNode);
}

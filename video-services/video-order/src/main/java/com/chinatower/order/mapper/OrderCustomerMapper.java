package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderCustomerDO;
import com.chinatower.platform.entity.order.OrderInfoVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderCustomerMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderCustomerMapper extends BaseMapper<OrderCustomerDO> {

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
    int validAppCode(@Param("appCode")String appCode, @Param("appVerCode")String appVerCode, @Param("acceptNode") String acceptNode);

    int validLocationAppCode(@Param("appCode")String appCode, @Param("appVerCode")String appVerCode, @Param("acceptNode") String acceptNode);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode")String orderCode);

    int countOrder(@Param("tenantId")String tenantId);

    /**
     * 根据节点查询所有父节点
     * @param locationCode 属地化节点
     * @return 父节点列表
     */
    String getParentCodeListByLocationCode(@Param("locationCode")String locationCode);

    /**
     * 根据节点编码查询节点级别
     * @param locationCode 节点编码
     * @return 节点级别
     */
    String getLocationLevelByCode(@Param("locationCode")String locationCode);

    /**
     * 根据节点列表查询非中心节点的节点列表
     * @param locationCodeList 节点列表
     * @return 非中心节点的节点列表
     */
    List<String> getLocationCodeWithoutCenterCode(@Param("locationCodeList")List<String> locationCodeList);

    /**
     * 根据用户名查询订单数量
     * @param userName 用户名
     * @param currentAcceptNode 当前节点编码
     * @return 订单数量
     */
    int countByUserName(@Param("userName") String userName, @Param("currentAcceptNode") String currentAcceptNode);

    /**
     * 根据受理节点查询有效的接入节点信息
     * @param acceptNode 受理节点
     * @return 有效接入节点信息
     */
    List<String> queryValidAccessNodeList(@Param("acceptNode")String acceptNode);
}

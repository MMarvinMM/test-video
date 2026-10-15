package com.chinatower.algorithm.mapper;

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
    int validAppCode(@Param("appCode")String appCode, @Param("appVerCode")String appVerCode);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode")String orderCode);
}

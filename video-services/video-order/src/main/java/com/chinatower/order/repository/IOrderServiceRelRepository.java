package com.chinatower.order.repository;

import com.chinatower.common.core.domain.OrderServiceRelDO;
import com.chinatower.platform.entity.order.OrderServiceRelVO;

import java.util.List;

public interface IOrderServiceRelRepository {
    /**
     * 功能描述
     * @author chenmf
     * @date 2022/1/10
     * [orderCodeList, packageCodeList]
     * @return
     */
    List<OrderServiceRelDO> getOrderServiceRelByList(List<String> orderCodeList, List<String> packageCodeList);

    /**
     * 功能描述
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    void deleteByOrderCode(String orderCode);

    /**
     * 功能描述
     * @author chenmf
     * @date 2022/1/10
     * [orderServicDOList]
     * @return
     */
    void saveListBatch(List<OrderServiceRelDO> orderServiceRelDOList);

    List<OrderServiceRelVO> getAreaInfo(List<String> areaCodes);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);
}

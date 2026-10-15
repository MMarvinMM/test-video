package com.chinatower.order.repository;




import com.chinatower.common.core.domain.OrderCustomerHisDO;

import java.util.List;

/**
 * @ClassName IOrderCustomerHisRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderCustomerHisRepository {

    /**
     * 功能描述 保存数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCustomerHisDO]
     * @return
     */
    void saveCustomer(List<OrderCustomerHisDO> orderCustomerHisDOList);
}

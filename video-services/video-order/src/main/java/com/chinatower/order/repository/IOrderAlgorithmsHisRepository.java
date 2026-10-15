package com.chinatower.order.repository;




import com.chinatower.common.core.domain.OrderAlgorithmsHisDO;

import java.util.List;

/**
 * @ClassName IOrderAlgorithmsHisRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderAlgorithmsHisRepository {
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageList]
     * @return
     */
    void saveListBatch(List<OrderAlgorithmsHisDO> orderPackageList);
    
    /**
     * 功能描述
     * @author chenmf
     * @date 2022/1/11
     * [orderCode, moveSign]
     * @return
     */
    List<OrderAlgorithmsHisDO> getOrderAlgorithmsByOrderCode(String orderCode, int moveSign);
}

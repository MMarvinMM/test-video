package com.chinatower.order.repository;



import com.chinatower.common.core.domain.OrderPackageHisDO;

import java.util.List;

/**
 * @ClassName IOrderPackageHisRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderPackageHisRepository {

    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageHisDOList]
     * @return
     */
    void saveListBatch(List<OrderPackageHisDO> orderPackageHisDOList);

}

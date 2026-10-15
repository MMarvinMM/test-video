package com.chinatower.event.repository;

import com.chinatower.event.vo.*;
import java.util.List;

/**
 * 客户订购套餐相关表操作
 * @author guyuefei
 * @date 2022-05-12 09:58:34
 * @version v1.1
 */
public interface IOrderPackageRepository {

    /**
     * 通过版本编码获取客户信息
     * @param productCode
     * @return
     */
    List<OrderCustomerVO> getCustomersByProductCode(String productCode);

    List<OrderCustomerVO> getCustomersByOrderAreaCode(ReceiveSatelliteAlarmVo receiveSatelliteAlarmVo);
}

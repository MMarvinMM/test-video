package com.chinatower.event.mapper;

import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.vo.ReceiveSatelliteAlarmVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户订购套餐相关表操作
 * @author guyuefei
 * @date 2022/2/28 09:33
 * @version v1.1
 */
@Mapper
public interface OrderPackageMapper {

    /**
     * 通过版本编码获取客户信息
     * @param productCode
     * @return
     */
    List<OrderCustomerVO> getCustomersByProductCode(@Param("productCode") String productCode);

    List<OrderCustomerVO> getCustomersByOrderAreaCode(ReceiveSatelliteAlarmVo receiveSatelliteAlarmVo);
}

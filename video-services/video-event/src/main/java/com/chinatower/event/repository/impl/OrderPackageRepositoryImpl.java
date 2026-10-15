package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.mapper.OrderPackageMapper;
import com.chinatower.event.repository.IOrderPackageRepository;
import com.chinatower.event.vo.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 客户订购套餐相关表操作
 * @author guyuefei
 * @date 2022-05-12 09:59:27
 * @version v1.1
 */
@Slf4j
@Service("orderPackageRepository")
@DS(EbikeDataBaseConstant.BASE)
public class OrderPackageRepositoryImpl implements IOrderPackageRepository {

    @Autowired
    private OrderPackageMapper orderPackageMapper;

    /**
     * 通过版本编码获取客户信息
     * @param productCode
     * @return
     */
    @Override
    public List<OrderCustomerVO> getCustomersByProductCode(String productCode) {
        List<OrderCustomerVO> customersByProductCode = orderPackageMapper.getCustomersByProductCode(productCode);
        if(! Collections.isEmpty(customersByProductCode)) {
            return customersByProductCode.stream().filter(customer -> Objects.nonNull(customer)).collect(Collectors.toList());
        }
        return java.util.Collections.EMPTY_LIST;
    }

    @Override
    public List<OrderCustomerVO> getCustomersByOrderAreaCode(ReceiveSatelliteAlarmVo receiveSatelliteAlarmVo) {
        List<OrderCustomerVO> customersByOrderAreaCode = orderPackageMapper.getCustomersByOrderAreaCode(receiveSatelliteAlarmVo);
        if(! Collections.isEmpty(customersByOrderAreaCode)) {
            return customersByOrderAreaCode.stream().filter(customer -> Objects.nonNull(customer)).collect(Collectors.toList());
        }
        return java.util.Collections.EMPTY_LIST;
    }
}

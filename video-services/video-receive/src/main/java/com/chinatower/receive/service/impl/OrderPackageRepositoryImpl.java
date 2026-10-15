package com.chinatower.receive.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.mapper.OrderPackageMapper;
import com.chinatower.receive.service.IOrderPackageRepository;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:28
 */

@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class OrderPackageRepositoryImpl implements IOrderPackageRepository {

    @Autowired
    private OrderPackageMapper orderPackageMapper;
    @Override
    public List<OrderCustomerVO> getCustomersByOrderAreaCode(ReceiveSatelliteAlarmApiVo receiveSatelliteAlarmVo) {
        List<OrderCustomerVO> customersByOrderAreaCode = orderPackageMapper.getCustomersByOrderAreaCode(receiveSatelliteAlarmVo);
        if(! Collections.isEmpty(customersByOrderAreaCode)) {
            return customersByOrderAreaCode.stream().filter(customer -> Objects.nonNull(customer)).collect(Collectors.toList());
        }
        return java.util.Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getPlatformCodeByAre(String province, String city, String county,List<String> nodes) {
        return orderPackageMapper.getPlatformCodeByAre(province,city,county,nodes);
    }
}

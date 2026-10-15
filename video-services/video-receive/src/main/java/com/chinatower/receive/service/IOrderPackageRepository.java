package com.chinatower.receive.service;

import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.receive.vo.dto.OrderCustomerVO;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:27
 */
public interface IOrderPackageRepository {

    List<OrderCustomerVO> getCustomersByOrderAreaCode(ReceiveSatelliteAlarmApiVo receiveSatelliteAlarmVo);

    List<String> getPlatformCodeByAre(String province,String city,String county,List<String> nodes);
}

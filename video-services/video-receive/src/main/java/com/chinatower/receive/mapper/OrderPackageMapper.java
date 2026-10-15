package com.chinatower.receive.mapper;

import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:25
 */

@Mapper
public interface OrderPackageMapper {

    List<OrderCustomerVO> getCustomersByOrderAreaCode(ReceiveSatelliteAlarmApiVo receiveSatelliteAlarmVo);

    List<String> getPlatformCodeByAre(@Param("province") String province, @Param("city") String city, @Param("county") String county,@Param("list") List<String> nodes);

    List<String> getSatelliteAcceptNodeByAre(ReceiveSatelliteAlarmApiVo receiveSatelliteAlarmVo);
}

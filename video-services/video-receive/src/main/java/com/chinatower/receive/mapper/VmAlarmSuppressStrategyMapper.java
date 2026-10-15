package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:50
 */

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmSuppressStrategyMapper {

    /**
     * 查询告警压制策略ID
     * @param receiveAlarmVO
     * @param customers
     * @return
     */
    List<Integer> selectSuppressStrategy(@Param("req") ReceiveAlarmVO receiveAlarmVO, @Param("customers") List<OrderCustomerVO> customers);

    List<VmAlarmSuppressStrategyVO> selectList(VmAlarmSuppressStrategyVO suppressVO);
}

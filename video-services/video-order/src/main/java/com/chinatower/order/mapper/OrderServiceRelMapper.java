package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderServiceRelDO;
import com.chinatower.platform.entity.order.OrderServiceRelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderServiceMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderServiceRelMapper extends BaseMapper<OrderServiceRelDO> {
    List<OrderServiceRelVO> getAreaInfo(List<String> areaCodes);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode") String orderCode);
}

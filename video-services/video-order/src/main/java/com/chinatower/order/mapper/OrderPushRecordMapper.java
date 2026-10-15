package com.chinatower.order.mapper;

import com.chinatower.common.core.domain.OrderPushRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface OrderPushRecordMapper {

    /**
     * 批量插入
     * @param list 信息
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<OrderPushRecordDO> list);

    /**
     * 根据id更新订单推送信息
     * @param param 推送信息
     * @return 影响行数
     */
    int updateById(@Param("param") OrderPushRecordDO param);

    /**
     * 根据订单编码查询订单推送信息
     * @param orderCode 订单编码
     * @return 订单推送信息
     */
    List<OrderPushRecordDO> selectByOrderCode(@Param("orderCode") String orderCode);
}
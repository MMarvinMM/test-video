package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警推送任务：按事件ID查询事件详情
 */
@Mapper
public interface AlarmPushDAO {

    /**
     * 按事件ID批量查询告警详情
     *
     * @param orderIds  事件ID列表
     * @param tableName 分表名
     * @return 事件详情
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<OrderInfoVO> getOrderInfoByIds(@Param("orderIds") List<String> orderIds, @Param("tableName") String tableName);
}

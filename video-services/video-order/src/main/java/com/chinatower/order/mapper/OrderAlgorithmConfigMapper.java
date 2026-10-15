package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.chinatower.platform.entity.order.OrderAlgorithmConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderAlgorithmConfigMapper extends BaseMapper<OrderAlgorithmConfigDO> {

    void batchInsert(@Param("list") List<OrderAlgorithmConfigDO> list);

    List<OrderAlgorithmConfigDO> selectByOrderCode(@Param("orderCode") String orderCode);

    List<OrderAlgorithmConfigVO> selectByDeviceCodeListAndTypeAndOrderCode(
            @Param("deviceCodeList") List<String> deviceCodeList,
            @Param("type") Integer type,
            @Param("orderCode") String orderCode);

    List<OrderAlgorithmConfigVO> selectAlgorithmInfoByOrderCodeAndAppScenario(
            @Param("orderCode") String orderCode,
            @Param("appScenario") String appScenario);
}

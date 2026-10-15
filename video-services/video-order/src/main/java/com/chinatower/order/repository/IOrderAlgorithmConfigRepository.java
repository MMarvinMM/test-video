package com.chinatower.order.repository;

import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.chinatower.platform.entity.order.OrderAlgorithmConfigVO;

import java.util.List;

public interface IOrderAlgorithmConfigRepository {

    void saveBatch(List<OrderAlgorithmConfigDO> list);

    List<OrderAlgorithmConfigDO> selectByOrderCode(String orderCode);

    void removeByIds(List<Long> ids);

    List<OrderAlgorithmConfigVO> selectByDeviceCodeListAndTypeAndOrderCode(
            List<String> deviceCodeList, Integer type, String orderCode);

    List<OrderAlgorithmConfigVO> selectAlgorithmInfoByOrderCodeAndAppScenario(
            String orderCode, String appScenario);
}

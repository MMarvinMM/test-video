package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.chinatower.order.mapper.OrderAlgorithmConfigMapper;
import com.chinatower.order.repository.IOrderAlgorithmConfigRepository;
import com.chinatower.platform.entity.order.OrderAlgorithmConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderAlgorithmConfigRepositoryImpl extends ServiceImpl<OrderAlgorithmConfigMapper, OrderAlgorithmConfigDO>
        implements IOrderAlgorithmConfigRepository {

    @Autowired
    private OrderAlgorithmConfigMapper orderAlgorithmConfigMapper;

    @Override
    public void saveBatch(List<OrderAlgorithmConfigDO> list) {
        this.saveBatch(list, 5000);
    }

    @Override
    public List<OrderAlgorithmConfigDO> selectByOrderCode(String orderCode) {
        QueryWrapper<OrderAlgorithmConfigDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        return orderAlgorithmConfigMapper.selectList(queryWrapper);
    }

    @Override
    public void removeByIds(List<Long> ids) {
        super.removeByIds(ids);
    }

    @Override
    public List<OrderAlgorithmConfigVO> selectByDeviceCodeListAndTypeAndOrderCode(
            List<String> deviceCodeList, Integer type, String orderCode) {
        return orderAlgorithmConfigMapper.selectByDeviceCodeListAndTypeAndOrderCode(deviceCodeList, type, orderCode);
    }

    @Override
    public List<OrderAlgorithmConfigVO> selectAlgorithmInfoByOrderCodeAndAppScenario(
            String orderCode, String appScenario) {
        return orderAlgorithmConfigMapper.selectAlgorithmInfoByOrderCodeAndAppScenario(orderCode, appScenario);
    }
}

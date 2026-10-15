package com.chinatower.receive.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.mapper.AlarmSuppressConfigMapper;
import com.chinatower.receive.service.IAlarmSuppressConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 14:13
 */
@Service
public class IAlarmSuppressConfigRepositoryImpl implements IAlarmSuppressConfigRepository {

    @Autowired
    private AlarmSuppressConfigMapper alarmSuppressConfigMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public List<AlarmSuppressConfigDTO> querySuppressConfigByTenantIds(List<String> tenantIdList) {
        return alarmSuppressConfigMapper.querySuppressConfigByTenantIds(tenantIdList);
    }
}

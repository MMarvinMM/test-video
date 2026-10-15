package com.chinatower.receive.service;

import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 14:12
 */
public interface IAlarmSuppressConfigRepository {

    /**
     * 根据租户id获取告警压制规则
     * @param tenantIdList
     * @return
     */
    List<AlarmSuppressConfigDTO> querySuppressConfigByTenantIds(List<String> tenantIdList);
}

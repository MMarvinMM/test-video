package com.chinatower.event.common.service;

import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorDto;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/28 17:20
 */
public interface CacheService {

    String getIotFactory(ReceiveIotAlarmFactorDto factorDto,String deviceCode,String alarmTime);
    void deleteCache();
}

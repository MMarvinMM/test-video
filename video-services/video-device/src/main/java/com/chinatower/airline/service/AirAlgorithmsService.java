package com.chinatower.airline.service;

import com.chinatower.device.entity.dto.AirAlTaskLogReq;
import com.chinatower.device.entity.dto.SyncAlgorithmsReq;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/11 12:12
 */
public interface AirAlgorithmsService {
    /**
     * @param syncAlgorithmsReq
     */
    void edit(SyncAlgorithmsReq syncAlgorithmsReq);

    /**
     *
     * @param airAlTaskLogReqs
     */
    void retryAirAlSync(List<AirAlTaskLogReq> airAlTaskLogReqs);
}

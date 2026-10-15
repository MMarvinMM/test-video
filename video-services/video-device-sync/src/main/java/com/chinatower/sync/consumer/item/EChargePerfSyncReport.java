package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EChargePerfSyncReport implements Serializable {
    /**
     * {
     *     "businessType": "4",
     *     "deviceCode": "DTMJ70134108",
     *     "signalDatas": [
     *         {
     *             "collectTime": "2025-05-14 16:49:10",
     *             "mid": "05104001",
     *             "value": "220.1"
     *         },
     *         {
     *             "collectTime": "2025-05-14 16:49:10",
     *             "mid": "05102001",
     *             "value": "1"
     *         }
     *     ]
     * }
     */


    // 设备编码
    private String deviceCode;

    private String businessType;
    private List<EnergySignalData> signalDatas;
}

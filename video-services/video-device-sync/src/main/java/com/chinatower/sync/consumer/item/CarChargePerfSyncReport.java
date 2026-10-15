package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CarChargePerfSyncReport implements Serializable {

    /**
     * [
     *     {
     *         "businessType": "8",
     *         "deviceCode": "1000260000699302",
     *         "signalDatas": [
     *             {
     *                 "mid": "00100000",
     *                 "value": "3",
     *                 "collectTime": "2025-05-14 16:49:47"
     *             },
     *             {
     *                 "mid": "00100007",
     *                 "value": "1.8705",
     *                 "collectTime": "2025-05-14 16:49:47"
     *             }
     *         ]
     *     }
     * ]
     */

    // 设备编码
    private String deviceCode;
    private String businessType;
    private List<EnergySignalData> signalDatas;
}

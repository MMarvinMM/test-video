package com.chinatower.sync.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;


@Data
public class EnergyDevAlarmClearReport extends BaseMongoDTO {
    /**
     * [
     *   {
     *     "businessType": "4",
     *     "clearCause": "1",
     *     "clearTime": "2025-05-20 15:59:46",
     *     "deviceCode": "41152240899010",
     *     "mid": "0438002001"
     *   }
     * ]
     */

    private String businessType;
    private String clearCause;
    private String clearTime;
    private String deviceCode;
    private String mid;
}

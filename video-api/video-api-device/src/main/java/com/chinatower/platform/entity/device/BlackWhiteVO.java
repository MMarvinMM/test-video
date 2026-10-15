package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlackWhiteVO extends BaseDataScopeEntity {
    /** 车牌号 */
    private String vehicleNumber;
    /** 生效时间 */
    private String validPeriod;
    private int configId;
    //1黑名单 2白名单
    private int blackOrWhite;
    private String validPeriodVo;
}

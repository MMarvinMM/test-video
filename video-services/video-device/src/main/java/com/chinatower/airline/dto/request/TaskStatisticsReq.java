package com.chinatower.airline.dto.request;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 10:58
 */
@Data
public class TaskStatisticsReq extends PageDTO {
    private String cityCode;
    private String userId;
    private String startTime;
    private String endTime;
    private String provinceCode;
    private String countyCode;
    private String queryType;
}

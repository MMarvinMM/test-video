package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @ClassName AlgorithmMonitorPointVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmMonitorPointVO {
    /** 监控区域点ID */
    String monitorPointId;
    /** 监控区域X轴 */
    Float percentX;
    /** 监控区域Y轴 */
    Float percentY;

    String lon;

    String lat;
}

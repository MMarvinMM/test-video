package com.chinatower.video.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @ClassName AlgorithmMonitorPointVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@Alias("AlgorithmMonitorPointVOVideo")
public class AlgorithmMonitorPointVO {
    String monitorPointId;
    Float percentX;
    Float percentY;
}

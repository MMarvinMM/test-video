package com.chinatower.video.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @ClassName AlgorithmMonitorAreaVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@Alias("AlgorithmMonitorAreaVOVideo")
public class AlgorithmMonitorAreaVO {
    String monitorAreaId;
    List<AlgorithmMonitorPointVO> moniPointList;
}
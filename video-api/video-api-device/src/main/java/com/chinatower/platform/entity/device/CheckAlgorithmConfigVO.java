package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmListVO
 * @Description
 * @Author hued
 * @Date 2022年12月23日10:17:01
 */
@Data
public class CheckAlgorithmConfigVO extends BaseDataScopeEntity {
    // 设备编号
    String deviceCode;
    // 通道编号
    String channelCode;
    // 预置位编码
    String presetId;
    // 算法编码
    String algorithmCode;

    // 预置位编码集合
    List<String> presetIdList;

    private List<String> configIdList;

    private String img;
}

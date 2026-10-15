package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmGroupDeviceVO
 * @Description
 * @Author hued
 * @Date 2024/12/31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmGroupDeviceVO {

    /** 通道编码 */
    String channelCode;
    /** 设备编码 */
    String deviceCode;
    /** 算法总数 */
    Integer total;
    /** 按设备通道分组算法list */
    List<AlgorithmAreaVO> rows;
}

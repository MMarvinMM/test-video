package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 通道算法信息.
 *
 * @author: fangjun
 */
@Data
public class ChannelAlgorithm implements Serializable {

    /**
     * 通道编码.
     */
    private String channelCode;

    /**
     * 通道名称.
     */
    private String channelName;

    /**
     * 算法编码列表.
     */
    private List<AlgorithmCode> algorithmList;
}

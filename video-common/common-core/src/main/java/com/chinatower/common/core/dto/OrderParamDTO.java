package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订购关系（订购信息）.
 *
 * @author: fangjun
 */
@Data
public class OrderParamDTO implements Serializable {

    /**
     * 应用编码.
     */
    private String appId;

    /**
     * 设备编码.
     */
    private String deviceCode;

    /**
     * 通道编码.
     */
    private String channelCode;

    /**
     * 算法编码列表.
     */
    private List<String> algorithmCodeList;

}

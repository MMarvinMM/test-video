package com.chinatower.platform.entity.devicemgmt.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/3 9:26
 * @description 通道信息
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelInfoVO implements Serializable {
    private static final long serialVersionUID = 7987843532124375764L;
    /**
     * 设备编码
     */
    private String parentDeviceCode;
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 通道状态
     */
    private String status;
    /**
     * 可视范围
     */
    private String visualRange;
    /**
     * 水平辐射角度
     */
    private String horizRange;
    /**
     * 俯仰辐射角度
     */
    private String vertiRange;
    /**
     * 租户ID
     */
    private String tenantId;

    private String modifier;
}

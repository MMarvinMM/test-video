package com.chinatower.analysis.api.lowalt.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 设备分布VO
 */
@Data
public class DeviceDistributionVO {
    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 设备数量
     */
    private Integer deviceCount;

    /**
     * 排序
     */
    private Integer rankNum;

    /**
     * 设备数量
     */
    private List<DeviceCount> deviceTypeCount;


    @Data
    public static class DeviceCount {

        /**
         * 设备类型名称
         */
        private String deviceTypeName;

        /**
         * 设备类型：1-雷达，2-rid，3-ads-b
         */
        private String deviceType;

        /**
         * 设备数量
         */
        private Integer deviceCount;

        public DeviceCount(String deviceTypeName, String deviceType, Integer deviceCount) {
            this.deviceTypeName = deviceTypeName;
            this.deviceType = deviceType;
            this.deviceCount = deviceCount;
        }
    }

}
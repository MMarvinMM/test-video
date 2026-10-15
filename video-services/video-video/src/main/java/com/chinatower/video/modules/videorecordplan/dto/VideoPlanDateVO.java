package com.chinatower.video.modules.videorecordplan.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: ChannelVideoPlanVO
 * @Description: 通道录像计划实体
 * @Author: chenmf
 * @Date: 2022-01-24 11:51
 **/
@Data
public class VideoPlanDateVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 年份
     */
    private int year;

    /**
     * 月份
     */
    private int month;

    /**
     * 年份
     */
    private int nextYear;

    /**
     * 月份
     */
    private int nextMonth;

    /**
     *客户唯一编号
     */
    private String tenantId;

    /**
     *行业编码
     *
     */
    private String industryCode;

    /**
     *版本编码
     *
     */
    private String appVerCode;


    private LocalDate firstDayOfMonth;

    private LocalDate lastDayOfMonth;

    private LocalDate nextFirstDayOfMonth;

    private LocalDate nextLastDayOfMonth;


    /**
     * 设备通道列表
     */
    private List<DeviceChannel> devices;

    /**
     * 内部类：设备通道信息
     */
    @Data
    public static class DeviceChannel implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 摄像机编码
         */
        private String deviceCode;

        /**
         * 通道编码
         */
        private String channelCode;

        /**
         * 构造函数
         * @param deviceCode 摄像机编码
         * @param channelCode 通道编码
         */
        public DeviceChannel(String deviceCode, String channelCode) {
            this.deviceCode = deviceCode;
            this.channelCode = channelCode;
        }
    }

    /**
     * 获取所有设备编码
     * @return 设备编码列表
     */
    public List<String> getAllDeviceCodes() {
        List<String> deviceCodes = new ArrayList<>();
        if (devices != null) {
            for (DeviceChannel device : devices) {
                deviceCodes.add(device.getDeviceCode());
            }
        }
        return deviceCodes;
    }

    /**
     * 获取指定设备的通道编码
     * @param deviceCode 设备编码
     * @return 通道编码列表（可能为空）
     */
    public List<String> getChannelCodesByDevice(String deviceCode) {
        List<String> channelCodes = new ArrayList<>();
        if (devices != null) {
            for (DeviceChannel device : devices) {
                if (deviceCode.equals(device.getDeviceCode())) {
                    channelCodes.add(device.getChannelCode());
                }
            }
        }
        return channelCodes;
    }



}

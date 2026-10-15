package com.chinatower.event.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: luo.zhenghong
 * @Date: 2022-7-25
 * @Description:
 * 物联设备告警接收请求实体
 */
@Data
public class IotDeviceGroupsVO {
    private static final long serialVersionUID = 1L;
    /**
     * 物联配置id
     */
    private String configId;


    /**
     * 符合规则物联设备集合
     */
    private List<DeviceGroup> deviceGroupList = new ArrayList<>();
    @Data
    public static class DeviceGroup{
        /**
         * 物联设备编码
         */
        private String deviceCode;
        /**
         * 符合规则时插入时间
         */
        private String insertTime;

        /**
         * 告警时间
         */
        private String alarmTime;
        private List<IotFactor> monitorList= new ArrayList<>();
    }

    @Data
    public static class IotFactor{
        /**
         * 物联因子编码
         */
        private String factorCode;

        /**
         * 监测值
         */
        private String factorValue;


        /**
         * 传感器编码
         */
        private String sensorCode;

        /**
         * 传感器名称
         */
        private String sensorName;


    }
}

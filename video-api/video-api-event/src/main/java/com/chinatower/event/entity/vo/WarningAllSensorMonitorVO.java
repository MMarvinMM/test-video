package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WarningSensorMonitorVO
 * @Deacription
 * @Author luo.zhenghong
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class WarningAllSensorMonitorVO{

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 监测时间
     */
    private String monitorTime;

    /**
     * 因子信息
     */
    private List<WarningAllSensorMonitorVO.IotFactor> monitorList = new ArrayList<>();
    @Data
    public static class IotFactor{
        /**
         * 因子编码
         */
        private String factorCode;

        /**
         * 因子名称
         */
        private String factorName;
        /**
         * 因子单位
         */
        private String factorUnit;

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

        /**
         * 是否高亮
         */
        private boolean isHighlight;
    }



}

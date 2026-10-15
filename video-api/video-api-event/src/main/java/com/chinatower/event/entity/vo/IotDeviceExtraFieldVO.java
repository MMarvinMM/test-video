package com.chinatower.event.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Auther: luo.zhenghong
 * @Date: 2022-7-25
 * @Description:
 * 物联设备告警接收请求实体
 */
@Data
public class IotDeviceExtraFieldVO {
    private static final long serialVersionUID = 1L;
    /**
     * 物联设备编码
     */
    private String iotDeviceCode;


    /**
     * 符合规则物联设备集合
     */
    private List<RelDeviceInfo> relDevice;
    @Data
    public static class RelDeviceInfo{
        /**
         * 设备编码
         */
        private String deviceCode;
        /**
         * 是否二次识别 1-是；0-否
         */
        private String isSecondIdentify;
        /**
         * 二次识别结果 1-真，0-假
         */
        private String secondIdentifyResult;
        /**
         * 联动算法
         */
        private String linkageAlgorithm;
    }
}

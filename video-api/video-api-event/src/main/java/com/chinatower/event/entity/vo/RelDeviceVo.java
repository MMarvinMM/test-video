package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: RelDeviceVo
 * @description: 设备关联的摄像机信息
 * @author: cuisj
 * @date: 2022/06/04
 * @requestID 需求单号及需求名称
 */
@Data
public class RelDeviceVo implements Serializable {
    /**
     * 摄像机编号
     */
    private String deviceCode;
    /**
     * 摄像机名称
     */
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    /**
     * 摄像机标签
     * */
    private String deviceLabelString;


    /**
     * 摄像机厂家名称
     */
    private String modelFactoryName;

    /**
     * 摄像机挂高，单位：米
     */
    private String height;

    /**
     * 摄像机经纬度
     */
    private String deviceLotAndLat;

    /**
     * 摄像机地址
     */
    private String deviceAddress;

    /**
     * 抓图时间
     */
    private String snapShotTime;

}

package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: IotRelDeviceVo
 * @description: 物联设备关联的摄像机信息
 * @author: yongqi.han
 * @date: 2022/7/19
 * @requestID 需求单号及需求名称
 */
@Data
public class IotRelDeviceVo  implements Serializable {
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
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 摄像机地址
     */
    private String deviceAddress;

    /**
     * 摄像机厂家
     */
    private String modelCode;

    /**
     * 摄像机地址 与TenantIotConfRelVO做映射
     */
    private String location;

    /**
     * 摄像机厂家名称
     */
    private String modelCodeName;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 设备收藏标记
     */
    private String deviceCodeCollect;

    /**
     * 摄像机图标地址
     * */
    private String deviceImageUrl;

    /**
     * 是否二次识别 1-是；0-否
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     */
    private String secondIdentifyResult;

    /**
     * 联动算法
     */
    private String linkageAlgorithm;

    /**
     * 摄像机类型
     */
    private String deviceType;

    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机子类型
     */
    private String deviceSubType;

    /**
     * 摄像机子类型名称
     */
    private String deviceSubTypeName;
}

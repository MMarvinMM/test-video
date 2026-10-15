package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceYjbRsp extends BaseDataScopeEntity {
    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 类别编码
     */
    private String categoryCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 位置信息
     */
    private String location;

    /**
     * 高度
     */
    private String height;

    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 设备型号编码
     */
    private String deviceModelCode;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 县/区编码
     */
    private String countyCode;

    /**
     * 乡镇编码
     */
    private String townCode;

    /**
     * 摄像机抓图
     */
    private List<String> snapshotImg;

    /**
     * 通道数量
     */
    private Integer channelCount;

    /**
     * 抓图时间
     */
    private String snapshotTime;

    /**
     *  标签编码
     */
    private String labelId;

    /**
     *  标签名称
     */
    private String labelName;

    /**
     * 流url地址  本次流ID
     */
    private String streamUrl;

    /**
     * 长度限制32字符
     */
    private String streamUuid;

    /**
     * 切换主子码流 2-不可切换；
     */
    private int streamSwitchFlag;

    /**
     * 返回信息
     */
    private String returnMsg;
}

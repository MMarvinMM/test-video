package com.chinatower.video.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDownloadVO implements Serializable {

    /**
     * 开始时间
     * @required true
     */
    private String startTime;

    /**
     * 结束时间
     * @required true
     */
    private String endTime;

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 通道编码
     * @required true
     */
    private String channelCode;

    /**
     * 0-前端存储；1-中心存储；2-实时录像下载；目前前端存储下载不支持。
     * @required true
     */
    private Integer storageType = 1;


    /**
     * 批次id
     */
    private String batchId;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    private String warningOrderId;

    private String tableType;

    /**
     * 存储时间(单位秒)   5年：157680000  默认2天:172800
     */
    private Integer memoryCycle = 172800;

    /**
     * 操作来源类型  1-前端下载  2-告警下载  3-一键告警
     */
    private Integer operateSourceType = 1;

    /**
     * 是否加水印 0:否  1:是
     */
    private String isWaterMark;
}

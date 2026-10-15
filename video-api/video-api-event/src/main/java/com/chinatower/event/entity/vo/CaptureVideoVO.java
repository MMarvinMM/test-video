package com.chinatower.event.entity.vo;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 抓视频参数
 * @author cuisj
 * @date 2024-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptureVideoVO extends BaseMongoDTO {

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 批次id
     */
    private String batchId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 告警id
     */
    private String warningOrderId;

    /**
     * 表名标识  1:压制 2.告警 3.研判
     * */
    private String tableType;

    /**
     * 表名
     * */
    private String tableName;

    /**
     * 下载进度
     * */
    private String process;

    /**
     * 下载状态1000正常，其他编码为下载异常
     * */
    private String processCode;

    /**
     * 下载录像地址
     * */
    private String downLoadUrl;

    /**
     * 受理节点编码
     */
    private String acceptNode;

    /**
     * 接入节点
     */
    private String accessNode;

    /** 租户ID */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;
}

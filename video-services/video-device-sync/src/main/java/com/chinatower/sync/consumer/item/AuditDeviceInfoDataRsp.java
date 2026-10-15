package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 4.10.中心和属地设备/通道同步-到其他系统-接口拉取 请求实体类
 */
@Data
public class AuditDeviceInfoDataRsp implements Serializable {
    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;

    /**
     * 最后一条数据ID
     */
    private Long lastDataId;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 返回查询结果列表
     */
    private List<AIotDeviceSync> data;
}


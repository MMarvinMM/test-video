package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * <p>
 * <p>
 * 类名称: UserMemoryOperateDTO.java
 *
 * @date 2022/05/29 16:33
 */
@Data
public class PolygonOperateDTO extends BaseDataScopeEntity {
    /**任务id*/
    private String taskId;

    /**
     * 设备编码
     * */
    private String deviceCode;

    /**
    * 通道编码
    * */
    private String channelCode;

    /**
     * 预置位id
     * */
    private String presetId;

    private String tenantId;

    private String industryCode;

    /**
     * 识别区域唯一编码
     * */
    private String monitorAreaId;

}

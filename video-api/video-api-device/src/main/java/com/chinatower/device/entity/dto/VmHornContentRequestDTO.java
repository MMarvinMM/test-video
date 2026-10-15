package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * @author willem
 * @date 2024/10/10
 * @description
 */
@Data
public class VmHornContentRequestDTO extends BaseDataScopeEntity {
    /**
     * 任务查询内容列表入参
     */
    private String param;
    /**
     * 内容类型 0-音频 1-文本
     */
    private Integer type;

    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页展示数量
     */
    private Integer pageSize;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
}

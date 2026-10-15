package com.chinatower.event.dto;

import com.chinatower.common.core.dto.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "告警声音查询入参")
public class AlarmSoundDTO extends BaseModel implements Serializable {
    /**
     * 告警声音ID
     */
    private Long alarmSoundId;
    /**
     * 告警声音名称
     */
    private String alarmSoundName;
    /**
     * 客户ID
     */
    private String tenantId;
    /**
     * 文件名称
     */
    private String docName;
    /**
     * 状态 0启用 1停用
     */
    private String statusCd;
    /**
     * 0 默认声音，1 其他声音
     */
    private String soundType;
    /**
     * 行业应用
     */
    private String industryCode;
    /**
     * 应用版本
     */
    private String appVerCode;

    /**
     * 排序方式
     * 排序字段
     * @return
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;
}

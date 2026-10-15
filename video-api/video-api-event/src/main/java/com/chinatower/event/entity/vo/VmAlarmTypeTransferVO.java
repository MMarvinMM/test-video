package com.chinatower.event.entity.vo;

import com.chinatower.common.core.dto.BaseModel;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.entity.BasePlatformEntity;
import lombok.Data;

/**
 * @className: VmAlarmTypeTransferVO
 * @description: 告警转移关联告警类型bean
 * @author: lyy
 * @date: 2023/06/30
 * @version  v1.6.00
 */
@Data
public class VmAlarmTypeTransferVO extends BaseIndustryEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 告警转移策略ID
     */
    private Long alarmTransferId;

    /**
     * 告警来源 （1-AI告警； 2-人工上报；3-一键告警；4-摄像机告警；8-物联告警；9-卫星告警；10-雷达告警）
     */
    private String warningSource;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 租户ID
     */
    private String tenantId;
}

package com.chinatower.bdm.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceSelectVO extends CommonFeignEntity {

    /**
     * 0-收藏
     * 1-全部
     */
    private String isMonitorFlag;

    private String userId;

    /**
     * 应急事件编码
     */
    private String eventCode;

    /**
     * 阶段编码
     */
    private String stageId;

    /**
     * 任务编码
     */
    private String taskId;

    /**
     * 应急资源标识 传1代表查是否是应急资源
     */
    private String isEmergencyFlag;

}

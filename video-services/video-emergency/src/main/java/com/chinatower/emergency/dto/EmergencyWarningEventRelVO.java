package com.chinatower.emergency.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: EmergencyWarningEventRelVO
 * @description: 事件与告警关系表
 * @author: fengty
 * @create: 2024-10-28 14:18
 */
@Data
public class EmergencyWarningEventRelVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 告警主键
     */
    @NotNull(message = "告警编码不能为空")
    private String warningOrderId;

    /**
     * 事件主键
     */
    @NotNull(message = "事件编码不能为空")
    private String eventCode;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标记，0:正常 1：删除
     */
    private Integer deleteFlag;

    private List<String> eventCodes;

}

package com.chinatower.event.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 环节处理超期提醒表
 * </p>
 * @author lyy
 * @version v1.5.75
 * @date 2023/03/20 14:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_flow_link_overdue_remind")
public class VmFlowLinkOverdueRemindDO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 4182320090573659121L;

    /** 主键 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /** 流程ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attrId;
    /** 超期提醒时间 */
    private Integer timeLimitRemind;
    /** 超期提醒单位, 0: 秒，1：分钟，2：小时，3：天 */
    private String timeLimitRemindCompany;
    /** 超期提醒人 */
    private String timeLimitRemindName;
    /** 超期提醒人姓名集合 (提供给app)*/
    private List<Map<String, String>> timeLimitRemindNames;
    /** 超期提醒标识（0:环节处理超期提醒；1：受理超期提醒） */
    private String overdueRemindSign;
}

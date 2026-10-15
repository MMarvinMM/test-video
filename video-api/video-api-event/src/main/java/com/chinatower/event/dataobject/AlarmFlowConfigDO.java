package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警流程配置表
 * </p>
 *
 * @author AutoGen
 * @since 2022-06-13
 */
@Getter
@Setter
@TableName("vm_alarm_flow_config")
public class AlarmFlowConfigDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long alarmFlowId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 事件来源
     */
    private String warningSource;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 模板ID
     */
    private Long flowId;

    /**
     * 组织ID, 中间用用逗号隔开
     */
    private String orgId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 1生效 0失效
     */
    private String status;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;


}

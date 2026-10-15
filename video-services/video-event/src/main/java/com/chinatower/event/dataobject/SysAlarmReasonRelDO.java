package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_alarm_reason_rel")
public class SysAlarmReasonRelDO implements Serializable {

    /**
     *  ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     *  告警类型值
     */
    private String typeValue;
    
    /**
     * 告警原因名称
     */
    private String reason;

    /**
     * 租户编码
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
     * 创建人
     */
    private String creator;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新人
     */
    private String modifier;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 0正常 1删除
     */
    private Integer deleteFlag;

}

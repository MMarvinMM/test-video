package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 第三方告警配置Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyAlarmVo extends BaseDataScopeEntity implements Serializable {

    /**
     * id主键
     */
    private Integer id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 告警来源名称
     */
    private String warningSourceName;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识 0正常  1删除
     */
    private Integer deleteFlag;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业应用版本
     */
    private String appVerCode;
}

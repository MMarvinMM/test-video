package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警类型与行业关联表
 * @author tianhao
 */
@Data
public class SysAlarmTypeIndustryRelVo implements Serializable {

    //告警类型与行业关联表ID
    private Long sysAlarmTypeIndustryRelId;

    //告警类型ID
    private String alarmTypeId;

    //行业编码
    private String industryCode;

    //是否可见 1000:可见  1100: 不可见
    private String isVisible;

    //创建人
    private String creator;

    //创建时间
    private Date createTime;

    //修改人
    private String modifier;

    //修改时间
    private Date updateTime;

    //0正常 1删除
    private Integer deleteFlag;

    //告警类型值
    private String typeValue;

    //租户id
    private String tenantId;

    //行业应用版本
    private String appVerCode;
}

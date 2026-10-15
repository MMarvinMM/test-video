package com.chinatower.platform.project.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_industry_auth")
public class SysIndustryAuth implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long authId;

    private Integer authType;

    private Long compoId;

    private Long menuId;

    private Long viewId;

    private String tenantId;

    private String industryCode;

    private String appVerCode;
}

package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class SysInfoSetVO extends BaseDataScopeEntity {

    private String setId;

    /**
     * SysInfoSetVOType
     */
    private String type; //类型: 0-默认配置 1-本人 2-个性化主题 3-本组织 个性化配置 4-本客户 个性化配置

    @NotNull(message = "属性名称不能为空")
    @Size(max = 30, message = "属性名称长度不能超过30个字符")
    private String setName; //设置名称

    private String setValue;//设置值

    private String remarks; //备注

    private String creator;//创建人

    private Date createTime;//创建时间

    private String deptId;//组织ID
    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 1-要查询默认值
     */
    private int isDefault;
}

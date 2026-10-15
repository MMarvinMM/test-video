package com.chinatower.platform.project.system.domain;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @date 2022-03-21
 */
@Data
public class SysDeptStreetRel extends SystemBaseEntity
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "组织机构id")
    private String deptId;

    @ApiModelProperty(value = "省")
    private String provinceCode;

    @ApiModelProperty(value = "市")
    private String cityCode;

    @ApiModelProperty(value = "区")
    private String countyCode;

    @ApiModelProperty(value = "街道")
    private String streetAreaCode;

    @ApiModelProperty(value = "行业应用版本")
    private String appVerCode;

    @ApiModelProperty(value = "行业应用编码")
    private String industryCode;


    public SysDeptStreetRel() {

    }

    public SysDeptStreetRel(String deptId, String streetAreaCode) {
        this.deptId = deptId;
        this.streetAreaCode = streetAreaCode;

    }

}

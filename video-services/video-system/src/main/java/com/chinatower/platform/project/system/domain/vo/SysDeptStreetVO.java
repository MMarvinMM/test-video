package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("管理区域视图")
public class SysDeptStreetVO implements Serializable {

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "组织机构id")
    //private String deptId;
    private String orgId;

    @ApiModelProperty(value = "省")
    private String provinceCode;

    @ApiModelProperty(value = "市")
    private String cityCode;

    @ApiModelProperty(value = "区")
    private String countyCode;

    @ApiModelProperty(value = "街道")
    private String streetCode;

    @ApiModelProperty(value = "街道")
    private String streetAreaCode;


    @ApiModelProperty(value = "行业应用版本")
    private String appVerCode;

    public String getStreetCode(){
        if(StringUtils.isNotBlank(streetCode)){
            return streetCode;
        }
        return streetAreaCode;
    }

    @ApiModelProperty(value = "组织机构id")
    //private String deptId;
    private String deptId;
}

package com.chinatower.bdm.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 大屏组件执法设备统计基础版返回实体类
 */
@Data
@AllArgsConstructor
public class AllResourceTypeNumVO implements Serializable {

    @ApiModelProperty("资源类型名称")
    private String resourceTypeName;

    @ApiModelProperty("资源数量")
    private Long resourceNum;
}

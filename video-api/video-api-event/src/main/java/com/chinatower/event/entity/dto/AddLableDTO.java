package com.chinatower.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AddTagDTO
 * @Deacription 新增告警类型标签对象
 * @Author chenmf
 * @Date 2022/4/26 16:25
 * @Version 1.0
 **/
@Data
@ApiModel(value = "新增告警标签对象",description = "新增告警标签对象")
public class AddLableDTO implements Serializable {

    /**
     * 告警标签名称
     * @required true
     */
    @ApiModelProperty(value = "告警标签名称")
    private String lableName;

}

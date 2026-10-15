package com.chinatower.common.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName ModifyTagDTO
 * @Deacription 修改告警类型标签对象
 * @Author chenmf
 * @Date 2022/4/26 16:25
 * @Version 1.0
 **/
@Data
@ApiModel(value = "修改告警类型标签对象",description = "修改告警类型标签对象")
public class ModifyTagDTO implements Serializable {
    
    @NotNull(message = "告警类型标签Id不能为空")
    @ApiModelProperty(value = "告警类型标签Id")
    private Long tagId;
    
    @NotBlank(message = "告警类型标签名称不能为空")
    @ApiModelProperty(value = "告警类型标签名称")
    @Size(min = 0, max = 10, message = "告警类型标签名称长度不能超过10个字符")
    private String tagName;

}

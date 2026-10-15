package com.chinatower.event.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName AddTagDTO
 * @Deacription 新增告警类型标签对象
 * @Author chenmf
 * @Date 2022/4/26 16:25
 * @Version 1.0
 **/
@Data
@ApiModel(value = "新增告警类型标签对象",description = "新增告警类型标签对象")
public class AddTagDTO implements Serializable {
    
    @NotBlank(message = "告警类型标签名称不能为空")
    @ApiModelProperty(value = "告警类型标签名称")
    @Size(min = 0, max = 10, message = "告警类型标签名称长度不能超过10个字符")
    private String tagName;

}

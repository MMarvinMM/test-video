package com.chinatower.event.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DeleteTagDTO
 * @Deacription 删除告警类型标签对象
 * @Author chenmf
 * @Date 2022/4/26 16:25
 * @Version 1.0
 **/
@Data
@ApiModel(value = "删除告警类型标签对象",description = "删除告警类型标签对象")
public class DeleteTagDTO implements Serializable {
    
    @NotNull(message = "告警类型标签Id不能为空")
    @ApiModelProperty(value = "告警类型标签Id")
    private Long tagId;

}

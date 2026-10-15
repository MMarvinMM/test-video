package com.chinatower.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeleteTagDTO
 * @Deacription 删除告警类型标签对象
 * @Author chenmf
 * @Date 2022/4/26 16:25
 * @Version 1.0
 **/
@Data
@ApiModel(value = "删除告警标签对象",description = "删除告警标签对象")
public class DeleteLableDTO implements Serializable {

    /**
     * 告警标签ID
     * @required true
     */
    @ApiModelProperty(value = "告警标签Id")
    private Long lableId;

}

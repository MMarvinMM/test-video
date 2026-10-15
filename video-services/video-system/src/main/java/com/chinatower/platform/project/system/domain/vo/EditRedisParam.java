package com.chinatower.platform.project.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel
public class EditRedisParam {

    @ApiModelProperty("id")
    private Long id;

    @NotBlank
    @ApiModelProperty("key")
    private String key;

    @NotBlank
    @ApiModelProperty("值类型")
    private String valueType;

    @NotNull
    @ApiModelProperty("过期时间")
    private Date dueTime;

    @NotBlank
    @ApiModelProperty("类型")
    private String sourceType;

    @NotBlank
    @ApiModelProperty("内容")
    private String content;

}

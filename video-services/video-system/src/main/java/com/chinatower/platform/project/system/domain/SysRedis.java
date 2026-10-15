package com.chinatower.platform.project.system.domain;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class SysRedis extends SystemBaseEntity {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("key")
    private String key;

    @ApiModelProperty("值类型")
    private String valueType;

    @ApiModelProperty("过期时间")
    private Date dueTime;

    @ApiModelProperty("类型")
    private String sourceType;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("执行结果")
    private String resultType;

    @ApiModelProperty("失败原因")
    private String errorReason;

}

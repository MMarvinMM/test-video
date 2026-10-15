package com.chinatower.platform.project.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "检验用户是否需要更新密码")
public class VerifyUpdateVO {

    @ApiModelProperty(value = "是否需要更新:0-不需要，1-需要")
    private String needUpdate;

    @ApiModelProperty(value = "剩余天数")
    private Integer remainingDays;

    @ApiModelProperty(value = "密码到期时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;

}

package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 小程序版本对象 bs_mini_program_upgrade_info
 * @author lxt
 * @date 2024-08-05
 */
@ApiModel("小程序版本管理")
@Data
public class MiniProgramUpgradeInfoVO extends PageDTO {

    private Long id;

    @ApiModelProperty(value = "属地版本")
    private String colonyVersion;

    @ApiModelProperty(value = "小程序版本")
    private String miniProgramVersion;

    @ApiModelProperty(value = "代码版本")
    private String codeVersion;

    private String colonyCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeEnd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeEnd;

}

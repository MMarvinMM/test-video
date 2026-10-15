package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预置点
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPresetReq extends DeviceSouthCommonDTO {

    @ApiModelProperty(value = "预置点名称，以平台设置为准", required = true)
    @NotNull(message = "预置点名称不能为空")
    @Size(min = 0, max = 20, message = "预置点名称长度不能超过20个字符")
    private String presetName;

}

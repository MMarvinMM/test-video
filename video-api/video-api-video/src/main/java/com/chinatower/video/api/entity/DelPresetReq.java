package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;

/**
 * 预置点
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("DelPresetReqVideo")
public class DelPresetReq extends DeviceSouthCommonDTO {
    @ApiModelProperty(value = "预置点id", required = true)
    @NotNull(message = "预置点id不能为空")
    private Long presetId;
}

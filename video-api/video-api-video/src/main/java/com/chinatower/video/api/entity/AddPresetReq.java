package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预置位
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("AddPresetReqVideo")
public class AddPresetReq extends DeviceSouthCommonDTO {

    @ApiModelProperty(value = "预置位名称，以平台设置为准", required = true)
    @NotNull(message = "预置位名称不能为空")
    @Size(min = 0, max = 20, message = "预置位名称长度不能超过20个字符")
    private String presetName;

    private String industryCode;
    private String appVerCode;

    // 请求来源 0:pc端  1:app端
    private String reqFrom;

    /**
     * 受理节点
     * @required true
     */
    private String acceptNode;

    /**
     * 接入节点
     * @required true
     */
    private String accessNode;
}

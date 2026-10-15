package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 预置点
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("BatchDelPresetReqVideo")
public class BatchDelPresetReq extends DeviceSouthCommonDTO {
    @ApiModelProperty(value = "预置点列表", required = true)
    @NotEmpty(message ="预置点列表不能为空")
    private List<Long> presets;

   /* @ApiModelProperty(value = "设备编号列表", required = true)
    @NotEmpty(message ="设备编号列表不能为空")
    private List<String> deviceCodes;*/

    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;
}

package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDoSwitchLightReq extends VideoBaseControlReq{
    /**
     * 开关类型
     *  1-开灯;2-关灯
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_SWITCH_TYPE)
    private int switchType;
    /**
     * 操作类型
     *  1:雨刷  2灯光 3：透雾
     * */
    @NotBlank(message = "操作类型不能为空")
    private int optType;
}

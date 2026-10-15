package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName: ReservePlanVO
 * @Description: 视频预案实体
 * @Author: zhaozj
 * @Date: 2022/4/6 16:07
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelReservePlanDTO implements Serializable {
    
    /**
     * 预案id
     */
    @NotNull(message = "预案id不能为空")
    @ApiModelProperty(value = "预案id", required = true)
    private String planId;
    
}

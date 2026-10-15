package com.chinatower.airline.dto.request;

import com.chinatower.airline.anno.EnumTypeValid;
import com.chinatower.airline.enums.OptTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 录像参数
 *
 * @author ljy
 * @date 2026/1/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecordingReq extends BaseAirDeviceReq{
    @ApiModelProperty(value = "操作类型",notes = "0:关闭录像，1开启录像")
    @NotNull(message = "操作类型不能为空")
    @EnumTypeValid(enumClass = OptTypeEnum.class, message = "操作类型支持 0|1(关闭｜开启)")
    private Integer optType;
    
}

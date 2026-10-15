package com.chinatower.device.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 算法状态信息
 *
 * @return 
 * @author chenmf
 * @date 2022/3/24
 * @version
 */
@Data
public class AlgoStatusReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 任务id
     */
    @NotNull(message = "任务id不能为空")
    @ApiModelProperty(value = "任务id")
    private String taskId;

    //铁塔视角算法状态：1:启用成功 2:启用失败 3:处理中
    @ApiModelProperty(value = "算法启动状态")
    private int algorithmStatus;
}

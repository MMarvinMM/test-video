package com.chinatower.bdm.modules.timeline.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)实体类
 *
 * @author makejava
 * @since 2024-05-27
 */
@Data
@ApiModel(value = "VmSensingAlgorithmTaskInfoDto", description = "遥感算法任务信息表DTO")
public class VmSensingAlgorithmTaskInfoDto extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 30, message = "任务名称最多30个字符")
    @ApiModelProperty(value = "任务名称", required = true)
    private String taskName;

    /**
     * 图层1 信息
     */
    @ApiModelProperty(value = "图层一信息")
    private LayerInfoDto layerOneInfo;

    /**
     * 图层2 信息
     */
    @ApiModelProperty(value = "图层二信息")
    private LayerInfoDto layerTwoInfo;

    /**
     * 是否默认，1:默认，0:非默认
     */
    @ApiModelProperty(value = "是否默认，1:默认，0:非默认")
    private Integer defaultFlag;

    /**
     * 算法code
     */
    @ApiModelProperty(value = "算法code")
    private String algorithmCode;

    /**
     * 算法名称
     */
    @ApiModelProperty(value = "算法名称")
    private String algorithmName;

}

package com.chinatower.bdm.modules.timeline.dto;

import com.chinatower.common.core.dto.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)查询实体类
 *
 * @author makejava
 * @since 2024-05-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "VmSensingAlgorithmTaskInfoQueryDto", description = "遥感算法任务信息表查询实体类")
public class VmSensingAlgorithmTaskInfoQueryDto extends BaseModel {
    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long id;

    /**
     * 任务名称（模糊查询）
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 算法名称（算法类型，模糊查询）
     */
    @ApiModelProperty(value = "算法名称")
    private String algorithmName;
    /**
     * 算法code
     */
    @ApiModelProperty(value = "算法code")
    private String algorithmCode;

    /**
     * 任务状态（1:执行中，2:成功，3:失败）
     */
    @ApiModelProperty(value = "任务状态，1:执行中，2:成功，3:失败")
    private Integer taskStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "开始创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    @ApiModelProperty(value = "结束创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "开始修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    @ApiModelProperty(value = "结束修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String creator;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "是否展示二次识别结果")
    private String showRecogResult;

}

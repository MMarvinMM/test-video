package com.chinatower.bdm.modules.timeline.vo;

import com.chinatower.bdm.modules.timeline.dto.LayerInfoDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)展示类
 *
 * @author makejava
 * @since 2024-05-27
 */
@Data
@ApiModel(value = "VmSensingAlgorithmTaskInfoVo", description = "遥感算法任务信息表展示类")
public class VmSensingAlgorithmTaskInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 任务状态，1:执行中，2:成功，3:失败
     */
    @ApiModelProperty(value = "任务状态，1:执行中，2:成功，3:失败")
    private Integer taskStatus;

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

    /**
     * 图层1信息
     */
    @ApiModelProperty(value = "图层1信息")
    private LayerInfoDto layerOneInfo;
    private String layerOneInfoStr;

    /**
     * 图层2信息
     */
    @ApiModelProperty(value = "图层2信息")
    private LayerInfoDto layerTwoInfo;
    private String layerTwoInfoStr;

    /**
     * 默认标识，1:默认，0或者null非默认
     */
    @ApiModelProperty(value = "是否默认，1:默认，0:非默认")
    private Integer defaultFlag;

    /**
     * 删除标识，0:未删除，1:删除
     */
    @ApiModelProperty(value = "删除标识")
    private Integer deleteFlag;

    /**
     * 行业信息
     */
    @ApiModelProperty(value = "行业信息")
    private String industryCode;

    /**
     * 行业应用版本编码
     */
    @ApiModelProperty(value = "行业应用版本编码")
    private String appVerCode;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

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

    /**
     * 任务执行失败时的错误信息
     */
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    private String taskBatchId;

    private String secondRecogResult;

}

package com.chinatower.video.modules.arcruiseplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * AR巡航计划表
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@Data
@TableName("vm_ar_cruise_plan")
@ApiModel(value = "ArCruisePlan对象", description = "AR巡航计划表")
public class ArCruisePlanVo extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("唯一标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("AR实景ID")
    private String arViewId;

    @ApiModelProperty("通道编码")
    private String channelCode;

    @ApiModelProperty("巡航计划编号")
    private String cruisePlanNumber;

    @ApiModelProperty("巡航计划名称")
    private String cruisePlanName;

    @ApiModelProperty("执行状态0未执行1等待中2执行中")
    private String executionStatus;

    @ApiModelProperty("执行位置（tag_id）")
    private String executionStep;

    @ApiModelProperty("删除标志位：0正常、1：删除")
    private Integer deleteFlag;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建人")
    @TableField(exist = false)
    private String creatorName;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改人")
    @TableField(exist = false)
    private String modifierName;

    @ApiModelProperty("查询时间")
    private String queryTime;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @TableField(exist = false)
    private List<ArCruisePlanRel> arCruisePlanRelList;

}

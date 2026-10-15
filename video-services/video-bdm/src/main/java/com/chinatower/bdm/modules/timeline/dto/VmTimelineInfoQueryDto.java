package com.chinatower.bdm.modules.timeline.dto;

import com.chinatower.common.core.dto.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 时间轴基础表(VmTimelineInfo)查询实体类
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "VmTimelineInfoQueryDto", description = "时间轴基础表查询实体类")
public class VmTimelineInfoQueryDto extends BaseModel {
    /**
     * 时间轴信息ID
     */
    @ApiModelProperty(value = "时间轴信息ID")
    private Long timelineId;
    /**
     * 时间轴名称
     */
    @ApiModelProperty(value = "时间轴名称")
    private String timelineName;
    /**
     * 是否展示时间轴详情 默认为空展示，Y-展示 N-不展示
     */
    @ApiModelProperty(value = "是否展示时间轴详情，默认为空展示，Y-展示 N-不展示")
    private String showDetail;

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


}

package com.chinatower.bdm.modules.timeline.dto;

import com.chinatower.common.core.dto.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 时间轴详情表(VmTimelineDetail)查询实体类
 *
 * @author makejava
 * @since 2024-05-27 17:16:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "VmTimelineDetailQueryDto", description = "时间轴详情表查询实体类")
public class VmTimelineDetailQueryDto  extends BaseModel {

    /**
     * 时间轴信息ID
     */
    @ApiModelProperty(value = "时间轴信息ID")
    private Long timelineId;

    /**
     * 图层ID
     */
    @ApiModelProperty(value = "图层ID")
    private String layer;

    /**
     * 图层名称
     */
    @ApiModelProperty(value = "图层名称")
    private String layerName;

}

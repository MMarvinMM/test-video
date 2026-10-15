package com.chinatower.bdm.modules.timeline.dto;

import com.chinatower.bdm.modules.timeline.entity.VmTimelineDetail;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 时间轴基础表(VmTimelineInfo)实体类
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
@Data
@ApiModel(value = "VmTimelineInfoDto", description = "")
public class VmTimelineInfoDto extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -6557918250789971329L;
    /**
     * 时间轴信息ID
     */
    @ApiModelProperty(value = "时间轴信息ID")
    private Long timelineId;
    /**
     * 时间轴名称
     */
    @NotBlank(message = "时间轴名称不能为空")
    @ApiModelProperty(value = "")
    private String timelineName;

    /**
     * 排序值
     */
    @ApiModelProperty(value = "排序值")
    private String sortNum;

    /**
     * 时间轴详情列表
     */
    @ApiModelProperty(value = "时间轴详情列表")
    private List<VmTimelineDetail> timelineDetailList;

}

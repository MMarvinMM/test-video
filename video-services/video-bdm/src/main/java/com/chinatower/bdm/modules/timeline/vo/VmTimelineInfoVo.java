package com.chinatower.bdm.modules.timeline.vo;

import com.chinatower.bdm.modules.timeline.entity.VmTimelineDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 时间轴基础表(VmTimelineInfo)实体类
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
@Data
@ApiModel(value = "VmTimelineInfoVo", description = "时间轴基础表展示类")
public class VmTimelineInfoVo implements Serializable {
    private static final long serialVersionUID = 438280293474520919L;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "更新人")
    private String modifier;
    /**
     * 0正常 1删除
     */
    @ApiModelProperty(value = "删除标识")
    private Integer deleteFlag;

    /**
     * 1:默认，0或者null是非默认
     */
    @ApiModelProperty(value = "是否默认")
    private Integer defaultFlag;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortNum;

    /**
     * 时间轴详情列表
     */
    @ApiModelProperty(value = "时间轴详情列表")
    private List<VmTimelineDetail> timelineDetailList;

}

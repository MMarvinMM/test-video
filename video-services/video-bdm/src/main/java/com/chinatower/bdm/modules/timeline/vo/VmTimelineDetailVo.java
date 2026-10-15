package com.chinatower.bdm.modules.timeline.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 时间轴详情表(VmTimelineDetail)实体类
 *
 * @author makejava
 * @since 2024-05-27 17:16:16
 */
@Data
@ApiModel(value = "VmTimelineDetailVo", description = "时间轴详情表实体展示类")
public class VmTimelineDetailVo implements Serializable {
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
     * 时间轴详情ID
     */
    @ApiModelProperty(value = "时间轴详情ID")
    private Long timelineDetailId;
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
    /**
     * 图层名字空间
     */
    @ApiModelProperty(value = "图层名字空间")
    private String layerNamespace;
    /**
     * 年
     */
    @ApiModelProperty(value = "年")
    private String year;
    /**
     * 月
     */
    @ApiModelProperty(value = "月")
    private String month;
    /**
     * 日
     */
    @ApiModelProperty(value = "日")
    private String day;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private String sortNum;
    /**
     * 二维对象属性
     */
    @ApiModelProperty(value = "二维对象属性")
    private String geoType;
    /**
     * 服务器地址
     */
    @ApiModelProperty(value = "服务器地址")
    private String rootGeoserverUrl;

    /**
     *  图层名字
     */
    @ApiModelProperty("图层名字")
    private String customName;

    /**
     * 最大经度
     */
    @ApiModelProperty("最大经度")
    private String maxx;
    /**
     * 最大纬度
     */
    @ApiModelProperty("最大纬度")
    private String maxy;

    /**
     * 最小经度
     */
    @ApiModelProperty("最小经度")
    private String minx;

    /**
     * 最小纬度
     */
    @ApiModelProperty("最小纬度")
    private String miny;

    /**
     * 停留时间
     */
    @ApiModelProperty("停留时间")
    private String stayTime;

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
    @ApiModelProperty(value = "删除辨识")
    private Integer deleteFlag;

    /**
     * 图层类型 1: '矢量图层' 2: '非矢量图层'.3-DEM 4-DSM 5-倾斜摄影 6-激光点云
     */
    private String category;

    /**
     * 3456 图层类型的文件地址
     */
    private String fileUrl;

}

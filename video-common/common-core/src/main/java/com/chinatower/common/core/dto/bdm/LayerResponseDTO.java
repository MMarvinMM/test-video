package com.chinatower.common.core.dto.bdm;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @desription: 图层列表返回
 * @data 创建时间：2023/4/7 9:23
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerResponseDTO {
    /**
     * 数据ID
     */
    @ApiModelProperty(value = "数据ID")
    private String id;
    /**
     * 自定义图层名称
     */
    @ApiModelProperty(value = "自定义图层名称")
    private String customName;
    /**
     * 图层类别
     */
    @ApiModelProperty(value = "图层类别")
    private String layerTypeName;

    /**
     * 图层类别ID
     */
    @ApiModelProperty(value = "图层类别ID")
    private String layerTypeId;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 行业编码
     */
    @ApiModelProperty(value = "行业编码")
    private String industryCode;
    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;
    /**
     * 行业应用
     */
    @ApiModelProperty(value = "行业应用")
    private String industryName;
    /**
     * 行业版本编码
     */
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;
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
     * geoserver地址
     */
    @ApiModelProperty(value = "geoserver地址")
    private String geoserverUrl;

    /**
     * 缩减的图层url
     */
    @ApiModelProperty(value = "缩减的图层url")
    private String viewUrl;
    /**
     * geoserver图层名称
     */
    @ApiModelProperty(value = "geoserver图层名称")
    private String layerName;

    /**
     * geoserver图层命名空间
     */
    @ApiModelProperty(value = "图层命名空间")
    private String layerNamespace;
    /**
     * 数据属性
     */
    @ApiModelProperty(value = "数据属性")
    private String gameKey;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 最大X轴
     */
    @ApiModelProperty(value = "最大X轴")
    private Double maxx;
    /**
     * 最大Y轴
     */
    @ApiModelProperty(value = "最大Y轴")
    private Double maxy;
    /**
     * 最小X轴
     */
    @ApiModelProperty(value = "最小X轴")
    private Double minx;
    /**
     * 最小Y轴
     */
    @ApiModelProperty(value = "最小Y轴")
    private Double miny;

    /**
     * 最小Y轴
     */
    @ApiModelProperty(value = "简称")
    private String abbreviation;

    /**
     * geoserver跟地址
     */
    @ApiModelProperty(value = "geoserver跟地址")
    private String rootGeoserverUrl;
    /**
     * 图层总面积
     */
    @ApiModelProperty(value = "图层总面积")
    private Double layerArea;
    /**
     * 图层图斑总数
     */
    @ApiModelProperty(value = "图层图斑总数")
    private Integer polygonCount;

    /**
     * 图层类型
     */
    @ApiModelProperty(value = "图层图斑总数")
    private String geoType;

    /**
     * 节点编码
     */
    private String nodeCode;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 套合图层标记 1-是 0不是
     */
    private String nestFlag;

    private String category;

    private String fileUrl;
    /**
     * 图斑编号字段
     */
    private String tbbhRef;
    /**
     * 图斑面积字段
     */
    private String areaRef;

    /**
     * 备注
     */
    private String notes;

    private List<LayerTypeStyleMappingVO> styles;

    // ar标签是否收藏 0未收藏 1已收藏
    private Integer markFlag;

    /**
     * 上传类型 1:图层链接；2上传文件
     */
    private String uploadType;

    /**
     * 发布状态 0:发布中；1发布成功：2发布失败
     */
    private String publishStatus;

    private String obsUrl;

    private String originUrl;

    private String fileName;

}

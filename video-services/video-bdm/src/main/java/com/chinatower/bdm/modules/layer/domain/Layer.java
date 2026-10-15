package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 图层管理
 * </p>
 *
 * @author ch
 * @since 2023-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_layer")
@ApiModel(value="Layer对象", description="图层管理")
public class Layer  {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "图层名称")
    private String layerName;

    @ApiModelProperty(value = "图层类别ID")
    private String layerTypeId;

    @ApiModelProperty(value = "图层命名空间")
    private String layerNamespace;

    @ApiModelProperty(value = "geoserver地址")
    private String geoserverUrl;

    @ApiModelProperty(value = "geoserver地址")
    private String viewUrl;

    @ApiModelProperty(value = "自定义图层名称")
    private String customName;

    @ApiModelProperty(value = "背景色")
    private String layerColor;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "删除状态: 原字典值：normal正常，删除deleted 现字典值：0正常，删除1")
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleteFlag;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    @ApiModelProperty(value = "图层文件ID")
    private String fileId;

    @ApiModelProperty(value = "图层文件名称")
    private String fileName;


    @ApiModelProperty(value = "图层文件地址")
    private String fileUrl;


    @ApiModelProperty(value = "数据属性")
    private String gameKey;

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
     * 图层总面积
     */
    @ApiModelProperty(value = "图层总面积")
    private Double layerArea;
    /**
     * 图层图斑总数
     */
    @ApiModelProperty(value = "图层图斑总数")
    private Integer polygonCount;
    private String rootGeoserverUrl;
    private String rootLayerName;
    /**
     * 几何类型
     */
    @ApiModelProperty(value = "几何类型")
    private String geoType;

    private String nodeCode;

    private String nodeName;
    private String nestFlag;
    /**
     * 1-矢量 2-非矢量 3-DEM 4-DSM 5-倾斜摄影 6-激光点云
     */
    private String category;

    private String layerTypeName;

    private String abbreviation;

    private String notes;

    /**
     * 上传类型 1:图层链接；2上传文件
     */
    private String uploadType;

    /**
     * 发布状态 0:发布中；1发布成功：2发布失败
     */
    private String publishStatus;

    /**
     * 上传obs地址
     */
    private String obsUrl;

    private String originUrl;
}

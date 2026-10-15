package com.chinatower.device.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.entity.BaseEntity;
import com.chinatower.platform.entity.device.AlgorithmMonitorPointVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "vm_polygon_patch_config")
public class PolygonPatchConfig {

    /**
     * 主键
     */
    @TableId(value = "polygon_id")
    private String polygonId;

    /**
     * 前端排序id  前端生成
     */
    @TableField(value = " monitor_area_id")
    private String  monitorAreaId;

    /**
     * 电子围栏点集合
     */
    @TableField(value = "polygon_points")
    private String polygonPoints;

    /**
     * 设备编码
     */
    @TableField(value = "device_code")
    private String deviceCode;

    /**
     * 通道编码
     */
    @TableField(value = "channel_code")
    private String channelCode;

    /**
     * 预置位id
     */
    @TableField(value = "preset_id")
    private String presetId;

    /**
     * 预置位名称
     */
    @TableField(value = "preset_name")
    private String presetName;

    /**
     * 图层id
     */
    @TableField(value = "layer_id")
    private String layerId;

    /**
     * 图层名称
     */
    @TableField(value = "layer_name")
    private String layerName;

    /**
     * 图斑id
     */
    @TableField(value = "feature_id")
    private String featureId;

    /**
     * 删除标记
     */
    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(value = "industry_code")
    private String industryCode;

    @TableField(value = "app_ver_code")
    private String appVerCode;

    @TableField(value = "tenant_id")
    private String tenantId;


    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @TableField(value = "update_by")
    private String updateBy;

    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    @TableField(value = "polygon_points_lon_lat")
    private String polygonPointsLonLat;

    @TableField(value = "center_longitude")
    private String centerLongitude;

    @TableField(value = "center_latitude")
    private String centerLatitude;

    @TableField(exist = false)
    private String centerPercentX;

    @TableField(exist = false)
    private String centerPercentY;

    @TableField(exist = false)
    private String pan;

    @TableField(exist = false)
    private String tilt;

    @TableField(exist = false)
    private String zoom;


    private String dataScope;


    private String layerIdFeatureIdConcat;

    /**
     * 1-修改 2-未修改 3-新增
     */
    private String isUpdate;

    private String configId;

    private List<AlgorithmMonitorPointVO> polygonPointList;

    /**
     * 电子围栏序号
     */
    private Integer polygonNo;

    /**
     * 电子围栏名称
     */
    private String polygonName;

    /**
     * 图斑bsm
     */
    private String tbbh;

    /**
     * 图层类型名称
     */
    private String layerTypeId;

    /**
     * 图层类型名称
     */
    private String layerTypeName;
}

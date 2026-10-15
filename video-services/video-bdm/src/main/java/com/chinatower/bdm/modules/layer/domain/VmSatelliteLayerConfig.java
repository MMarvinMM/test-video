package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @desription: 卫片执法图层数据
 * @data 创建时间：2024/5/7 11:31
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("vm_satellite_layer_config")
@ApiModel(value="VmSatelliteLayerConfig对象", description="卫片执法图层数据")
public class VmSatelliteLayerConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 告警类型id
     */
    private String alarmTypeId;

    /**
     * 图层模版id
     */
    private String layerTemplateId;

    /**
     * 导入文件地址
     */
    private String fileUrl;

    /**
     * 图层名称
     */
    private String layerName;

    /**
     * 图层命名空间
     */
    private String layerNamespace;

    /**
     * geoserver地址
     */
    private String geoserverUrl;

    /**
     * 自定义图层名称
     */
    private String customName;

    /**
     * 最大x
     */
    private Double maxx;

    /**
     * 最大y轴
     */
    private Double maxy;

    /**
     * 最小x
     */
    private Double minx;

    /**
     * 最小y
     */
    private Double miny;

    /**
     * 图层文件地址
     */
    private String layerFileUrl;

    /**
     * 图层文件名称
     */
    private String layerFileName;

    /**
     * 图层总面积
     */
    private Double layerArea;

    /**
     * 图层图斑数量
     */
    private Integer polygonCount;

    /**
     * geoserver根目录
     */
    private String rootGeoserverUrl;

    /**
     * 几何类型
     */
    private String geoType;

    /**
     * 图层种类 1-矢量 2-非矢量
     */
    private Integer category;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识 0:正常 1:删除
     */
    private String deleteFlag;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 节点编码
     */
    private String acceptNode;
}

package com.chinatower.gis.api.entity;

import com.chinatower.common.core.dto.bdm.LayerPolygonDTO;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @desription: 入参PG 对象
 * @data 创建时间：2023/8/11 15:57
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerPGDTO extends CommonFeignEntity {

    /**
     * 图层类别 0-sys_layer 1-vm_satellite_layer_config
     */
    private int type = 0;
    /**
    * 图层ID
    * @required true
    */
    private String layerId;
    /**
     * 图层类型
     * @required true
     */
    private String layerType;
    /**
     * 图层类型名称
     * @required true
     */
    private String layerTypeName;
    /**
     * 图层名称简称
     * @required true
     */
    private String layerTypeSimple;
    private String initialTypeName;
    /**
     * 图层表如果存在则不创建直接新增入库
     * @required true
     */
    private String pgTableName;
    /**
     * 图层图斑数据
     * @required true
     */
    private List<Map<String,Object>> list;

    private String uuid;
    /**
     * 几何类型
     */
    @ApiModelProperty(value = "几何类型")
    private String geoType;

    @ApiModelProperty(value = "面积字段")
    private String areaField;

    @ApiModelProperty(value = "图层类型字段")
    private List<SysGisFieldsDTO> fields;

    @ApiModelProperty(value = "必填字段")
    private List<SysGisFieldsDTO> mustFields = new ArrayList<>();
}

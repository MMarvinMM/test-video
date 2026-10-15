package com.chinatower.gis.api.entity.map;

import com.chinatower.gis.api.entity.GisBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 行政区域查询
 * @data 创建时间：2023/8/15 15:24
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiAreaQueryReq extends GisBaseReq {
    /**
    * 搜索的关键字，行政区域名称
    * @required true
    */
    @ApiModelProperty(value = "搜索的关键字，行政区域名称")
    private String keyWord;
    /**
     * 指定行政区的国标码（行政区划编码表）严格按照sys_area表
     * @required false
     */
    @ApiModelProperty(value = "指定行政区的国标码（行政区划编码表）严格按照sys_area表")
    private String adCode;

    @ApiModelProperty(value = "1-清楚缓存 0")
    private int hasCache;

    @ApiModelProperty(value = "是否清楚全部缓存")
    private Boolean clearCacheFlag = false;

    @ApiModelProperty(value = "是否需要下级边界数据")
    private Boolean nextPolygonFlag = false;

    /**
     * 清理redis缓存
     */
    private Boolean redisClearFlag = false;
    /**
     * 边界数据是否抽希
     */
    private Integer chouxi = 0;
    /**
     * 抽希阈值
     */
    private double epsilon = 0;
}

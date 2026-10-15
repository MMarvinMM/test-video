package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheAnalysisAllReq extends CommonFeignEntity {
    /**
     *  请求几何的WKT形式表达，坐标系为4326
     * @required false
     * */
    @ApiModelProperty(value = " 请求几何的WKT形式表达，坐标系为4326")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String wkt;
    /**
     * 图形的缓冲区距离，单位米
     * @required false
     * */
    @ApiModelProperty(value = "图形的缓冲区距离，单位米")
    private double distance;
    /**
     * 标识码坐在的图层id
     * @required false
     * */
    @ApiModelProperty(value = "标识码坐在的图层id")
    private String layerID;
    /**
     * 标识码
     * @required false
     * */
    @ApiModelProperty(value = "标识码")
    private String bsm;

    /**
     * 分析类型，默认值为 1，代表“国土”
     * @required false
     * */
    @ApiModelProperty(value = "分析类型")
    private int analysisType = 1;

    /**
     * 查询结果GUID
     * 1、默认为null，
     * 2、当为null时，进行一个新的套合分析。
     * 3、如果不为null，择进行已有guid的继续分析，如果guid不为空且不存在，择报错
     * @required false
     * */
    @ApiModelProperty(value = "查询结果GUID")
    private String guid;

    /**
     * 分析的图层列表，
     * 1、如果为空，则分析默认图层，即各个类型配置的默认套盒分析图层；
     * 2、如果列表缺失类型，则分析默认类型图层；
     * 3、如果类型不存在，则忽略这个错误
     * 4、如果图层不存在，则忽略这个错误
     * */
    private List<TaoheAnalysisLayerItem> layerItems;

}

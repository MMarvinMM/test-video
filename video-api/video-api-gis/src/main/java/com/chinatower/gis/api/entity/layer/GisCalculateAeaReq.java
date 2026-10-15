package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GisCalculateAeaReq {
    public GisCalculateAeaReq() {
        distance = 0;
        inProject = 4326;
        outProject = 4527;
    }
    /**
     * 请求几何的WKT形式表达，默认坐标为4326
     * @required true
     * */
    @ApiModelProperty(value = "请求几何的WKT形式表达，默认坐标为4326")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String wkt;
    /**
     * 图形的缓冲区距离，默认 0
     * */
    @ApiModelProperty(value = "图形的缓冲区距离")
    private double distance;
    /**
     * 输入几何的 EPSG 编码，默认4326
     * */
    @ApiModelProperty(value = "图形的缓冲区距离")
    private int inProject;
    /**
     * 计算面积输出时的 EPSG 编码，4527
     * */
    @ApiModelProperty(value = "图形的缓冲区距离")
    private int outProject;
}

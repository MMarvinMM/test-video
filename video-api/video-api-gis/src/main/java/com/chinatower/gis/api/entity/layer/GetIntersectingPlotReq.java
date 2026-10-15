package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetIntersectingPlotReq extends CommonFeignEntity {
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
     * 分析的图层列表，
     * 如果为空，则分析全部图层；
     * */
    private List<TaoheAnalysisLayerItem> layerItems;

    public String getLayerIds() {
        String ids = null;
        for (TaoheAnalysisLayerItem item : layerItems) {
            if(ids == null )
                ids = "'" + item.getLayerID() + "'";
            else
                ids = ids + ", '" + item.getLayerID() + "'";
        }
        return ids;
    }
}

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
public class GetXjxlFeaturesByIdsReq extends CommonFeignEntity {
    /**
     * 图层ID
     * @required false
     * */
    @ApiModelProperty(value = "图层ID")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String layerID;
    /**
     * 图斑ID数组
     * @required false
     * */
    @ApiModelProperty(value = "图斑ID数组")
    private List<String> tbbhs;

    /**
     * 返回标准里的所有字段，都在只返回部分字段，部分字段为标识码、图斑编号、坐落单位名称、地类名称
     * @required false
     * */
    @ApiModelProperty(value = "返回标准里的所有字段，都在只返回部分字段")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private boolean returnAllCols;

    /**
     * 属性过滤器，置空为不进行属性过滤
     * @required false
     * */
    @ApiModelProperty(value = "返回字段包含Geometry")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private boolean returnGeometry;

    /**
     * 分页数据
     */
    private Integer pageNum;
    /**
     * 分页数据
     */
    private Integer pageSize;
    /**
     * bsm
     */
    private String bsm;
}

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
public class SearchSTBHHXReq extends GetFeaturesByFilterReqEx {
    /**
     * 红线名称
     * @required false
     * */
    @ApiModelProperty(value = "红线名称")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String hxmc;
    /**
     * 红线类型
     * @required false
     * */
    @ApiModelProperty(value = "红线类型")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> hxlx;
    /**
     * 自然保护地级别
     * @required false
     * */
    @ApiModelProperty(value = "自然保护地级别")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> zrbhdjb;
    /**
     * 自然保护地类型
     * @required false
     * */
    @ApiModelProperty(value = "自然保护地类型")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> zrbhdlx;
    /**
     * 自然保护地分区
     * @required false
     * */
    @ApiModelProperty(value = "自然保护地分区")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> zrbhdfq;
}

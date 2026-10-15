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
public class SearcJSYDGZQReq extends GetFeaturesByFilterReqEx {
    /**
     * 图斑编号
     * @required false
     * */
    @ApiModelProperty(value = "管制区类型代码")
    @NotBlank(message = Constants.NOT_EMPTY_PARAM)
    private List<String> gzqlxdm;
}

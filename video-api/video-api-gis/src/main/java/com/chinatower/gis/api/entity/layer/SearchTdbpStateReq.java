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
public class SearchTdbpStateReq extends CommonFeignEntity {
    /**
     * 查询土地报批状态的请求相列表
     * @required false
     * */
    @ApiModelProperty(value = "请求项目列表")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<SearchTdbpStateReqItem> items;
}
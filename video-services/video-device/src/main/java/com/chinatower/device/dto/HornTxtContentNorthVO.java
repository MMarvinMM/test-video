package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 广播文本内容调用南向接口实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HornTxtContentNorthVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区域编码")
    private String regionCode;

    @ApiModelProperty(value = "语速")
    private Integer speechRate;

    @ApiModelProperty(value = "语调音高")
    private Integer tonePitch = 50;

    @ApiModelProperty(value = "文本内容")
    private String txtContent;

    @ApiModelProperty(value = "文本标题")
    private String txtName;

    @ApiModelProperty(value = "音量 0~100数值越大，音量越大")
    private Integer volume;

    @ApiModelProperty(value = "引擎类型")
    private String xunfeiEnt = "intp65";

    @ApiModelProperty(value = "发音人 中英-女:x2_yezi  中英-男:x2_qige")
    private String xunfeiVcn;

    @ApiModelProperty(value = "接入节点")
    private String accessNode;

    @ApiModelProperty(value = "受理节点")
    private String acceptNode;
}

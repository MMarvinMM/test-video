package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LayerIdVo {

    /**
     * 图层ID
     */
    @ApiModelProperty(value = "图层ID")
    private String layerId;

    /**
     * 是否二次识别 1-是；0-否
     */
    private String isSecondIdentify;

    /**
     * 联动摄像机
     */
    private String linkageCamera;

    /**
     * 联动动作 0:抓图 1:抓视频
     */
    private String linkageAction;

    /**
     * 选择算法
     */
    private String selectAlgorithm;
}

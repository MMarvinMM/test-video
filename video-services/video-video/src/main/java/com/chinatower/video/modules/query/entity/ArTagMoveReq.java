package com.chinatower.video.modules.query.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ArTagMoveReq {
    /**
     * 主视频画面，摄像机编码
     */
    @NotBlank(message = "摄像机编码不能为空")
    private String deviceCode;

    /**
     * 主视频画面，通道编码
     */
    @NotBlank(message = "通道编码不能为空")
    private String channelCode;

    /**
     * 需要显示到主视频画面的标签集合
     */
    private List<ArTagMoveVO> arTagInfoReqList;

    /**
     *  按画面百分比坐标显示 0:不按百分比显示 1:按百分比显示
     */
    private String isPercentShow;
}

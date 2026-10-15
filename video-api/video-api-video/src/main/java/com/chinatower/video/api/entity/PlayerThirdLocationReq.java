package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerThirdLocationReq extends VideoBaseReq{
    /**
     * 播放窗口长度像素值
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_WINDOW_LENGTH)
    private int windowLength;
    /**
     * 播放窗口宽度像素值
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_WINDOW_WIDTH)
    private String windowWidth;
    /**
     * 拉框中心的横轴坐标像素值
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_MID_POINTX)
    private int midPointX;
    /**
     * 拉框中心的纵轴坐标像素值
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_MID_POINTY)
    private int midPointY;
    /**
     * 拉框长度像素值
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_LENGTHX)
    private int lengthX;
    /**
     * 拉框宽度像素值
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_LENGTHY)
    private int lengthY;
    /**
     * 缩放标识
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_ZOOM_FLAG)
    private boolean zoomFlag ;


    @Data
    public static class PlayerThirdLocationReqEntity extends CommonResultEntiry {
        private PlayerThirdLocationReq data;
    }

}

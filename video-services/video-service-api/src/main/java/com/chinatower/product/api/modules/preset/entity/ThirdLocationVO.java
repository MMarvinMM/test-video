package com.chinatower.product.api.modules.preset.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName: thirdLocationVO
 * @Description: 3D定位实体
 * @Author: liuxt
 * @Date: 2021-05-11 11:51
 **/
@Data
@ToString
public class ThirdLocationVO {


    //播放窗口长度像素值
    private int windowLength;

    //播放窗口宽度像素值
    private int windowWidth;

    //拉框中心的横轴坐标像素值
    private int midPointX ;

    //拉框中心的纵轴坐标像素值
    private int midPointY ;

    //拉框长度像素值
    private int lengthX ;

    //拉框宽度像素值
    private int lengthY ;

    //缩放标识（true：放大 false:缩小）
    private Boolean zoomFlag ;

    //设备编号
    private String deviceCode;

    //通道编号
    private String channelCode;

    private String customerCode;
    private String userCode;
    private String orderCode;
}

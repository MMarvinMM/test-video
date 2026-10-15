package com.chinatower.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

/**
 * 框信息类（用于多框绘制）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoxInfo {

    /**
     * 多边形框绘制类型（默认）
     */
    public static final String DRAW_TYPE_POLYGON = "polygon";

    /**
     * 绊线绘制类型：一条线 + 一个方向箭头
     */
    public static final String DRAW_TYPE_TRIP_THREAD = "tripThread";

    private List<AlarmPoint> alarmPointList;
    private Color color;
    private int strokeWidth;
    /**
     * 框内展示的文本信息，例如置信度、目标类别等
     */
    private String label;

    private Integer monitorType;

    /**
     * 绘制类型，默认多边形框
     */
    private String drawType = DRAW_TYPE_POLYGON;

    /**
     * 绊线方向点列表（drawType=tripThread 时使用）
     */
    private List<AlarmPoint> directionPointList;

}
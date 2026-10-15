package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * 1.1.2.1 获取指定辖区的UGC用户上报事件列表响应实体类
 */
@Data
public class TrafficUgcListRsp implements Serializable {

    /**
     * 事件ID
     */
    private String id;

    /**
     * 事件类型
     */
    private String type;

    /**
     * 事件类型名称
     */
    private String typeName;

    /**
     * 事件位置点坐标
     */
    private String location;

    /**
     * 省份ID
     */
    private String provinceCode;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市ID
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 事件开始时间，时间戳格式
     */
    private String starttime;

    /**
     * 事件影响车道及程度，字符串，如右侧车道，可能为空
     */
    private String lane;

    /**
     * 道路类型
     */
    private String roadType;

    /**
     * 内容描述
     */
    private String content;

    /**
     * 事件所在道路名称
     */
    private String roadName;

    /**
     * 事件源头语义化
     */
    private String semantic;

    /**
     * 桩号名称
     */
    private String pile;

    /**
     * 用户上传图片URL，若无为null
     */
    private String picUrl;

    /**
     * 方向，如南向北
     */
    private String direction;
}

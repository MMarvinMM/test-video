package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameters {
    /**
     * 起点加密经纬度
     */
    @XmlElement(name = "orig")
    private String orig;

    /**
     * 终点加密经纬度
     */
    @XmlElement(name = "dest")
    private String dest;

    /**
     * 途径点加密经纬度集合
     */
    @XmlElement(name = "mid")
    private String mid;

    /**
     * 经纬度加密的 key 值
     */
    @XmlElement(name = "key")
    private String key;

    /**
     * 地图宽度
     */
    @XmlElement(name = "width")
    private String width;

    /**
     * 地图高度
     */
    @XmlElement(name = "height")
    private String height;

    /**
     * 导航路线类型
     */
    @XmlElement(name = "style")
    private String style;

    /**
     * 版本控制
     */
    @XmlElement(name = "version")
    private String version;

    /**
     * 排序方式
     */
    @XmlElement(name = "sort")
    private String sort;
}

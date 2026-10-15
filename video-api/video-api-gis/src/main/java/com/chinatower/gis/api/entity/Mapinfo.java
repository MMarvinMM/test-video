package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Mapinfo {

    /**
     * 全部结果同时显示的适宜中心经纬度
     */
    @XmlElement(name = "center")
    private String center;

    /**
     * 全部结果同时显示的适宜缩放比例
     */
    @XmlElement(name = "scale")
    private String scale;
}

package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleItem {

    @XmlAttribute(name = "id")
    private Integer id;
    /**
     * 每段线路文字描述
     */
    @XmlElement(name = "strguide")
    private String strguide;

    /**
     * 最后一段道路名称
     */
    @XmlElement(name = "lastStreetName")
    private String lastStreetName;

    /**
     * 当前行驶路段名称（含多个路段）
     */
    @XmlElement(name = "streetNames")
    private String streetNames;

    /**
     * 合并段之间衔接的道路名称
     */
    @XmlElement(name = "strguide")
    private String linkStreetName;

    /**
     * 路牌”引导提示/高速路收费站出口信息
     */
    @XmlElement(name = "signage")
    private String signage;

    /**
     * 道路收费信息(0=免费路段，1=收费路段，2=部分收费路段)
     */
    @XmlElement(name = "tollStatus")
    private String tollStatus;

    /**
     * 转折点经纬度
     */
    @XmlElement(name = "turnlatlon")
    private String turnlatlon;

    /**
     * 线路经纬度
     */
    @XmlElement(name = "streetLatLon")
    private String streetLatLon;

    /**
     * 行驶总距离（单位：米）
     */
    @XmlElement(name = "streetDistance")
    private String streetDistance;
    /**
     * 行合并后的号段，对应详细描述中的号段
     */
    @XmlElement(name = "segmentNumber")
    private String segmentNumber;
}

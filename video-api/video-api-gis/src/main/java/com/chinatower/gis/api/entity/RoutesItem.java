package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RoutesItem {

    @XmlAttribute(name = "id")
    private Integer id;
    /**
     * 每段线路文字描述
     */
    @XmlElement(name = "strguide")
    private String strguide;

    /**
     * “路牌”引导提示/高速路收费站出口信息
     */
    @XmlElement(name = "signage")
    private String signage;

    /**
     * 当前路段名称
     */
    @XmlElement(name = "streetName")
    private String streetName;

    /**
     * 下一段道路名称
     */
    @XmlElement(name = "nextStreetName")
    private String nextStreetName;

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
}

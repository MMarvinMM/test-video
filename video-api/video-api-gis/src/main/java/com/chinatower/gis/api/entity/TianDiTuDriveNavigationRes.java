package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @desription: 天地图驾驶导航
 * @data 创建时间：2023/8/10 9:20
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "result")
public class TianDiTuDriveNavigationRes {

    /**
     * 统计信息
     */
    @XmlElement(name = "parameters")
    private Parameters parameters;

    /**
     * 路由
     */
    @XmlElement(name = "routes")
    private Routes routes;

    @XmlElement(name = "simple")
    private Simple simple;

    /**
     * 全长（单位：公里）
     */
    @XmlElement(name = "distance")
    private String distance;

    /**
     * 行驶总时间（单位：秒）
     */
    @XmlElement(name = "duration")
    private String duration;

    /**
     * 线路经纬度字符串
     */
    @XmlElement(name = "routelatlon")
    private String routelatlon;

    @XmlElement(name = "mapinfo")
    private Simple mapinfo;


}

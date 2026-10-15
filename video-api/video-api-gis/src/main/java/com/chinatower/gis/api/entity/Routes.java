package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @desription: 轨迹路段
 * @data 创建时间：2023/8/23 16:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Routes {
    /**
     * 每个路段信息
     */
    @XmlElement(name = "item")
    private List<RoutesItem> item;
}

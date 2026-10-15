package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 行政区域查询返回
 * @data 创建时间：2023/8/15 15:25
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiAreaQueryRes extends Region{
    /**
     * 当前搜索的名称
     */
    private String name;
    /**
     * 搜索的级别
     */
    private String level;

    private List<District> districts;
}

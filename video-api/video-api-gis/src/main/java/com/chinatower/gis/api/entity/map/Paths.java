package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 路线
 * @data 创建时间：2023/9/8 11:36
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class Paths {

    /**
     * 方案距离，单位：米
     */
    private String distance;
    /**
     * 0 代表限行已规避或未限行，即该路线没有限行路段
     *
     * 1 代表限行无法规避，即该线路有限行路段
     */
    private String restriction;
    /**
     * 路线分段
     */
    private List<Routs> steps;
}

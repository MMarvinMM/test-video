package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 轨迹查询返回
 * @data 创建时间：2023/8/15 15:28
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TrajectoryQueryRes {

    /**
     * 返回的路线总数
     * @required true
     */
    private long count;

    /**
     * 路由
     * @required true
     */
    private List<Paths> routs;
}

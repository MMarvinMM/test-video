package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 半径查询返回
 * @data 创建时间：2023/8/25 16:09
 * @author chuan hao
 * @version 1.0
 *
 */

@Data
public class PoiRadiusQueryResTableInfo {

    /**
     * 关键字数据列表
     *  @required true
     */
    private List<PoiRadiusQueryRes> list;
    /**
     * 总数量
     *  @required true
     */
    private Integer total;
}

package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 关键字查询返回结果
 * @data 创建时间：2023/8/25 15:45
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiKeyWordQueryRes {
   /**
   * 关键字数据列表
   * @required true
   */
    private List<GisPoiKeyWordQueryRes> list;
    /**
     * 总数量
     * @required false
     */
    private int total;
}

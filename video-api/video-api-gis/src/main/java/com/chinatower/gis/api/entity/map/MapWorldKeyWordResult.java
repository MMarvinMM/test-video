package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 天地图关键搜索返回
 * @data 创建时间：2023/8/17 9:23
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldKeyWordResult {

    /**
     * 数量
     */
    private Integer count;
    /**
     * 取值1-5，对应不同的响应类型： 1（普通POI），2（统计），3（行政区)，4（建议词搜索），5（线路结果）
     */
    private String resultType;
    /**
     * 针对统计（类型2）集合返回
     */
    private MapWorldKeyWordResultStatistics statistics;
    /**
     * 针对点（类型1）集合返回
     */
    private List<MapWorldKeyWordResultPois> pois;
    /**
     * 返回状态对象
     */
    private MapWorldKeyWordResultStatus status;
    /**
     * 搜索关键字
     */
    private String keyWord;

    /**
     * 区域
     */
    private MapWorldKeyWordResultArea area;
}

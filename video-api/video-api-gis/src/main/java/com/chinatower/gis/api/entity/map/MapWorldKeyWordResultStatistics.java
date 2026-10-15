package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 天地图搜索返回统计
 * @data 创建时间：2023/8/19 16:57
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldKeyWordResultStatistics {

    /**
     * 推荐行政区名称
     */
    private List<StatisticsPriorityCitys> priorityCitys;
    /**
     * 各省包含信息集合
     */
    private List<StatisticsPriorityCitys> allAdmins;
}

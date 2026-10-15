package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 高德关键字城市
 * @data 创建时间：2023/9/7 16:09
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdKeyWordSuggestion {

    private String keywords;
    /**
     * 推荐城市
     */
    private List<GdKeyWordSuggestionCity> cities;
}

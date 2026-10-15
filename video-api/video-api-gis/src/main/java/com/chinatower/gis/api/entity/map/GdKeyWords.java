package com.chinatower.gis.api.entity.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 高德关键字
 * @data 创建时间：2023/9/5 9:40
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdKeyWords {
    /**
     * 结果状态值，值为0或1 0：请求失败；1：请求成功
     */
    private Integer status;
    /**
     * status为0时，info返回错误原因，否则返回“OK”
     */
    private String info;
    /**
     * 状态码
     */
    @JsonProperty(value = "infocode")
    private String infoCode;
    /**
     * 搜索方案数目
     */
    private Integer count;

    /**
     * 当搜索结果并非是POI（是地址时），且没有搜索到POI时返回
     */
    @JsonProperty(value = "sug_address")
    private String sugAddress;

    /**
     * 响应描述（移动B域能开平台使用）
     */
    @JsonProperty(value = "res_desc")
    private String resDesc;
    /**
     * 推荐城市
     */
    private GdKeyWordSuggestion suggestion;
    /**
     * 信息
     */
    private List<GdKeyWordsPoi> pois;
}

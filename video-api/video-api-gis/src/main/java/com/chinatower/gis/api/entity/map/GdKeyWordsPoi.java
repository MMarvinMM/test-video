package com.chinatower.gis.api.entity.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 关键字POI
 * @data 创建时间：2023/9/5 9:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdKeyWordsPoi {

    /**
     * 唯一ID
     */
    private String id;
    /**
     * 父POI的ID
     */
    private String parent;
    /**
     * 顺序为大类、中类、小类
     *
     * 例如：餐饮服务;中餐厅;特色/地方风味餐厅
     */
    private String type;
    /**
     * 兴趣点类型编码
     */
    @JsonProperty(value = "typecode")
    private String typeCode;
    /**
     * 行业类型
     */
    @JsonProperty(value = "biz_type")
    private String bizType;
    /**
     * 地址
     */
    private String address;
    /**
     * 经纬度
     */
    private String location;
    /**
     * 离中心点距离
     */
    private String distance;
    /**
     * POI的电话
     */
    private String tel;
    /**
     * POI所在省份编码
     */
    private String pcode;
    /**
     * POI所在省份名称
     */
    private String pname;
    /**
     * 城市编码
     */
    private String citycode;
    /**
     * 城市名
     */
    private String cityname;
    /**
     * 区域编码
     */
    private String adcode;
    /**
     * 区域名称
     */
    private String adname;
    /**
     * 别名
     */
    private String alias;
    /**
     *  该POI的特色内容
     */
    private String tag;
    private String name;
    /**
     * 权重
     */
    private String weight;
    /**
     * 评分
     */
    private String score;
    /**
     * 重要性
     */
    private String importance;
    /**
     * 子类型
     */
    private String childtype;
    /**
     * 照片
     */
    private List<Object> photos;
    private List<GdKeyWordsPoiChildren> children;
}

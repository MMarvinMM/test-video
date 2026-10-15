package com.chinatower.gis.api.entity.layer;

import lombok.Data;

import java.util.List;

/**
 * @desription: 图例利用现在的耕地等别 统计
 * @data 创建时间：2024/6/4 15:46
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerXztbGddbAreaRes {

    /**
     * 四等耕地等数据
     */
    public List<LayerXztbGddbAreaChildRes> childRes;

    /**
     * 平均耕地等别
     */
    public String avgGddb;
}

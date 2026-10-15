package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2024/6/4 15:47
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerXztbGddbAreaChildRes {

    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private Integer value;
    /**
     * 统计的面积
     */
    private String area;
    /**
     * 占比 %
     */
    private String ratio;
}

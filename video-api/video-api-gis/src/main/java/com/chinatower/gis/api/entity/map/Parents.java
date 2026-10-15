package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 查询行政区域中上级数据
 * @data 创建时间：2023/8/15 17:23
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class Parents {

    private ParentsChild country;
    /**
     * 省
     */
    private ParentsChild province;
    /**
     * 城市
     */
    private ParentsChild city;
}

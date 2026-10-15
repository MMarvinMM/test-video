package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/8/15 17:24
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ParentsChild {

    /**
     * 区域类型
     */
    private String adminType;
    /**
     * 区域编码
     */
    private String cityCode;
    /**
     * 名称
     */
    private String name;
}

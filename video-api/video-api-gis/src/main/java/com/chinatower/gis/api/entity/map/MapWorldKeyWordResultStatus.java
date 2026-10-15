package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 返回状态对象
 * @data 创建时间：2023/8/17 9:25
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldKeyWordResultStatus {
    /**
     * 返回状态描述
     */
    private String cndesc;
    /**
     * 返回状态服务状态码表 1000 成功
     */
    private Integer infocode;

}

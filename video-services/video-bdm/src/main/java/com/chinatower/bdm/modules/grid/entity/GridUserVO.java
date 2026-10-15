package com.chinatower.bdm.modules.grid.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 根据网格查下面的用户实体
 * @Author: hued
 * @Date: 2023年2月7日10:17:16
 */
@Data
public class GridUserVO implements Serializable {

    private static final long serialVersionUID = -4379048213120041725L;
    /**
     * 用户编码
     */
    private String userId;
    /**
     * 网格编码
     */
    private String gridId;
}

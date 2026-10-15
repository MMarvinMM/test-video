package com.chinatower.platform.dao;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/12/14 16:15
 */
@Data
public class LocalRequest {

    /**
     * 节点编号
     */
    private String colonyCode;

    /**
     * 日期
     */
    private String dateString;
}

package com.chinatower.receive.vo.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/12/20 11:23
 */

@Data
public class AndTuleVo {

    /**
     * 0:符合  1：不符合
     */
    private List<String> flag;
    private OrderCustomerVO customerVO;
}

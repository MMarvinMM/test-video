package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneFactoryVO{

    /**
     * 前端因子回显名称
     */
    private String itemName;

    /**
     * 前端因子回显名称值
     */
    private String itemCode;

}

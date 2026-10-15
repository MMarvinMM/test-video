package com.chinatower.platform.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/10 15:30
 */

@Data
public class WarningOrderResp {

    private List<WarningOrderEntity> list;

    private long total;

    private long realTotal;
}

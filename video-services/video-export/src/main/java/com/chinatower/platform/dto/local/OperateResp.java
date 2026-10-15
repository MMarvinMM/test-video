package com.chinatower.platform.dto.local;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/10 15:30
 */

@Data
public class OperateResp {

    private List<OperateOrderEntity> list;

    private long total;
}

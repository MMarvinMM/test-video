package com.chinatower.order.vo;

import com.chinatower.common.entity.Result;
import lombok.Data;

@Data
public class OrderAddRsp {
        private String packageName;
        private Result result;
}

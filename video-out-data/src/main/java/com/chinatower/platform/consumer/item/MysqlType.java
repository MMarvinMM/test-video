package com.chinatower.platform.consumer.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 目标实时动态数据监视数据对象
 * Created by wanglei on 2022/3/31.
 */
@Data
@Getter
@Setter
public class MysqlType {
    private String id;
    private String commodity_name;
    private String commodity_price;
    private String number;
    private String description;
}

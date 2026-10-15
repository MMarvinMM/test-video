package com.chinatower.platform.consumer.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 目标实时动态数据监视数据对象
 * Created by wanglei on 2022/3/31.
 */
@Data
@Getter
@Setter
public class SqlType {
    private int id;
    private int commodity_name;
    private int commodity_price;
    private int number;
    private int description;
}



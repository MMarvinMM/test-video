package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/3 11:07
 */
@Data
public class PageInfoModel<T> {
    private int pageNum;
    private int pageSize;
    private long total;
    private List<T> list;
}

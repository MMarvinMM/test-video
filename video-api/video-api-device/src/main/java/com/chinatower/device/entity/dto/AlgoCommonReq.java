package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class AlgoCommonReq<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 事件编码 */
    private String eventId;

    /** 数据 */
    private transient List<T> data;
}

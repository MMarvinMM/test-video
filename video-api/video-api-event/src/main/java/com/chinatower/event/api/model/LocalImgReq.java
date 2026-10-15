package com.chinatower.event.api.model;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/14 20:25
 */
@Data
public class LocalImgReq {

    /**
     * 工单号
     */
    private String warningOrderId;

    /**
     * 属地化节点code
     */
    private String platformCode;

    /**
     * 图片地址(所有图片字段)
     */
    private List<String> picUrl;

    /**
     * 告警来源
     */
    private String warningSource;
}

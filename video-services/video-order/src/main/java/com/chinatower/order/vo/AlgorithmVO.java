package com.chinatower.order.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 算法信息
 */
@Data
@ToString
public class AlgorithmVO implements Serializable {
    //订购编码
    private String orderCode;

    //客户编码
    private String customerCode;

    //算法服务编码
    private String algorithmCode;

    //算法服务名称
    private String algorithmName;

    //上下架状态
    private String algorithmShape;

    //套餐编码
    private String packageCode;

    //订单状态：0变更;1新增订购;2订单续订、启用;3退订;4停用、失效
    private Integer ordStatus;
    //订单状态：0变更;1新增订购;2订单续订、启用;3退订;4停用、失效
    private Integer ordStatusBofore;

    //0:失效 1:有效
    private String deleteFlag;
    //0:失效 1:有效
    private String deleteFlagBofore;

    //设备编码
    private String deviceCode;
}
package com.chinatower.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 * 客户编号
 * @return
 * @author chenmf
 * @date 2022/2/10
 * @version
 */
@Data
public class AlgorithmByOrderVO implements Serializable {
    // 订单编号
    private String orderCode;
    // 客户编号
    private String customerCode;
    //订单状态：0变更;1新增订购;2订单续订、启用;3退订;4停用、失效
    private int ordStatus;
    // 设备编号
    private List<String> deviceCodeList;
    //算法集合
    private List<AlgorithmVO> algorithmCodeList;

    private Integer deleteFlag;
    //状态：0:停用 1:启用
    private String status;

    private String industryCode;

    private List<String> industryCodeList;

    private String appVerCode;

}

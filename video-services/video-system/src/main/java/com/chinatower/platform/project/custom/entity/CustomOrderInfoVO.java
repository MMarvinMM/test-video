package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: com.chinatower.product.cust.modules.systemservice.entity
 * @description: TODO
 * @author: zhaol@strongdata.com.cn
 * @create: 2021/9/18 14:06
 */
@ToString
@Data
public class CustomOrderInfoVO {

    private String orderCode;
    private String ordStatus;
    private String orderTime;
    private String subscriber;
    private String validTime;
    private String invalidTime;
    private String customerCode;
    private List<CustomOrderPackageVO> packageData;

}

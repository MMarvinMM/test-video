package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: com.chinatower.product.cust.modules.systemservice.entity
 * @description: TODO
 * @author: zhaol@strongdata.com.cn
 * @create: 2021/9/18 14:09
 */
@ToString
@Data
public class CustomOrderPackageVO {

    private String packageId;
    private String packageCode;
    private String packageName;
    private String description;
    private String storageTime;
    private String packageOrderCode;
    private List<CustomOrderApplicationVO> appData;
    private List<OrderServicesVO> serviceData;
    private List<CustomOrderAlgorithmsVO> algorithmData;
    private List<CustomOrderAlgorithmsVO> giftAlgorithmData;
    private List<CustomOrderDevicesVO> deviceData;

}

package com.chinatower.platform.entity.order;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;


/**
 * @ClassName OrderInfoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/27 15:56
 * @Version 1.0
 **/
@Data
@Alias("orderInfo")
public class OrderInfoVO implements Serializable {
    
    private String orderCode;
    private int ordStatus;
    private String orderTime;
    private String subscriberCode;
    private String validTime;
    private String invalidTime;
    private String provinceName;
    private String cityName;
    /**
     * 节点编号
     */
    private String locationCode;

    /**
     * 节点名称
     */
    private String locationName;
    private List<OrderPackageVO> packageData;
}

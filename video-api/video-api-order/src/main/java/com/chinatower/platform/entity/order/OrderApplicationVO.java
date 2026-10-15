package com.chinatower.platform.entity.order;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @ClassName OrderInfoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/27 15:56
 * @Version 1.0
 **/
@Data
@ToString
@Alias("orderApplication")
public class OrderApplicationVO implements Serializable {
    
    private String appCode;
    private String productName;
    private String productCode;
    private String capabilitiesStr;

}

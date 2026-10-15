package com.chinatower.platform.entity.order;

import lombok.Data;
import lombok.ToString;
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
@ToString
@Alias("orderDevices")
public class OrderDevicesVO implements Serializable {
    
    private String deviceCode;
    private String deviceName;
    private String siteName;
    private String location;
    private String longitude;
    private String latitude;

    private String orderCode;

    private String packageCode;

    //关联算法信息列表
    private List<OrderAlgorithmConfigVO> algorithmConfigList;

}

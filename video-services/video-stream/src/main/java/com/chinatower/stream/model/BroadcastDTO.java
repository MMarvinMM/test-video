package com.chinatower.stream.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("广播设备列表模型")
public class BroadcastDTO {
    
    private String id;
    private String code;
    private String latitude;
    private String longitude;
    private String name;
    private String status;
    
}

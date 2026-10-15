package com.chinatower.stream.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("广播参数模型")
public class BroadcastVO {

    private Integer level;
    
    private Integer volume;
    
    private List<String> targets;
    
    private Integer emergency;
    
    private Long endDate;
    
    private String text;

    private String rtsp;
}

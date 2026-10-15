package com.chinatower.device.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ChannelStorageTimeVO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/13 19:46
 * @Version 1.0
 **/
@Data
public class ChannelStorageTimeVO {
    
    private String recordPlanId;
    
    private String customerCode;
    
    private String deviceCode;
    
    private String channelCodesStr;
    
    private String packageCode;
    
    private String storageTime;
    
    private String orderCode;
    
    private String storageDay;
    
    private List<ChannelStorageTimeVO> channelStorageTimeVOList;
    
    private List<ChekcChannelVideoPlanVO> channelVideoPlanVOList;
    
    //设备名称
    private String devName;
    
    //通道名称
    private String channelName;
    
    //来源
    private String source;

    private String industryCode;
}

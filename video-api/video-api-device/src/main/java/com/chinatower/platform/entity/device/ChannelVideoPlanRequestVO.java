package com.chinatower.platform.entity.device;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName: ChannelVideoPlanRequestVO
 * @Description: 通道录像计划实体
 * @Author: chenmf
 * @Date: 2022-01-24 11:51
 **/
@Data
@ToString
public class ChannelVideoPlanRequestVO {
    
    @Valid
    private List<ChannelVideoPlanVO> recordPlanList;

    private String creator;

}

package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ReservePlanVO
 * @Deacription
 * @Author chenmf
 * @Date 2022/4/19 9:36
 * @Version 1.0
 **/
@Data
public class ReservePlanVO implements Serializable {
    
    //共有预案
    List<ReservePlanDTO> publicPlanList;
    
    //私有预案
    List<ReservePlanDTO> privatePlanList;
}

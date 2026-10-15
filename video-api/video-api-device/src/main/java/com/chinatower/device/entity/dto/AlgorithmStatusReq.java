package com.chinatower.device.entity.dto;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @ClassName AlgorithmStatusReq
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/24 16:48
 * @Version 1.0
 **/
@Data
public class AlgorithmStatusReq implements Serializable {
    

    private String eventId;
    
    @Valid
    private AlgoStatusReq data;
}

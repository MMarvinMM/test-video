package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @ClassName OrderRelationshipDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/18 10:14
 * @Version 1.0
 **/

@Data
public class OrderRelationshipDTO implements Serializable {
    
    private String eventId;
    
    @Valid
    private OrderGatewayDTO data;
    
}

package com.chinatower.receive.vo.dto;

import lombok.Data;

@Data
public class TypeCountDTO {

    /**
     * AI研判结果,true:真警、false：假警、unjudged：无法判断
     */
    private String type;

    private Integer count;
}

package com.chinatower.device.dto;

import lombok.Data;

@Data
public class QueryDTO {

    private String flag;

    private String day;

    private Integer pageNum;

    private Integer pageSize;

}

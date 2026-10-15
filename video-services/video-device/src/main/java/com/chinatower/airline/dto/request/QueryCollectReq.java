package com.chinatower.airline.dto.request;

import lombok.Data;

@Data
public class QueryCollectReq {
    //img  video
    private String queryType;
    //用户id
    private String userId;
    //行业
    private String industryCode;
}

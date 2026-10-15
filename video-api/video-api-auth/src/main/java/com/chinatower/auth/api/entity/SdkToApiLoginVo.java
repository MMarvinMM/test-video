package com.chinatower.auth.api.entity;

import lombok.Data;

@Data
public class SdkToApiLoginVo {
    /**
     * 用户Id
     *
     */
    private String userId;

    /**
     * 行业Id
     */
    private String tradeId;

    /**
     * 行业版本编码
     */
    private String appVerCode;


}

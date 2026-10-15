package com.chinatower.platform.entity.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @auther liuhuanhuan
 * @date 2022/3/17
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysTenantTrade implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 版本号
     */
    private String appVerCode;

    /**
     * 行业名称
     */
    private String industryName;

}

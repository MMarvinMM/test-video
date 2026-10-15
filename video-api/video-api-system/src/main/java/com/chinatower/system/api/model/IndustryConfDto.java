package com.chinatower.system.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 行业信息dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndustryConfDto implements Serializable {

    private String industryId;

    private String industryCode;

    private String industryName;

    /**
     * 行业描述
     */
    private String industryDesc;

    /**
     * 1生效 0失效
     */
    private String status;

    /**
     * 行业应用图标
     */
    private String imgUrl;

    /**
     * 接口配置 0-API 1-SDK 2-未配置
     */
    private String apiIntfConfigType;

    private String appKey;

    private String secretKey;

    /**
     * 移动端应用图标地址
     */
    private String mobileAppIconUrl;

    /**
     * 登录展示图地址
     */
    private String loginDisplayImgUrl;

    /**
     * 产品简介信息
     * 多个逗号分隔
     */
    private String productBriefInfo;

    /**
     * 产品顺序
     */
    private Integer productSeq;

    /**
     * 订购状态 0-订购 1-未订购
     */
    private Integer orderStatus;

    private String appVerCode;

    private String username;

    private String tenantId;

    private String tenantName;

    private String industryTypeId;

    private String industryTypeName;

}

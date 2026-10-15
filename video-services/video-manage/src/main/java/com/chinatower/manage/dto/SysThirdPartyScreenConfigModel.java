package com.chinatower.manage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class SysThirdPartyScreenConfigModel implements Serializable {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 大屏名称
     */
    private String screenName;
    /**
     * 大屏地址
     */
    private String screenUrl;
    /**
     * appKey
     */
    private String appKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 更新人
     */
    private String modifier;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 删除状态：0正常 1删除
     */
    private Integer deleteFlag;

    private Map<String,String> headerMap;

    /**
     * 大屏别名
     */
    private String screenAlias;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

}

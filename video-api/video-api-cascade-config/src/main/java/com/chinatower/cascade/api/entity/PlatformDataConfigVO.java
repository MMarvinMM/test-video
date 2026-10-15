package com.chinatower.cascade.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformDataConfigVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = -4379048213120041725L;
    /**
     * 上级属地编码
     */
    private String platformParentCode;
    /**
     * 当前属地名称
     */
    private String platformName;
    /**
     * 属地编码
     */
    private String platformCode;
    /**
     * 当前属地网关地址
     */
    private String gatewayUrl;
    /**
     * 当前属地ofs公网地址
     */
    private String ofsUrl;
    /**
     * 属地编码层级
     */
    private String platformLevel;
    /**
     * 行政区域编码
     */
    private String admCode;
    /**
     * 管理区域编码
     */
    private String areaCode;

    /**
     * 上级编码路径
     */
    private String codePath;
    /**
     * AK
     */
    private String appKey;
    /**
     * SK
     */
    private String secretKey;

    /**
     * 通用平台访问地址
     */
    private String generalUrl;
}

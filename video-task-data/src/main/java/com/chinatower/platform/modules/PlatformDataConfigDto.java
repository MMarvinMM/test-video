package com.chinatower.platform.modules;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/6 14:16
 */
@Data
public class PlatformDataConfigDto {

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 平台级别 0:中心节点 1:省份节点 2:地市节点
     */
    private String platformLevel;

    /**
     * 父级平台编码
     */
    private String platformParentCode;

    /**
     * 网关地址
     */
    private String gatewayUrl;

    /**
     * 通用平台地址
     */
    private String generalUrl;

    /**
     * ofs地址
     */
    private String ofsUrl;

    /**
     * ak
     */
    private String appkey;

    /**
     * sk
     */
    private String secretkey;

    /**
     * 行政区域编码
     */
    private String admCode;

    /**
     * 管理区域编码
     */
    private String areaCode;
}

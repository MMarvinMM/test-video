package com.chinatower.platform.model.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
@Data
public class CascadeConfigSyncVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = -4379048213120041725L;
    /**
     * 上级属地编码
     */
    private String parentCode;
    /**
     * 当前属地名称
     */
    private String colonyName;
    /**
     * 属地编码
     */
    private String colonyCode;
    /**
     * 当前属地网关地址
     */
    private String gwAddr;
    /**
     * 当前属地ofs公网地址
     */
    private String ofsAddr;
    /**
     * 属地编码层级
     */
    private String level;
    /**
     * 上级编码路径
     */
    private String codePath;
    /**
     * 行政区域编码
     */
    private String admCode;
    /**
     * 管理区域编码
     */
    private String areaCode;
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

    /**
     * 外网地址
     */
    private String programUrl;

    /**
     * 小程序版本
     */
    private String miniProgramVersion;

    /**
     * wgt版本
     */
    private String wgtVersion;

    /**
     * 属地版本
     */
    private String colonyVersion;

    /**
     * 别名
     */
    private String nodeAlias;

}

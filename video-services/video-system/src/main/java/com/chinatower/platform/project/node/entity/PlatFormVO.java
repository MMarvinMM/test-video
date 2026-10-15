package com.chinatower.platform.project.node.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class PlatFormVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -4379048213120041725L;
    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 通用平台访问地址
     */
    private String generalUrl;

    /**
     * 平台级别 0:中心节点 1:省份节点 2:地市节点
     */
    private String platformLevel;

    private String programUrl;
}

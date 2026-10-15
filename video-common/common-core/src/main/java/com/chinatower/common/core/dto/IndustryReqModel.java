package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 行业请求参数接收类
 * @author chmod
 * @version 1.0
 * @date 2022/3/16 21:27
 */
@Data
public class IndustryReqModel implements Serializable {
    private static final long serialVersionUID = -6446304868597169528L;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;

    /**
     * 版本号
     * @required true
     */
    private String version;
}

package com.chinatower.platform.entity.gis;

import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2024/9/24 16:24
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysGisFieldsDictDTO {
    /**
     * 类型代码
     */
    private String abbreviation;
    /**
     * 字段
     */
    private String filterType;
    /**
     * 字典 key
     */
    private String filterAttrKey;
    /**
     * 字典值
     */
    private String filterAttrValue;
}

package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @desription: 图层字段配置字典值
 * @data 创建时间：2024/1/22 14:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerTypeAttrFilterVO {

    /**
     * 传了ID就是修改 没传就是新增
     */
    private String id;
    /**
     * 筛选属性编号
     */
    private String code;
    /**
     * 筛选属性编号
     */
    private String filterAttrKey;

    /**
     * 筛选属性值
     */
    private String filterAttrValue;

}

package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.vo.BaseGridEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 网格不分页vo
 * @author zl
 */
@Data
@ToString
public class GridNoPageVO{

    /**
     * 网格名称
     * @required true
     */
    private String gridName;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;


    /**
     * 客户编码
     * @required true
     */
    private String tenantId;

    private List<String> gridNameList;

    private List<String> gridIdList;

    /**
     * 省份编码
     */
    private String provinceId;

    /**
     * 地市编码
     */
    private String cityId;
}

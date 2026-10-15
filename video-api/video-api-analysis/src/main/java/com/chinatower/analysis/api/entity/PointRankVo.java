package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointRankVo extends BaseDataScopeEntity implements Serializable {

    private Integer topN;

    private String appVerCode;

    private String industryCode;

    private String tenantId;

    private Integer pageSize;

    private Integer pageNum;

    /**
     * 是否为参考点 0否1是
     */
    private String pointFlag;
    /**
     * 省市区编码集合
     */
    private List<String> areaCodeLists;
}

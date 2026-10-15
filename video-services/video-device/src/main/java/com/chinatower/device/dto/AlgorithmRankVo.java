package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlgorithmRankVo extends BaseDataScopeEntity implements Serializable {

    private Integer topN;

    /**
     * 0:停用 1:启用
     */
    private String status;

    private String appVerCode;

    private String industryCode;

    private String tenantId;

    private Integer pageSize;

    private Integer pageNum;

}

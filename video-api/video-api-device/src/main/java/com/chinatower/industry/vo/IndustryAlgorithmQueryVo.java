package com.chinatower.industry.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndustryAlgorithmQueryVo extends BaseDataScopeEntity implements Serializable {
    /**
     * 租户编码
     * @required true
     */
    private String tenantId;
    /**
     * 行业编码
     * @required true
     */
    private String industryCode;
    /** 用户编码 */
    private String userId;
    /** 设备编码 */
    private String deviceCode;
    /** 页数 */
    private Integer pageNum;
    /** 分页数 */
    private Integer pageSize;

    /**
     * 设备编码集合
     * @required true
     */
    @NotEmpty
    private List<String> deviceCodeList;

    /**
     * 应用场景:1视联场景;2无人机场景
     */
    private String appScenarios;

}

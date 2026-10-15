package com.chinatower.platform.dto;

import com.chinatower.common.core.web.page.PageDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 统计查询
 * @data 创建时间：2023/3/28 16:01
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LogStatisticsQuery extends PageDomain {
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String beginTime;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endTime;
    /**
     * 租户搜索
     */
    @ApiModelProperty("客户搜索")
    private String tenantKey;
    /**
     * 组织名称
     */
    @ApiModelProperty("组织ID")
    private String deptId;
}

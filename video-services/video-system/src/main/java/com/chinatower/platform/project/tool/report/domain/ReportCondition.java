package com.chinatower.platform.project.tool.report.domain;

import com.chinatower.common.core.web.domain.BaseEntity;
import lombok.Data;

/**
 * 动态报表查询条件
 * @author WANGZIC
 */
@Data
public class ReportCondition extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Integer pageSize;

    private Integer pageNum;

    private Integer reportId;

    private String reportSql;

    private String ifDataFilter;

    private String datasourceType;

    private String dataScope;
}

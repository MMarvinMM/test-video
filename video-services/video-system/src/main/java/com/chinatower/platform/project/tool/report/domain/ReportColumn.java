package com.chinatower.platform.project.tool.report.domain;

import lombok.Data;

/**
 * @author WANGZIC
 */
@Data
public class ReportColumn {

    private String columnKey;

    private String columnName;

    private String conditionType;

    private String ifShow;

    private String ifFormat;

    private String formatMethod;

    private String formatKey;

    private Integer columnSort;

    private Integer columnWidth;
}

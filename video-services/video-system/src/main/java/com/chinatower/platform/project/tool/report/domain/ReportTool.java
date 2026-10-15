package com.chinatower.platform.project.tool.report.domain;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
/**
 * 系统报表配置对象 tb_report_config
 * 
 * @author wangzic
 * @date 2020-02-14
 */
@Data
public class ReportTool extends BaseDataScopeEntity {
    private static final long serialVersionUID = 1L;

    /** 报表编码 */
    private Integer id;

    /** 报表名称 */
    @Excel(name = "报表名称")
    private String reportName;

    /** 报表类型 */
    @Excel(name = "报表类型")
    private String reportType;

    /** 报表描述 */
    @Excel(name = "报表描述")
    private String reportDesc;

    /** 报表SQL */
    @Excel(name = "查询SQL")
    private String reportSql;

    @Excel(name = "是否权限过滤", readConverterExp = "0=否,1=是")
    private String ifDataFilter;

    @Excel(name = "数据源类型")
    private String datasourceType;

    /** 查询条件（json） */
    @Excel(name = "查询条件")
    private String reportConditions;

    /** 查询字段（json） */
    @Excel(name = "查询字段")
    private String reportColumns;

    /** 删除标志（0未删除 2已删除） */
    private String delFlag;

    /** 创建者所属部门ID */
    private String deptId;

}
